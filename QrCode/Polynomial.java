import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private FORMAT f = FORMAT.LATEX; // 默认的打印格式
    
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
        String formatStr = "";
        // 指数为1或者0时，特殊显示
        if(expn == 1){
            formatStr = "%1$+d%3$s";
        }else if(expn == 0){
            formatStr = "%1$+d";
        }else{
            switch(this.f){
                case NORMAL:
                    formatStr = "%1$+d%3$s^%2$d";
                    break;
                case LATEX:
                    formatStr = "%1$+d%3$s^{%2$d}";
                    break;
            }
            
        }
        return String.format(formatStr, coef, expn, x);
    }
}
public class Polynomial {
    private List<Node> poly;
    private Iterator<Node> iter; // 每次使用请重置，只为了增加变量而不是存储当前位置

    public Polynomial(){
        this.poly = new LinkedList<Node>();
    }


    /**
     * 添加一项
     */
    public void addItem(int coef, int expn){
        // 过滤系数为0的项
        if(coef == 0){
            return;
        }
        this.add(new Node(coef, expn));
        sort();
    }

     /**
     * 添加一项
     */
    public void addItem(Node item){
        // 过滤系数为0的项
        if(item.coef == 0){
            return;
        }
        this.add(item);
        sort();
    }

    /**
     * 多项式排序，指数高的在前
     */
    private void sort(){
        Collections.sort(this.poly, new PolynomialComparator());
    }

    /**
     * 加上多项式中的一项
     * @param pn2
     * @return
     */
    private void add(Node n2){

        Iterator<Node> p1 = this.poly.iterator();
        while(p1.hasNext()){
            Node p1_node = p1.next();
            int index = this.poly.indexOf(p1_node);
            if(this.compareInt(p1_node.expn, n2.expn) == 0){
                
                int sum = p1_node.coef + n2.coef;
                if(sum!=0){
                    this.poly.set(index, new Node(sum, p1_node.expn));
                }
                n2 = null;
                break;
            }
        }

        if(n2 != null){
            this.poly.add(n2);
        }

        return ;
    }

    /**
     * 两个多项式相加（加上一个多项式）
     * @param pn2
     * @return
     */
    public Polynomial add(Polynomial pn2){
        // 复制原 poly
        Polynomial pn_result = new Polynomial();
        for(Node a : this.poly){
            pn_result.add(a);
        }

        // 新增的多项式，每一项加上去即可
        Iterator<Node> p2 = pn2.poly.iterator();
        while(p2.hasNext()){
            Node t = p2.next();
            pn_result.add(t);
        }
        pn_result.sort();
        return pn_result;
    }

    
    /**
     * 比较
     * @param v1
     * @param v2
     * @return
     */
    private int compareInt(int v1, int v2){
        if(v1 > v2){
            return 1;
        }else if(v1 == v2){
            return 0;
        }else{
            return -1;
        }

        
    }


    public String toString(){
        this.iter = this.poly.iterator();
        StringBuilder s = new StringBuilder();
        while(this.iter.hasNext()){
            s.append(this.iter.next());
        }
        return s.toString();
    }

    // 测试多项式加法
    public static void main(String[] args){
        Polynomial a = new Polynomial();
        a.addItem(-1,2);
        a.addItem(3,2);
        a.addItem(0,1);
        a.addItem(90,3);
        a.addItem(10,2);
        a.addItem(7,0);
        System.out.println(a); // +90x^{3}+12x^{2}+7

        Polynomial b = new Polynomial();
        b.addItem(0,3);       
        b.addItem(3,2);
        b.addItem(5,1);
        b.addItem(3,-3);
        System.out.println(b); // +3x^{2}+5x+3x^{-3}

        Polynomial c = a.add(b);
        System.out.println(c); // +90x^{3}+15x^{2}+5x+7+3x^{-3}

    }

}

/**
 * 排序多项式
 */
class PolynomialComparator implements Comparator<Node>{
    @Override
    public int compare(Node n1, Node n2){
        return (n2.expn - n1.expn);
    }
}

