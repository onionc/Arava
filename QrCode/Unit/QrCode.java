package Unit;

import java.util.regex.Pattern;
import java.nio.charset.Charset;

class QrCode{
    // 要编码的数据
    String data;
    // 四种模式
    enum MODE{Numeric, Alphanumeric, Byte, Kanji};
    MODE mode;
    // 纠错级别
    static enum LEVEL{L, M, Q, H};
    LEVEL level;
    // 版本设置
    int version;
    
    public QrCode(String data, LEVEL l) throws Exception{
        this.data = data;
        this.level = l;
        // 选择合适的模式
        chooseMode();
        // 确定最小版本
        chooseVersion();
        // 数据编码
        dataEncoding();
        System.out.println(this.version);
    }

    public QrCode(String data) throws Exception{
        this(data, LEVEL.L);
    }

    /**
     * 检测和设置模式 
     */
    protected void chooseMode(){
         
	    if(Pattern.compile("^[0-9]+$").matcher(this.data).matches()){
	        this.mode = MODE.Numeric;
	    }else if(Pattern.compile("^[0-9A-Z $%*+-./:]+$").matcher(this.data).matches()){
            this.mode = MODE.Alphanumeric;
        }else if(Charset.forName("ISO-8859-1").newEncoder().canEncode(this.data)){
            this.mode = MODE.Byte;
        }else if(Charset.forName("UTF-8").newEncoder().canEncode(this.data)){
            this.mode = MODE.Kanji;
        }
    }

    protected void chooseVersion() throws Exception {
        // 已经确定了容错级别和模式，再加上字符长度，选择一个最小的版本
        int dataLen = this.data.length();
        int levelIndex = this.level.ordinal();
        int modeIndex = this.mode.ordinal();
        this.version = 0;
        for(int i=1; i<Data.CharacterCapacities.length; i++){ // 版本 1-40
            if(Data.CharacterCapacities[i][levelIndex][modeIndex] >= dataLen){
                this.version = i;
                break;
            }
        }
        if(this.version>40 || this.version<1){
            throw new Exception("too long.");
        }
       
    }

    protected void dataEncoding(){

    }
    
}

class Test{
    public static void main (String[] args) throws Exception
	{
        new QrCode("1234"); // mode is Numeric
        new QrCode("/1T $:"); // mode is Alphanumeric
        new QrCode("/1T $:^"); // mode is Byte
        new QrCode("/1T $:^如果爱是谎言，王子说的是谎言，如果音乐是谎言，你的心是荒野", QrCode.LEVEL.H); // mode is Kanji

    }

}
