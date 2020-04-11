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
class Node<T extends Comparable>{
    public T coef; // 系数
    public T expn; // 指数
    private final static char x = 'x'; // 未知数的表示符号
    private enum FORMAT {NORMAL, LATEX}; // 打印格式
    private FORMAT f = FORMAT.LATEX; // 默认的打印格式
    
    public Node(T coef, T expn){
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
        String formatStr = "";
        // 指数为1或者0时，特殊显示
        if(expn.compareTo(1) == 0){
            formatStr = "%1$+d%3$s";
        }else if(expn.compareTo(0) == 0){
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
public class Polynomial<T extends Comparable> {
    private List<Node<T>> poly;
    private Iterator<Node<T>> iter; // 每次使用请重置，只为了增加变量而不是存储当前位置

    public Polynomial(){
        this.poly = new LinkedList<Node<T>>();
    }


    /**
     * 添加一项
     */
    public <T extends Comparable>Polynomial<T> addItem(T coef, T expn){
        // 过滤系数为0的项
        if(coef.compareTo(0) == 0){
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
        if(item.coef != 0){
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
        int index, sum;
        while(p1.hasNext()){
            p1_node = p1.next();
            index = this.poly.indexOf(p1_node);
            if(this.compareInt(p1_node.expn, n2.expn) == 0 && index>-1){ // p1_node.expn == n2.expn
                sum = p1_node.coef + n2.coef;
                if(sum!=0){
                    this.poly.set(index, new Node<T>(sum, p1_node.expn));
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
    public Polynomial<T> add(Polynomial<T> pn2){
        // 复制原 poly
        Polynomial<T> pn_result = new Polynomial<T>();
        for(Node<T> a : this.poly){
            pn_result.addNode(a);
        }

        // 新增的多项式，每一项加上去即可
        Iterator<Node<T>> p2 = pn2.poly.iterator();
        while(p2.hasNext()){
            Node<T> t = p2.next();
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
    public Polynomial<T> mul(Polynomial<T> pn2){

        Iterator<Node<T>> p1 = this.poly.iterator();
        Iterator<Node<T>> p2 = pn2.poly.iterator();
        Node<T> p1_node, p2_node;
        int index, coef, expn;

        Polynomial<T> pn3 = new Polynomial<T>();
      
        while(p1.hasNext()){
            p1_node = p1.next();
            while(p2.hasNext()){
                p2_node = p2.next();
                index = this.poly.indexOf(p1_node);
                { // p1_node * p2_node
                    coef = p1_node.coef * p2_node.coef;
                    expn = p1_node.expn + p2_node.expn;
                    pn3.poly.add(new Node<T>(coef, expn));
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
    /**
     * 除法
     * @param divisor
     */
    public void div(Polynomial<T> divisor, Polynomial<T> quotient, Polynomial<T> remainder){
        this.simplify();
        divisor.simplify();

        // 商 和 余数
        quotient = new Polynomial<T>();
        remainder = new Polynomial<T>();

        // 获取最大项，找到商. n1 被除数，n2 除数
        Node<T> n1 = this.maxExpnItem();
        Node<T> n2 = divisor.maxExpnItem();
        System.out.println(n1);
        System.out.println(n2);


     
            // int i,j,mm,ll;
            // int m = this.len();
            // int n = divisor.len();
            // int k = 
            // ll=m-1;
            // for(i=k;i>0;i--){
            //  R[i-1]=A[ll]/B[n-1];
            //  mm=ll;
            //  for(j=1;j<=n-1;j++){
            //  A[mm-1]-=R[i-1]*B[n-j-1];
            //  mm-=1;
            //  }
            //  ll-=1;
            // }
            // for(i=0;i<l;i++){
            //  L[i]=A[i];
            // }
            
    }

    /**
     * 获取多项式中的最大项
     */
    private Node<T> maxExpnItem(){
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
    public Polynomial<T> simplify(){
        return this.add(new Polynomial<T>());
    }
    
    /**
     * 比较
     * @param v1
     * @param v2
     * @return
     */
    private int compareInt(T v1, T v2){
        if(v1.compareTo(v2) > 0){
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
        Polynomial<Double > a = new Polynomial<Double>();
        a.addItem(-1,2).addItem(3,2).addItem(0,1).addItem(90,3);
        a.addItem(10,2);
        a.addItem(7,0);
        System.out.println(a); // +90x^{3}+12x^{2}+7

        Polynomial<Integer> b = new Polynomial<Integer>();
        b.addItem(0,3).addItem(3,2).addItem(5,1).addItem(3,-3);
        System.out.println(b); // +3x^{2}+5x+3x^{-3}

        // 加法操作
        // Polynomial c = a.add(b);
        // System.out.println(c); // +90x^{3}+15x^{2}+5x+7+3x^{-3}
        

        //乘法操作
        // Polynomial d = a.mul(b);
        // System.out.println(d); // +270x^{5}+450x^{4}+270+36x^{4}+60x^{3}+36x^{-1}+21x^{2}+35x+21x^{-3}

    

        Polynomial<Double> e = new Polynomial<Double>();
        e.addItem(2,2);
        a.div(e,null,null);

    }


    /**
     * 多项式排序
     */
    class PolynomialComparator implements Comparator<Node<T>>{
        @Override
        public int compare(Node<T> n1, Node<T> n2){
            return (n2.expn - n1.expn);
        }
    }

}



