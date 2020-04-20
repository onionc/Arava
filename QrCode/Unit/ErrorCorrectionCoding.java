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
        // 两个错误纠正码的生成器多项式
        p = new ErrorCorrectionCoding().generatorPolynomial(2); // +x^{2}+3x+2
        // p=new ErrorCorrectionCoding().generatorPolynomial(3); // +x^{3}+7x^{2}+14x+8
        // p = new ErrorCorrectionCoding().generatorPolynomial(10); // 
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

    // 


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
        
        // i=0
        if(ECC_Number==1){
            t.addItem(1, 1).addItem(1,0);
            System.out.println(t + "\tx^" + 0);

            return t;
        }else{
            PolynomialGF t2 = new PolynomialGF().addItem(1,1).addItem(1<<(ECC_Number-1),0);
            System.out.println(t2 + "\tx^" + (ECC_Number-1));

            return t2.mul(generatorPolynomial(ECC_Number-1));
        }
        
    }


}