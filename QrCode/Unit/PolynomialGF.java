package Unit;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 一元多项式, GF(256)
 */


public class PolynomialGF{
    private List<Node> poly;
    private Iterator<Node> iter; // 每次使用请重置

    public PolynomialGF(){
        this.poly = new LinkedList<Node>();
    }

    public PolynomialGF(Node n1){
        this.poly = new LinkedList<Node>();
        this.addItem(n1);
    }


    /**
     * 添加一项
     */
    public PolynomialGF addItem(int alpha_expn, int expn){
        this.addNode(new Node(alpha_expn, expn));
        sort();
        
        return this;
    }

     /**
     * 添加一项
     */
    public PolynomialGF addItem(Node item){
        // 过滤系数为0的项
        if(item.coef.value != 0){
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
    private void addNode(Node n2){
        if(n2.expn<0 || n2.coef.alpha_expn<0){
            Log.getLogger().warning("addNode: must>=0");
            return;
        }
        Node n2Copy = new Node(n2.coef.alpha_expn, n2.expn);
        Iterator<Node> p1 = this.poly.iterator();
        Node p1_node;
        int index;
        int sum;
        while(p1.hasNext()){
            p1_node = p1.next();
            index = this.poly.indexOf(p1_node);
            if( p1_node.expn == n2.expn && index>-1){
                sum = Node.addCoef(p1_node.coef, n2.coef);
                if(sum == 0)
                    this.poly.remove(index);
                else
                    this.poly.set(index, new Node(sum, p1_node.expn));
                    n2Copy = null;
                break;
            }
        }

        if(n2Copy != null){
            this.poly.add(n2Copy);
        }
        return ;
    }

    /**
     * 复制多项式
     * @param pn
     * @return
     */
    private PolynomialGF copy(){
        PolynomialGF pn_result = new PolynomialGF();
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
    public PolynomialGF add(PolynomialGF pn2){
        // 复制原 poly
        PolynomialGF pn_result = this.copy();

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
     * 多项式相乘.
     * @param pn2
     * @return
     */
    public PolynomialGF mul(PolynomialGF pn2){

        Iterator<Node> p1 = this.poly.iterator();
        Iterator<Node> p2 = pn2.poly.iterator();
        Node p1_node, p2_node;
        int index;
        int alpha_expn, expn;

        PolynomialGF pn3 = new PolynomialGF();
      
        while(p1.hasNext()){
            p1_node = p1.next();
            while(p2.hasNext()){
                p2_node = p2.next();
                index = this.poly.indexOf(p1_node);
                { // p1_node * p2_node
                    alpha_expn = Node.mulCoef(p1_node.coef, p2_node.coef);
                    expn = p1_node.expn + p2_node.expn;
 
                    pn3.poly.add(new Node(alpha_expn, expn));
                }
                
            }
            p2 = pn2.poly.iterator(); // 重置p2迭代
        }

        return pn3.simplify(); // 化简返回
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
    public PolynomialGF simplify(){
        return this.add(new PolynomialGF());
    }

    public String toString(){
        this.iter = this.poly.iterator();
        StringBuilder s = new StringBuilder();
        while(this.iter.hasNext()){
            s.append(this.iter.next());
        }
        return s.toString();
    }

    /**
     * 多项式排序
     */
    class PolynomialComparator implements Comparator<Node>{
        @Override
        public int compare(Node n1, Node n2){
            if(n2.expn == n1.expn){ 
                return 0;
            }else if(n2.expn < n1.expn){
                return -1;
            }else{
                return 1;
            }
        }
    }

    // 测试多项式运算
    public static void main(String[] args){
        PolynomialGF a = new PolynomialGF();
        a.addItem(3,2).addItem(0,1).addItem(90,3);
        a.addItem(10,2);
        a.addItem(7,0);
        System.out.println(a); // +2^{90}x^{3}+2^{115}x^{2}+x+2^{7}

        PolynomialGF b = new PolynomialGF();
        b.addItem(0,3).addItem(3,2).addItem(5,1).addItem(3,-3);
        System.out.println(b); // +x^{3}+2^{3}x^{2}+2^{5}x

        // // 加法操作
        PolynomialGF c = a.add(b);
        System.out.println(c); // +2^{62}x^{3}+2^{10}x^{2}+2^{138}x+2^{7}

        // // 乘法操作
        PolynomialGF d = a.mul(b);
        System.out.println(d); // +2^{90}x^{6}+2^{73}x^{5}+2^{225}x^{4}+2^{171}x^{3}+2^{143}x^{2}+2^{12}x

    }
}



