package Unit;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.nio.charset.Charset;

class QrCode{
    // 要编码的数据
    String data;
    // 模式
    Data.MODE mode;
    // 纠错级别
    Data.LEVEL level;
    // 版本设置
    int version;
    // 数据长度
    int dataLen;
    // 数据编码
    String dataEncodeStr;
    // 数据码字
    int dataCodewords[][];
    // 最终的数据字符串
    String message;
    // 矩阵
    int dataMatrix[][];

    private int eccAndBlocksIndex() {
        int index = (this.version-1)*4 + this.level.ordinal();
        return index;
    }
    
    public QrCode(String data, Data.LEVEL l) throws Exception{
        this.data = data;
        this.level = l;
        this.dataLen = this.data.length();
        // 选择合适的模式
        chooseMode();
        // 确定最小版本
        chooseVersion();
        // 数据编码
        dataEncoding();
        // 生成最终的数据字符串
        structFinalMessage();

        // 模块在矩阵中的位置 （包含数据信息和功能模块）
        modulePlacementInMatrix();
        
        // 数据掩码
        dataMasking();
        // System.out.println(this.message);
        
    }

    public QrCode(String data) throws Exception{
        this(data, Data.LEVEL.L);
    }

    /**
     * 检测和设置模式 
     */
    protected void chooseMode(){
	    if(Pattern.compile("^[0-9]+$").matcher(this.data).matches()){
	        this.mode = Data.MODE.Numeric;
	    }else if(Pattern.compile("^[0-9A-Z $%*+-./:]+$").matcher(this.data).matches()){
            this.mode = Data.MODE.Alphanumeric;
        }else{
            this.mode = Data.MODE.Byte; // Byte 和 Kanji 数据都用Byte
            this.dataLen = this.data.getBytes().length;
        }
        
        /*
        else if(Charset.forName("ISO-8859-1").newEncoder().canEncode(this.data)){
            this.mode = Data.MODE.Byte;
        }else if(Charset.forName("UTF-8").newEncoder().canEncode(this.data)){
            this.mode = Data.MODE.Kanji;
        }
        */
    }

    /**
     * 选择合适的版本
     * @throws Exception
     */
    protected void chooseVersion() throws Exception {
        // 已经确定了容错级别和模式，再加上字符长度，选择一个最小的版本
        int levelIndex = this.level.ordinal();
        int modeIndex = this.mode.ordinal();
        this.version = 0;
        for(int i=1; i<Data.CharacterCapacities.length; i++){ // 版本 1-40
            if(Data.CharacterCapacities[i][levelIndex][modeIndex] >= this.dataLen){
                this.version = i;
                break;
            }
        }
        if(this.version>40 || this.version<1){
            throw new Exception("The data is too long.");
        }
    }

    /**
     * 数据编码
     */
    protected void dataEncoding(){
        StringBuffer dataE = new StringBuffer();
        int modeIndex = this.mode.ordinal();
        
        // mode 指示器
        dataE.append(Data.ModeIndicator[modeIndex]);
        
        // 将数据长度编码。从字符计数指示器 获取到对应宽度，将数据长度转二进制并补0
        int dciA[] = Data.CharacterCountIndicator(this.version);
        int dci = dciA[this.mode.ordinal()];
        dataE.append(Common.formatBin(dci, this.dataLen));
        
        // 根据模式进行编码
        String encodeStr = new EncodeData(this).getCode();
        dataE.append(encodeStr);

        // System.out.println("前缀和根据模式进行数据编码：\n" + dataE.toString());

        // 补齐编码
        this.makeupCode(dataE);

        this.dataEncodeStr = dataE.toString();
    }

    /**
     * 补齐编码
     * @param bs
     */
    private void makeupCode(StringBuffer bs){
        // 获取数据码总数

        int index = this.eccAndBlocksIndex();
        int dcTotal = Data.ECC_AND_BLOCKS[index][Data.ECC_AND_BLOCKS_COLUMN.DC_TOTAL.ordinal()];
        int requireBits = dcTotal * 8;

        // 补上最多4位的0
        int terminator = requireBits-bs.length()>=4 ? 4 : requireBits-bs.length();
        for(int i=0; i < terminator; i++) bs.append('0');
        // 补齐8位
        for(int i=0; i < bs.length()%8; i++) bs.append('0');
        // 剩余的位置添加pad字节
        for(int i=0; bs.length()<requireBits; i++) bs.append(Data.PAD[i%2]);
    }

    /**
     * 生成和格式化最终数据信息
     */
    protected void structFinalMessage(){
        // 生成数据码字
        this.dataCodewords = DataCodewords.getDC(this.dataEncodeStr, this.version, this.level);
        
        // 交错数据码字
        int[] iDc = Common.interleaveData(this.dataCodewords);

        // 生成纠错码
        int ecc[][] = new int[this.dataCodewords.length][];
        for(int i=0; i<this.dataCodewords.length; i++){
            ecc[i] = ErrorCorrectionCoding.getCode(this.dataCodewords[i], Data.ECC_AND_BLOCKS[this.eccAndBlocksIndex()][Data.ECC_AND_BLOCKS_COLUMN.ECC_PER_BLOCK.ordinal()]);
        }

        // 交错纠错码
        int[] iEcc = Common.interleaveData(ecc);

        // 数据码和交错码拼接
        int[] messageArr = Common.joinArr(iDc, iEcc);

        // 转为二进制
        StringBuffer message = new StringBuffer();
        for(int i : messageArr){
            message.append(Common.formatBin(8, i));
        }

        // 添加0位
        int count = Data.VersionsRequiredRemainderBits[this.version];
        for(int i=0; i<count; i++){
            message.append('0');
        }

        this.message = message.toString();
    }

    /**
     * 模块在矩阵中的位置 （包含数据信息和功能模块）
     * 模块和像素有区别：二维码中的黑白块都是模块，绘制的时候一个块可能占用了多个像素
     * 功能模式是二维码规范要求的二维码非数据元素，如二维码矩阵四角的三种查找模式
     * 
     */
    protected void modulePlacementInMatrix(){
        
        int size = Data.getSize(this.version);
        // 初始化
        this.dataMatrix = new int[size][size];
        for(int i=0; i<this.dataMatrix.length; i++){
            for(int j=0; j<this.dataMatrix[i].length; j++){
                this.dataMatrix[i][j] = -1;
            }
        }

        // 添加查找模式 (Finder patterns)
        Common.setMatrix(this.dataMatrix, 0, 0, Data.FinderPatterns); // top-left
        Common.setMatrix(this.dataMatrix, 0, size-7, Data.FinderPatterns); // top-right
        Common.setMatrix(this.dataMatrix, size-7, 0, Data.FinderPatterns); // bottom-left
        printImage("1FP");

        // 添加分隔符 (Seperators)
        Common.setMatrix(this.dataMatrix, 7, 0, Data.SeperatorsRow);
        Common.setMatrix(this.dataMatrix, 0, 7, Data.SeperatorsColumn);
        Common.setMatrix(this.dataMatrix, 7, size-8, Data.SeperatorsRow);
        Common.setMatrix(this.dataMatrix, 0, size-8, Data.SeperatorsColumn);
        Common.setMatrix(this.dataMatrix, size-8, 0, Data.SeperatorsRow);
        Common.setMatrix(this.dataMatrix, size-8, 7, Data.SeperatorsColumn);
        printImage("2S");
        
        // 添加对齐模式 (Alignment Patterns)
        int apPosition[][] = Data.AlignmentPatternPosition(this.version);
        for(int apl[]: apPosition){
            boolean b = Common.setMatrixExcept(this.dataMatrix, apl[0]-2, apl[1]-2, Data.AlignmentPatterns);
            // System.out.printf("x,y: %d %d %b\n", apl[0]-2, apl[1]-2, b);
        }
        printImage("3AP");

        // 添加节奏模式 (Timing Patterns)
        int tpRow[][] = new int[1][size-16];
        int tpColumn[][] = new int[size-16][1];
        for(int i=0; i<size-16; i++){
            tpRow[0][i] = i%2==0 ? 1 : 0;
            tpColumn[i][0] = i%2==0 ? 1 : 0;
        }
        Common.setMatrix(this.dataMatrix, 6, 8, tpRow);
        Common.setMatrix(this.dataMatrix, 8, 6, tpColumn);
        printImage("4TP");

        // 添加dark模块和预留区域 (Dark Module and Reserved Areas)
        // dark模块 [(4 * this.version) + 9, 8]
        Common.setMatrix(this.dataMatrix,  4*this.version+9, 8, new int[][]{{1}});
        printImage("501DM");
        // 格式信息区域 (Format Information Area)
        // 为了防止数据太分散，包含之前的数据
        // 左上角的右侧和底部
        int FIA01[][] = Common.genMatrix(1,9,2); //[2,2,2,2,2,2,1,2,2]
        int FIA02[][] = Common.genMatrix(9,1,2); // [[2],...[1],[2],[2]]
        FIA01[0][6] = 1;
        FIA02[6][0] = 1;
        Common.setMatrixExcept(this.dataMatrix, 8, 0, FIA01);
        Common.setMatrixExcept(this.dataMatrix, 0, 8, FIA02);
        // 右上角的底部
        int FIA03[][] = Common.genMatrix(1,9,2);
        Common.setMatrixExcept(this.dataMatrix, 8, this.version*4+9, FIA03);
        // 左下角的右侧
        int FIA04[][] = Common.genMatrix(8,1,2);
        Common.setMatrixExcept(this.dataMatrix, 4*this.version+10, 8, FIA04);
        // 版本信息区域 （Version Information Area）
        if(this.version>=7){
            int VIA01[][] = Common.genMatrix(6, 3, 3);
            int VIA02[][] = Common.genMatrix(3, 6, 3);
            Common.setMatrixExcept(this.dataMatrix, 0, 4*this.version+6, VIA01);
            Common.setMatrixExcept(this.dataMatrix, 4*this.version+6, 0, VIA02);

        }
        printImage("502FIA");
        // 添加数据信息
        addDataBitsToMatrix();
        printImage("6data");
    }

    /**
     * 添加数据到图像中
     */
    private void addDataBitsToMatrix(){
        int i=0; // 数据索引
        int messageArr[] = Common.strSplitToInt(this.message, 1);

        int column = this.dataMatrix.length-1;
        int row = this.dataMatrix.length-1;

        // up=true 向上填充，=false 向下填充
        boolean up = true;
        // 结束条件：数据填充完毕或者列遍历完
        while(i<messageArr.length-1 && column>=0){
            
            // 跳过 timing patterns 列
            if(column==6)
                column--;
            // 每次写两个数据
            if(row>=0 && column>=0){
                if(this.dataMatrix[row][column]==-1){
                    this.dataMatrix[row][column] = messageArr[i++]+4;
                }
                if(this.dataMatrix[row][column-1]==-1){
                    this.dataMatrix[row][column-1] = messageArr[i++]+4;
                }
            }

            // 向上填充模式，每次row--；向下填充模式，每次row++。到达边界，重置row，并!up。
            if(up){
                if(row--==0){ // 每次处理后，行-1，
                    row++; //row=0;
                    column-=2;
                    up=!up;
                }

            }else{
                if(row++==this.dataMatrix.length-1){
                    row--; //row = this.dataMatrix.length-1;
                    column-=2;
                    up=!up;
                }
            }
            
        }

    }

    /**
     * 数据掩码
     */
    protected void dataMasking(){
        // 八种掩码 测试
        testEightMasking();
        // 确定掩码模式

        // 添加掩码

    }

    /**
     * 测试八种掩码
     */
    private void testEightMasking(){
        
        for(int i=0; i<Data.masking.length; i++){
            int dataTemp[][] = new int[this.dataMatrix.length][this.dataMatrix.length];
            for(int di=0; di<this.dataMatrix.length; di++){
                for(int dj=0; dj<this.dataMatrix.length; dj++){
                    if(this.dataMatrix[di][dj]!=4 && this.dataMatrix[di][dj]!=5){
                        dataTemp[di][dj] = this.dataMatrix[di][dj];
                    }else{
                        // System.out.printf("%d %d %d %b\n", i, di, dj, Data.masking[i].check(di, dj));
                        if(Data.masking[i].check(di, dj)){
                            dataTemp[di][dj] = this.dataMatrix[di][dj]^1; 
                        }
                    }
                }
            }
            printImage("70mask_"+i,dataTemp);
        }
    }
    /**
     * 惩罚分数
     */
    private void evaluationPunishScore(){

    }

    /**
     * 打印图片，用来测试
     * @param name
     */
    private void printImage(String name){
        new Paint(300, this.dataMatrix).save("./image/version"+this.version+"_"+name+".jpg");
    }
    private void printImage(String name, int data[][]){
        new Paint(300, data).save("./image/version"+this.version+"_"+name+".jpg");
    }

    /**
     * 输出二维码基本信息
     */
    public void info(){
        System.out.printf("length=%d, mode=%s, level=%s, version=%d\ncode encoding: %s\n" + 
            "data codewords: %s\nfinal message: %s\n", 
            this.dataLen, this.mode.name(), this.level.name(), this.version, this.dataEncodeStr,
            Arrays.deepToString(this.dataCodewords), this.message
        );
    }
}



class QrCodeTest{
    public static void main (String[] args) throws Exception
	{
        /* 测试模式和版本
        // 测试不同的模式
        new QrCode("1234").info(); // length=4, mode=Numeric, level=L, version=1
        new QrCode("/1T $:").info(); // length=6, mode=Alphanumeric, level=L, version=1
        new QrCode("/1T $:^").info(); // length=7, mode=Byte, level=L, version=1
        new QrCode("/1T $:^谦").info(); // length=8, mode=Kanji, level=L, version=1 // byte编码后是 length=10, mode=Byte, level=L, version=1
        // 测试同样数据在不同纠错级别下（根据数据长度选择的最小）的二维码版本
        new QrCode("如果爱是谎言，王子说的是谎言，如果音乐是谎言，你的心是荒野").info(); // length=9, mode=Kanji, level=L, version=1; byte后：length=87, mode=Byte, level=L, version=5
        new QrCode("如果爱是谎言，王子说的是谎言，如果音乐是谎言，你的心是荒野", Data.LEVEL.H).info(); // length=29, mode=Kanji, level=H, version=6; byte之后：length=87, mode=Byte, level=H, version=9
        */

        /** 
        // 测试数据编码 
        new QrCode("HELLO WORLD", Data.LEVEL.Q).info(); // 00100000010110110000101101111000110100010111001011011100010011010100001101000000111011000001000111101100
        new QrCode("HELLO WORLD", Data.LEVEL.M).info(); // 00100000010110110000101101111000110100010111001011011100010011010100001101000000111011000001000111101100000100011110110000010001
        */

        new QrCode("HELLO WORLD", Data.LEVEL.M).info();
    }

}
