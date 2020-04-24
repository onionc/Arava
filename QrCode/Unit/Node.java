package Unit;


/**
 * 每一项（每个节点）的数据
 * @param args
 */
public class Node{
    public Coef coef; // 系数
    public int expn; // 指数
    private final static char x = 'x'; // 未知数的表示符号
    
    /**
     * 
     * @param value
     * @param expn
     * @param isCoefValue true: 表示系数coef是一个数值，false 表示α的系数
     */
    public Node(int value, int expn, boolean isCoefValue){
        this.coef = new Coef(value, isCoefValue);
        this.expn = expn;
    }
    public Node(int a_e, int expn){
        this(a_e, expn, false);
    }

    /**
     * 两个Coef相加，返回相加后的数值
     * 比如：α^2+α^1 => 4+1 => 4^1 = 5, 返回5. 
     * 现在是返回数值，如果要返回指数，可以调用 antilog(5)，但是因为Node已重载，直接给数值 
     * @param c1
     * @param c2
     * @return
     */
    public static int addCoef(Coef c1, Coef c2){
        int value = c1.value ^ c2.value;
        return value;
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
 * 系数也可以直接是数字
 */
class Coef{
    public final char alpha='α';
    public int alpha_expn;
    public int value;
    public static boolean outputFormatValue = false;
    
    /**
     * 默认的值是  α^expn 中的expn 
     * @param expn
     */
    public Coef(int expn){
        this(expn, false);
    }

    /**
     * 如果系数直接是一个数字，则调用此构造方法
     * @param value
     * @param valueFlag true: 表示值，false 表示α的系数
     */
    public Coef(int value, boolean isCoefValue){
        if(isCoefValue){
            this.value = value;
            this.alpha_expn = Power.getAntilog(value);
        }else{
            this.alpha_expn = value;
            if(value<0)
            this.value = Power.getPower(value);
            else
            this.value = Power.getPower(value);

        }
    }
    

    public String toString(){
        if(outputFormatValue)
            return String.format("%d", value);

        else
            return String.format("%c^{%d}", alpha, alpha_expn);
    }
}