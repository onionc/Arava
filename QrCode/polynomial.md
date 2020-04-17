## 多项式操作

### 多项式四则运算

加法部分思路参考 [多项式加法，用单链表实现](https://www.cnblogs.com/erlongi/p/10759921.html)



```java
    public static void main(String[] args){
        Polynomial a = new Polynomial();
        a.addItem(-1,2).addItem(3,2).addItem(0,1).addItem(90.5,3);
        a.addItem(10,2);
        a.addItem(7,0);
        System.out.println(a); // +90.5x^{3}+12x^{2}+7

        Polynomial b = new Polynomial();
        b.addItem(0,3).addItem(3,2).addItem(5,1).addItem(3,-3);
        System.out.println(b); // +3x^{2}+5x+3x^{-3}

        // 加法操作
        Polynomial c = a.add(b);
        System.out.println(c); // +90.5x^{3}+15x^{2}+5x+7+3x^{-3}

        // 减法操作
        Polynomial c2 = a.sub(b);
        System.out.println(c2); // +90.5x^{3}+9.0x^{2}-5.0x+7-3.0x^{-3}

        // 乘法操作
        Polynomial d = a.mul(b);
        System.out.println(d); // +271.5x^{5.0}+488.5x^{4.0}+60.0x^{3.0}+21.0x^{2.0}+35.0x+271.5+36.0x^{-1.0}+21.0x^{-3.0}
        
        // 除法操作
        Polynomial d1 = new Polynomial().addItem(1,3).addItem(5,2).addItem(6,1).addItem(3,0);
        Polynomial e = new Polynomial(new Node(1,1)).addItem(1,0);

        // 商 和 余数
        Polynomial quotient = new Polynomial();
        Polynomial remainder = new Polynomial();
        d1.div(e, quotient, remainder);
        
        System.out.println(d1); // x^{3}+5x^{2}+6x+3
        System.out.println(e); // x+1
        System.out.println(String.format("%s ... (%s) ", quotient, remainder)); // +x^{2.0}+4.0x+2.0 ... (+1.0)
        System.out.println(String.format("%s + [(%s) / (%s)]", quotient, remainder, e)); // +x^{2.0}+4.0x+2.0 + [(+1.0) / (+x+1)]
    }
```

两个数的和差积分别为：

a = $+90.5x^{3}+12.0x^{2}+7$
b = $+3x^{2}+5x+3x^{-3}$
a+b = $+90.5x^{3}+15.0x^{2}+5x+7+3x^{-3}$
a-b = $+90.5x^{3}+9.0x^{2}-5.0x+7-3.0x^{-3}$
a\*b = $+271.5x^{5.0}+488.5x^{4.0}+60.0x^{3.0}+21.0x^{2.0}+35.0x+271.5+36.0x^{-1.0}+21.0x^{-3.0}$
除法用简单的多项式测试：
被除数：a = $+x^{3}+5x^{2}+6x+3$
除数：b = $+x+1$
商为：a/b = $+x^{2.0}+4.0x+2.0$ 余数是 $1.0$
表示为：
$ +x^{2.0}+4.0x+2.0 ... (+1.0)$
或者
$+x^{2.0}+4.0x+2.0 + [(+1.0) / (+x+1)]$

