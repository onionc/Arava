package Unit;


/**
 * 纠错算法
 */
public class ErrorCorrectionCoding{
    public static void main(String[] args){
        // 消息多项式
        /*
        int[] code = {2, 25, 218, 35};
        Polynomial mP = new ErrorCorrectionCoding().messagePolynomial(code);
        System.out.println(mP);
        */
        PolynomialGF p;
        // n个错误纠正码的生成器多项式
        // p = new ErrorCorrectionCoding().generatorPolynomial(2); // +x^{2}+3x+2 = +x^{2}+α^{25}x+α^{1}
        // p=new ErrorCorrectionCoding().generatorPolynomial(3); // +x^{3}+α^{198}x^{2}+α^{199}x+α^{3}
        p = new ErrorCorrectionCoding().generatorPolynomial(7); // +x^{7}+α^{87}x^{6}+α^{172}x^{5}+α^{134}x^{4}+α^{149}x^{3}+α^{238}x^{2}+α^{102}x+α^{21}
        System.out.println(p);
    }

    /**
     * 消息多项式
     * @param code
     * @return
     */
    public PolynomialGF messagePolynomial(int[] code){
        PolynomialGF m = new PolynomialGF();
        for(int i=0; i < code.length; i++){
            m.addItem(code[i], code.length-i-1);
        }

        return m;
    }

    /**
     * 两个错误纠正码的生成器多项式 (x - α^0) ... (x - α^{n-1}) 
     * 递归实现，上一版本是普通实现
     * @param ECC_Number 纠错码字数(number of error correction codewords)
     * @return
     */
    public PolynomialGF generatorPolynomial(int ECC_Number){
        if(ECC_Number<1){
            return null;
        }

        PolynomialGF t = new PolynomialGF();

        // n=0
        if(ECC_Number==1){
            t.addItem(0, 1).addItem(0,0);
            Log.getLogger().info("add Item: " + t);

            return t;
        }else{
            t = new PolynomialGF().addItem(0, 1).addItem(ECC_Number-1,0);
            Log.getLogger().info("add Item: " + t);

            return t.mul(generatorPolynomial(ECC_Number-1));
        }
        
    }


}