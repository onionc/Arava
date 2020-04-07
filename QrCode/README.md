## 二维码生成

需要纠错算法，纠错算法中需要使用多项式进行运算，所以先处理多项式的加和乘。



### 多项式加法

部分思路参考 [多项式加法，用单链表实现](https://www.cnblogs.com/erlongi/p/10759921.html)

计算：
($90x^{3}+12x^{2}+7$) + ($3x^{2}+5x+3x^{-3}$)
= $+90x^{3}+15x^{2}+5x+7+3x^{-3}​$

```java
    public static void main(String[] args){
        Polynomial a = new Polynomial();
        a.addItem(-1,2).addItem(3,2).addItem(0,1).addItem(90,3);
        a.addItem(10,2);
        a.addItem(7,0);
        System.out.println(a); // +90x^{3}+12x^{2}+7

        Polynomial b = new Polynomial();
        b.addItem(0,3).addItem(3,2).addItem(5,1).addItem(3,-3);
        System.out.println(b); // +3x^{2}+5x+3x^{-3}

        Polynomial c = a.add(b);
        System.out.println(c); // +90x^{3}+15x^{2}+5x+7+3x^{-3}
    }
```



### 多项式乘法

