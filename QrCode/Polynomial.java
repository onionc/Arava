import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * 一元多项式
 */


/**
 * 每一项（每个节点）的数据
 * @param args
 */
class Node{
    public int coef; // 系数
    public int expn; // 指数
    private final static char x = 'x'; // 未知数的表示符号
    private enum FORMAT {NORMAL, LATEX}; // 打印格式
    private FORMAT f = FORMAT.NORMAL; // 默认的打印格式
    
    public Node(int coef, int expn){
        this.coef = coef;
        this.expn = expn;
    }
    
    /**
     * 设置输出格式
     * @param fi 1:普通格式 (+90x^3-1x^-2+5x^1)，2 latex (+90x^{3}-1x^{-2}+5x^{1})
     */
    public void setFormat(int fi){
        switch(fi){
            case 1:
                this.f = FORMAT.NORMAL; 
                break;
            case 2:
                this.f = FORMAT.LATEX;
                break;
        }
    }

    public String toString(){
        //return String.format("%+d%s^%d", coef, x, expn);
        return String.format("%1$+d%3$s^{%2$d}", coef, expn, x);
    }
}
public class Polynomial {
    private List<Node> poly;
    private Iterator<Node> iter;

    public Polynomial(){
        this.poly = new LinkedList<Node>();
        this.reset();
    }

    /**
     * 重置迭代器
     */
    private void reset(){
        this.iter = poly.iterator();
    }

    /**
     * 添加一项
     */
    public void addItem(int coef, int expn){
        this.poly.add(new Node(coef, expn));
    }


    public String toString(){
        this.reset();
        StringBuilder s = new StringBuilder();
        while(this.iter.hasNext()){
            s.append(this.iter.next());
        }
        return s.toString();
    }

}