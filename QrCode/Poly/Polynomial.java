package Poly;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 一元多项式
 */


/**
 * 每一项（每个节点）的数据
 * @param args
 */
class Node<T extends Number>{
    public T coef; // 系数
    public T expn; // 指数
    private final static char x = 'x'; // 未知数的表示符号
    
    public Node(T coef, T expn){
        this.coef = coef;
        this.expn = expn;
    }

    public String toString(){
        String formatStr = "";
        // 系数为1（系数为0的情况在addItem时已过滤）、指数为1或者0时，特殊显示
        if(!Node.compareT(coef, 1)){
            if(coef.doubleValue() > 0)
                formatStr += "+";
            formatStr += "%1$s";
        }

        if(!Node.compareT(expn, 0)){
            if(formatStr == ""){
                formatStr = "+";
            }
            formatStr += "%3$s";
            if(!Node.compareT(expn, 1)){
                formatStr += "^{%2$s}";
            }
        }else if(formatStr==""){
            if(coef.doubleValue() > 0)
                formatStr += "+";
            formatStr += "%1$s";
        }
        
        return String.format(formatStr, coef, expn, x);
    }

    public static <T extends Number> boolean compareT(T v1, T v2){
        return (Math.abs(v1.doubleValue()-v2.doubleValue()) < 1e-6);
    }
}
public class Polynomial<T extends Number>{
    private List<Node<T>> poly;
    private Iterator<Node<T>> iter; // 每次使用请重置

    public Polynomial(){
        this.poly = new LinkedList<Node<T>>();
    }

    public Polynomial(Node<T> n1){
        this.poly = new LinkedList<Node<T>>();
        this.addItem(n1);
    }


    /**
     * 添加一项
     */
    public Polynomial<T> addItem(T coef, T expn){
        // 过滤系数为0的项
        if(!Node.compareT(coef, 0)){
            this.addNode(new Node<T>(coef, expn));
            sort();
        }
        return this;
    }

     /**
     * 添加一项
     */
    public Polynomial<T> addItem(Node<T> item){
        // 过滤系数为0的项
        if(!Node.compareT(item.coef, 0)){ // item.coef != 0
            this.addNode(item);
            sort();
        }
        return this;
    }

    /**
     * 多项式排序，指数高的在前
     */
    private void sort(){
        Collections.sort(this.poly, new PolynomialComparator());
    }

    /**
     * 加法操作：加上多项式中的一项
     * @param n2 一项（一个节点）
     * @return
     */
    private void addNode(Node<T> n2){
        Iterator<Node<T>> p1 = this.poly.iterator();
        Node<T> p1_node;
        int index;
        double sum;
        while(p1.hasNext()){
            p1_node = p1.next();
            index = this.poly.indexOf(p1_node);
            if(Node.compareT(p1_node.expn, n2.expn) && index>-1){ // p1_node.expn == n2.expn
                sum = p1_node.coef.doubleValue() + n2.coef.doubleValue();
                if(Node.compareT(sum, 0))
                    this.poly.remove(index);
                else
                    this.poly.set(index, new Node(sum, p1_node.expn));
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
     * 复制多项式
     * @param pn
     * @return
     */
    private Polynomial copy(){
        Polynomial pn_result = new Polynomial();
        for(Node a : this.poly){
            pn_result.addNode(a);
        }
        return pn_result;
    }

    /**
     * 两个多项式相加（加上一个多项式）
     * @param pn2
     * @return
     */
    public Polynomial add(Polynomial pn2){
        // 复制原 poly
        Polynomial pn_result = this.copy();

        // 新增的多项式，每一项加上去即可
        Iterator<Node> p2 = pn2.poly.iterator();
        while(p2.hasNext()){
            Node t = p2.next();
            pn_result.addNode(t);
        }
        pn_result.sort();
        return pn_result;
    }

    /**
     * 取相反数
     */
    private void opposite(){
        for(Node n : this.poly){
            n.coef = n.coef.doubleValue() * -1;
        }
    }

    /**
     * 两个多项式想减
     * @param pn
     * @return
     */
    public Polynomial sub(Polynomial pn){
        // 取相反数
        pn.opposite();
        System.out.println(pn);
        return this.add(pn);
    }

    /**
     * 多项式相乘.
     * @param pn2
     * @return
     */
    public Polynomial<T> mul(Polynomial<T> pn2){

        Iterator<Node<T>> p1 = this.poly.iterator();
        Iterator<Node<T>> p2 = pn2.poly.iterator();
        Node<T> p1_node, p2_node;
        int index;
        double coef, expn;

        Polynomial<T> pn3 = new Polynomial<T>();
      
        while(p1.hasNext()){
            p1_node = p1.next();
            while(p2.hasNext()){
                p2_node = p2.next();
                index = this.poly.indexOf(p1_node);
                { // p1_node * p2_node
                    coef = p1_node.coef.doubleValue() * p2_node.coef.doubleValue();
                    expn = p1_node.expn.doubleValue() + p2_node.expn.doubleValue();
 
                    pn3.poly.add(new Node(coef, expn));
                }
                
            }
            p2 = pn2.poly.iterator(); // 重置p2迭代
        }

        return pn3.simplify(); // 化简返回
    }

    /**
     * 当前多项式的长度
     * @return
     */
    private int len(){
        return this.poly.size();
    }

    private Node divItem(Node<T> n1, Node<T> n2){
        if(n1.expn.doubleValue() < n2.expn.doubleValue()){
            return null;
        }else{
            return new Node(n1.coef.doubleValue()/n2.coef.doubleValue(), n1.expn.doubleValue()-n2.expn.doubleValue());
        }
    }

    /**
     * 除法
     * @param divisor
     */
    public void div(Polynomial divisor, Polynomial quotient, Polynomial remainder){
        this.simplify();
        divisor.simplify();


        //  n1 被除数，n2 除数 的最高项
        Node n1 = this.maxExpnItem();
        Node n2 = divisor.maxExpnItem();
        // System.out.println(n1);
        // System.out.println(n2);
        // 获取最大项，找到商.
        Node quotientNode; // 临时商
        quotientNode = divItem(n1, n2);
        if(quotientNode==null){
            remainder.poly = this.copy().poly;
            return;
        }
        System.out.println(quotientNode);

        // 结果1
        Polynomial r1 = divisor.mul(new Polynomial(quotientNode));
        System.out.println(r1);

        // 计算差
        //System.out.println(this);
        System.out.println(r1);


        Polynomial r2 = this.sub(r1);
        System.out.println(r2);
        
        quotient.addItem(quotientNode);

        r2.div(divisor, quotient, remainder);

           
            
    }

    /**
     * 获取多项式中的最大项
     */
    private Node maxExpnItem(){
        iter = this.poly.iterator();
        if(iter.hasNext()){
            return iter.next();
        }else{
            return null;
        }
    }

    /**
     * 化简多项式
     */
    public Polynomial simplify(){
        return this.add(new Polynomial());
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
        // Polynomial a = new Polynomial();
        // a.addItem(-1,2).addItem(3,2).addItem(0,1).addItem(90,3);
        // a.addItem(10,2);
        // a.addItem(7,0);
        // System.out.println(a); // +90x^{3}+12x^{2}+7

        // Polynomial b = new Polynomial();
        // b.addItem(0,3).addItem(3,2).addItem(5,1).addItem(3,-3);
        // System.out.println(b); // +3x^{2}+5x+3x^{-3}

        // 加法操作
        // Polynomial c = a.add(b);
        // System.out.println(c); // +90x^{3}+15x^{2}+5x+7+3x^{-3}


        //乘法操作
        // Polynomial d = a.mul(b);
        // System.out.println(d); // +270x^{5}+450x^{4}+270+36x^{4}+60x^{3}+36x^{-1}+21x^{2}+35x+21x^{-3}

        Polynomial d1 = new Polynomial().addItem(1,3).addItem(5,2).addItem(6,1).addItem(3,0);
        Polynomial e = new Polynomial(new Node(1,1)).addItem(1,0);
        // Polynomial d1 = new Polynomial().addItem(1,2).addItem(-3,0);
        // Polynomial e = new Polynomial(new Node(2,1)).addItem(-4,0);
        // 商 和 余数

        Polynomial quotient = new Polynomial();
        Polynomial remainder = new Polynomial();
            
        d1.div(e, quotient, remainder);
        System.out.println(d1);
        
        System.out.println(e);
        System.out.println(quotient);
        System.out.println(remainder + "/(" + e + ")");


    }


    /**
     * 多项式排序
     */
    class PolynomialComparator implements Comparator<Node<T>>{
        @Override
        public int compare(Node<T> n1, Node<T> n2){
            if(Node.compareT(n2.expn, n1.expn)){ 
                return 0;
            }else if(n2.expn.doubleValue() < n1.expn.doubleValue()){
                return -1;
            }else{
                return 1;
            }
        }
    }

}



