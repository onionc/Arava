##  纠错算法
首先介绍伽罗瓦域，接着使用日志和反日志来简化GF(256)中的乘法。
因为纠错算法中需要使用多项式进行运算，需要处理多项式的四则运算。
纠错编码的多项式长除法，使用两种多项式，一个是消息多项式 (message polynomial)，另一个是生成多项式 (generator polynomial)。消息多项式将被一个生成器多项式除；生成多项式是通过相乘得到的多项式 $(x - α^0) ... (x - α^{n-1})$ 。

### 1. 伽罗瓦域 GF(256) 介绍



### 2. 日志和反日志

 因此，在GF(256)中执行乘法只需要生成2的所有乘方， 以及相应的逆对数。

>  一个数的逆对数（antilogarithm），是指一个正数的对数即等于已知数。也就是说，在b=logaN对数运算中（以a为底的对数），逆对数是已知对数b去寻求相应的[真数](https://baike.baidu.com/item/真数/20402544)N。 

```java
import java.util.Arrays;

/**
 * 幂和逆对数
 */
public class Power {
        /**
     * 生成2的n次方,GF(256) 范围内的幂
     */
    public static int[] powersOf2ForGF(){
        int gf[] = new int[256]; // key 是指数, value 是幂值
        int t = 0;

        // 初始化第一个值 2**0=1
        gf[0] = 1;
        for(int exponent=1; exponent<=255; exponent++){
            gf[exponent] = exponent;
            t = gf[exponent-1]*2;
            
            if(t>255){
                t ^= 285;
            }
            gf[exponent] = t;
        }

        
        return gf;
    }

    /**
     * 逆对数 anti log
     * @param log
     * @return
     */
    public static int[] antiLog(int powers[]){
        int antiLog[] = new int[256]; // key 是值, value 是指数
        
        for(int exponent=0; exponent<=255; exponent++){
            antiLog[powers[exponent]] = exponent;
        }
        antiLog[0] = Integer.MIN_VALUE; // 2**x=0，指数x不存在，无穷小则趋于0
        antiLog[1] = 0; // 值为1 对应的指数为 0 和 255, 取0
        return antiLog;
    }

    public static void main(String[] args){
        // 2 的幂
        int powers[] = Power.powersOf2ForGF();
        System.out.println(Arrays.toString(powers));
        // 生成逆对数
        int antiLog[] = Power.antiLog(powers);
        System.out.println(Arrays.toString(antiLog));
    }
}
```

```
[1, 2, 4, 8, 16, 32, 64, 128, 29, 58, 116, 232, 205, ... 216, 173, 71, 142, 1]
[-2147483648, 0, 1, 25, 2, 50, 26, 198, 3, 223, 51, ... 244, 234, 168, 80, 88, 175]
```



### 3. 消息多项式

消息多项式使用来自数据编码步骤的数据码字作为其系数。例如，如果转换为整数的数据码字是25、218和35，则消息多项式将是$25x^2 + 218x + 35$

生成消息多项式代码：

```java

public class ErrorCorrectionCoding{
    public static void main(String[] args){
        // 消息多项式
        int[] code = {2, 25, 218, 35};
        Polynomial mP = new ErrorCorrectionCoding().messagePolynomial(code);
        System.out.println(mP); // 2x^{3}+25x^{2}+218x+35
    }


    /**
     * 消息多项式
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

使用数字码 `2, 25, 218, 35` 生成消息多项式为 $2x^{3}+25x^{2}+218x+35$。

### 4. 生成多项式

生成多项式是通过相乘得到的多项式： $(x - α^0) ... (x - α^{n-1})$ 。

这里将展示如何计算2个错误修正码的生成多项式，因为它演示了计算所有剩余生成多项式的过程：

**两个错误纠正码的生成多项式**

