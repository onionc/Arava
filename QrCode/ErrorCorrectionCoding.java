
/**
 * 纠错算法
 */
public class ErrorCorrectionCoding{
    public static void main(String[] args){
        // System.out.println("纠错算法");
        Polynomial a = new Polynomial();
        a.addItem(90,3);       
        a.addItem(-1,-2);
        a.addItem(5,1);

        System.out.println(a);
    }
}