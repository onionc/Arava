## 二维码生成



### 1. 数据码字介绍



### 2. 纠错算法

需要纠错算法，纠错算法中需要使用多项式进行运算，所以先处理多项式的四则运算。



纠错编码使用多项式长除法，需要两种多项式，一个是消息多项式 (message polynomial)，另一个是生成多项式 (generator polynomial)。

消息多项式将被一个生成器多项式除。发生器多项式是通过相乘得到的多项式  

#### 2.1 消息多项式

消息多项式使用来自数据编码步骤的数据码字作为其系数。例如，如果转换为整数的数据码字是25、218和35，则消息多项式将是$25x^2 + 218x + 35$

生成消息多项式代码：

```java
import Poly.Polynomial; // 之前写的多项式类

/**
 * 纠错算法
 */
public class ErrorCorrectionCoding{
    public static void main(String[] args){
        // 消息多项式
        int[] code = {2, 25, 218, 35};
        Polynomial mP = new Test().messagePolynomial(code);
        System.out.println(mP); // +2.0x^{3.0}+25.0x^{2.0}+218.0x+35.0
    }


}

class Test{

    /**
     * 生成 消息多项式
     * @param code
     * @return
     */
    public Polynomial messagePolynomial(int[] code){
        Polynomial m = new Polynomial();
        for(int i=0; i < code.length; i++){
            m.addItem(code[i], code.length-i-1);
        }

        return m;
    }
}
```

使用数字码 `2, 25, 218, 35` 生成消息多项式为 $+2.0x^{3.0}+25.0x^{2.0}+218.0x+35.0$。

#### 2.2 生成多项式

生成多项式是通过相乘得到的多项式： $(x - α^0) ... (x - α^{n-1})$ 。

这里将展示如何计算2个错误修正码的生成器多项式，因为它演示了计算所有剩余生成器多项式的过程：

**2个错误纠正码的发生器多项式**

