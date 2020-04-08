## 二维码生成

需要纠错算法，纠错算法中需要使用多项式进行运算，所以先处理多项式的加和乘。



### 多项式加法和乘法操作

部分思路参考 [多项式加法，用单链表实现](https://www.cnblogs.com/erlongi/p/10759921.html)

计算：
$(90x^{3}+12x^{2}+7)$ 和 $ (3x^{2}+5x+3x^{-3})$

```java
Polynomial a = new Polynomial();
a.addItem(-1,2).addItem(3,2).addItem(0,1).addItem(90,3);
a.addItem(10,2);
a.addItem(7,0);
System.out.println(a); // +90x^{3}+12x^{2}+7

Polynomial b = new Polynomial();
b.addItem(0,3).addItem(3,2).addItem(5,1).addItem(3,-3);
System.out.println(b); // +3x^{2}+5x+3x^{-3}

// 加法操作
Polynomial c = a.add(b);
System.out.println(c); // +90x^{3}+15x^{2}+5x+7+3x^{-3}

//乘法操作
Polynomial d = a.mul(b);
System.out.println(d); // +270x^{5}+486x^{4}+60x^{3}+21x^{2}+35x+270+36x^{-1}+21x^{-3}
```

$(90x^{3}+12x^{2}+7) + (3x^{2}+5x+3x^{-3})$
= $+90x^{3}+15x^{2}+5x+7+3x^{-3}$

$(90x^{3}+12x^{2}+7) * (3x^{2}+5x+3x^{-3})$
= $+270x^{5}+450x^{4}+270+36x^{4}+60x^{3}+36x^{-1}+21x^{2}+35x+21x^{-3}$ （化简前）
= $+270x^{5}+486x^{4}+60x^{3}+21x^{2}+35x+270+36x^{-1}+21x^{-3}$ （化简后）