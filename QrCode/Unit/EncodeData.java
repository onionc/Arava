package Unit;

/**
 * 根据不同的模式对数据进行编码
 */
public class EncodeData{
    QrCode q; // QrCode对象参数
    String code; // 编码结果

    /**
     * 为了确保数据模式正确，用QrCode对象参数
     * @param q  // QrCode object
     */
    public EncodeData(QrCode q){
        this.q = q;
        this.code = "";

        switch(q.mode){
            case Numeric:
                this.code = NumericEncode();
                break;
            case Alphanumeric:
                this.code = AlphanumericEncode();
                break;
            case Byte:
                this.code = ByteEncode();
                break;
            case Kanji:
                this.code = KanjiEncode();
                break;
            default: 
                break;
        }
    }

    public String toString(){
        return this.code;
    }

    /**
     * 数字模式编码
     * https://www.thonky.com/qr-code-tutorial/numeric-mode-encoding
     * @return
     */
    public String NumericEncode(){
        StringBuffer s = new StringBuffer();

        int valueArr[] = Common.strSplitToInt(this.q.data, 3);
        String t = "";
        for(int v: valueArr){
            switch( String.valueOf(v).length() ){
                case 3:
                    t = Common.formatBin(10, v);
                    break;
                case 2:
                    t = Common.formatBin(7, v);
                    break;
                case 1:
                    t = Common.formatBin(4, v);
                    break;
            }
            s.append(t);
        }
        return s.toString();
    }

    /**
     * 字母数字模式编码
     * https://www.thonky.com/qr-code-tutorial/alphanumeric-mode-encoding
     * @return
     */
    public String AlphanumericEncode(){
        StringBuffer s = new StringBuffer();
        String valueArr[] = Common.strSplit(this.q.data, 2);
        String t = "";

        int sum = 0; // 两个或者一个字符的值(1*45+2)
        for(String v: valueArr){
            switch( v.length() ){
                case 2:
                    sum = Data.CharCode.indexOf(v.charAt(0)) * 45 + Data.CharCode.indexOf(v.charAt(1));
                    t = Common.formatBin(11, sum);
                    break;
                case 1:
                    sum = Data.CharCode.indexOf(v.charAt(0));
                    t = Common.formatBin(6, sum);
                    break;
            }
            s.append(t);
        }
        return s.toString();
    }

    /**
     * 字节编码模式
     * ISO-8859-1（即ascii扩展表）。也可以包含其他字符，先转utf-8编码，再取每个字节编码
     * @return
     */
    public String ByteEncode(){
        StringBuffer s = new StringBuffer();
        String valueArr[] = Common.strToBytes(this.q.data);

        for(String v: valueArr){
            s.append(v);
        }
        return s.toString();
    }

    /**
     * 弃用，都转utf-8后用Byte编码
     */
    public String KanjiEncode(){
        return null;
        
    }
}

class EncodeDataTest{
    public static void main(String[] args) throws Exception{
        // 不同模式数据的编码
        // mode=Numeric
        EncodeData e1 = new EncodeData(new QrCode("8675309"));
        System.out.println(e1.code); // 110110001110000100101001

        // mode=Alphanumeric
        EncodeData e2 = new EncodeData(new QrCode("HELLO WORLD"));
        System.out.println(e2.code); // 0110000101101111000110100010111001011011100010011010100001101

        // mode=Byte
        EncodeData e3 = new EncodeData(new QrCode("Hello, world!"));
        System.out.println(e3.code); // 01001000011001010110110001101100011011110010110000100000011101110110111101110010011011000110010000100001
        
        // mode=Kanji, 模式本来是Kanji，改为Byte
        EncodeData e4 = new EncodeData(new QrCode("茗荷"));
        System.out.println(e4.code); // 111010001000110010010111111010001000110110110111
    }
}