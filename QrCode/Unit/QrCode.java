package Unit;

import java.util.regex.Pattern;
import java.nio.charset.Charset;

class QrCode{
    // 要编码的数据
    String data;
    // 四种模式
    enum Mode{Numeric, Alphanumeric, Byte, Kanji};
    Mode mode;
    
    
    public QrCode(String data){
        this.data = data;
        checkMode();

        System.out.println(this.mode);
    }
    
    /**
     * 检测模式 
     */
    protected void checkMode(){
         
	    if(Pattern.compile("^[0-9]+$").matcher(this.data).matches()){
	        this.mode = Mode.Numeric;
	    }else if(Pattern.compile("^[0-9A-Z $%*+-./:]+$").matcher(this.data).matches()){
            this.mode = Mode.Alphanumeric;
        }else if(Charset.forName("ISO-8859-1").newEncoder().canEncode(this.data)){
            this.mode = Mode.Byte;
        }else if(Charset.forName("UTF-8").newEncoder().canEncode(this.data)){
            this.mode = Mode.Kanji;
        }

    }
    
}

class Test{
    public static void main (String[] args)
	{
        new QrCode("1234"); // mode is Numeric
        new QrCode("/1T $:"); // mode is Alphanumeric
        new QrCode("/1T $:^"); // mode is Byte
        new QrCode("/1T $:^的"); // mode is Kanji
	}
	
}
