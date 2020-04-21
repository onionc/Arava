package Unit;


/**
 * 每一项（每个节点）的数据
 * @param args
 */
public class Node{
    public Coef coef; // 系数
    public int expn; // 指数
    private final static char x = 'x'; // 未知数的表示符号
    
    public Node(int a_e, int expn){
        this.coef = new Coef(a_e);
        this.expn = expn;
    }

    /**
     * 两个Coef相加，返回相加后的指数
     * 比如：α^2+α^1 => 4+1 => 4^1 = 5, antilog(5)=32
     * @param c1
     * @param c2
     * @return
     */
    public static int addCoef(Coef c1, Coef c2){
        int value = c1.value ^ c2.value;
        int alpha_expn = Power.getAntilog(value);
        return alpha_expn;
    }

    /**
     * 两个Coef相乘
     */
    public static int mulCoef(Coef c1, Coef c2){
        return (c1.alpha_expn + c2.alpha_expn)%255;
    }

    public String toString(){
        String formatStr = "";
        // 系数为1（系数为0的情况在addItem时已过滤）、指数为1或者0时，特殊显示
        if(coef.value!=1){ 
            formatStr += "+%1$s";
        }

        if(expn!=0){
            if(formatStr == ""){
                formatStr = "+";
            }
            formatStr += "%3$s";
            if(expn!=1){
                formatStr += "^{%2$s}";
            }
        }else if(formatStr==""){
            formatStr += "+%1$s";
        }

        return String.format(formatStr, coef, expn, x);
    }
}

/**
 * 系数节点 α^expn, 一般地，α=2
 */
class Coef{
    public final char alpha='α';
    public int alpha_expn;
    public int value;
    public Coef(int expn){
        this.alpha_expn = expn;
        this.value = Power.getPower(alpha_expn);
    }
    public String toString(){
        return String.format("%c^{%d}", alpha, alpha_expn);
    }
}