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
        // 两个错误纠正码的生成器多项式
        new ErrorCorrectionCoding().generatorPolynomial(2);
        new ErrorCorrectionCoding().generatorPolynomial(3);
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
     * @param ECC_Number 纠错码字数(number of error correction codewords)
     * @return
     */
    public PolynomialGF generatorPolynomial(int ECC_Number){
        if(ECC_Number<1){
            return null;
        }

        PolynomialGF ps = new PolynomialGF();
        // i=0
        ps.addItem(1, 1).addItem(1,0);
        System.out.println(ps);
        
        for(int i=1; i<ECC_Number; i++){
            PolynomialGF t = new PolynomialGF().addItem(1,1).addItem(1<<i,0);
            System.out.println(t);
        
            ps = ps.mul(t);
        }
        System.out.println(ps);
        return ps;
    }


}