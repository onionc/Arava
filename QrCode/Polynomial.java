import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    private final static char x = 'x'; // 未知数表示
    
    public Node(int coef, int expn){
        this.coef = coef;
        this.expn = expn;
    }
    
    public String toString(){
        return String.format("%dx%d", coef, expn);
    }
}
public class Polynomial {
    private List<Node> poly;
    private Iterator<Node> iterator;

    public Polynomial(){
        this.poly = new ArrayList<Node>();
        this.reset();
    }

    /**
     * 重置迭代器
     */
    private void reset(){
        this.iterator = poly.iterator();
    }

    /**
     * 添加一项
     */
    public void add(int coef, int expn){
        this.poly.add(new Node(coef, expn));
    }


    public String toString(){
        this.reset();
        StringBuilder s = new StringBuilder();
        while(this.iterator.hasNext()){
            // todo
        }
        return "";
    }

}