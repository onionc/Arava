## 二维码生成

需要纠错算法，纠错算法中需要使用多项式进行运算，所以先处理多项式的加和乘。



### 多项式加法

部分思路参考 [多项式加法，用单链表实现](https://www.cnblogs.com/erlongi/p/10759921.html)

$+90x^{3}+12x^{2}+7$
$+3x^{2}+5x+3x^{-3}$
= $+90x^{3}+15x^{2}+5x+7+3x^{-3}$