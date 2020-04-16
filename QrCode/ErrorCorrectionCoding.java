import Poly.Polynomial;

/**
 * 纠错算法
 */
public class ErrorCorrectionCoding{
    public static void main(String[] args){
        // 消息多项式
        int[] code = {2, 25, 218, 35};
        Polynomial mP = new Test().messagePolynomial(code);
        System.out.println(mP);
    }


}

class Test{

    /**
     * 生成 消息多项式
     * @param code
     * @return
     */
    public Polynomial messagePolynomial(int[] code){
        Polynomial m = new Polynomial();
        for(int i=0; i < code.length; i++){
            m.addItem(code[i], code.length-i-1);
        }

        return m;
    }

}