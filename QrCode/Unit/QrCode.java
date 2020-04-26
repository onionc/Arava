import java.util.regex.Pattern;

class QrCode{
    String data;
    String mode;
    
    public QrCode(String data){
        this.data = data;
        checkMode();
    }
    
    /**
     * 检测模式 
     */
    protected void checkMode(){
         
	    if(Pattern.compile("^-?\\d+(\\.\\d+)?$").matcher(this.data).matches()){
	       this.mode = "Number"; 
	    }
	    
	    else if(java.nio.charset.Charset.forName("UTF-8").newEncoder().canEncode(this.data)){
	        this.mode = "Kanji";
	    }else if(java.nio.charset.Charset.forName("ISO-8859-1").newEncoder().canEncode(this.data)){
	        
	    }
        System.out.println(this.mode);
    }
    
	   

}

class test{
    public static void main (String[] args)
	{
	    QrCode q = new QrCode("1");    
	}
	
}
