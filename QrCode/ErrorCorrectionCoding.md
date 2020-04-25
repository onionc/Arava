##  纠错算法
原理参考 [Error Correction Coding - QR Code Tutorial ](https://www.thonky.com/qr-code-tutorial/error-correction-coding )

首先介绍伽罗瓦域，接着使用日志和反日志来简化GF(256)中的乘法。
因为纠错算法中需要使用多项式进行运算，需要处理多项式的四则运算。
生成纠错编码要进行多项式长除法，使用两种多项式，一个是消息多项式 (message polynomial)，另一个是生成器多项式 (generator polynomial)。消息多项式将被一个生成器多项式除；生成器多项式是通过相乘得到的多项式 $(x - α^0) ... (x - α^{n-1})$ 。

接着生成纠错码，用消息多项式除以生成器多项式得到余数多项式，它的系数就是所要的纠错码 。

### 1. 伽罗瓦域 (Galois Field) 介绍

伽罗瓦域，该域本质上是一组受限的数字（也称有限域），以及一些创建仍在该集合中的数字的数学运算。

二维码标准要求使用模2位运算和模100011101位运算。这意味着使用Galois Field $2^8$，或者说 Galois Field 256，有时写成GF(256)。 GF($2^w$) 表示含有 $2^w$个元素的有限域。

GF(256)中的数字都在0到255(包括)的范围内。注意，这是可以用8位字节表示的数字的相同范围(最大的8位字节是11111111，它等于255)。

**伽罗瓦域运算**

GF(256) 包含从0到255的所有数字。GF(256) 中的数学运算本质上是循环的，这意味着如果在 GF(256) 中执行的数学运算的结果大于255，那么就需要使用模运算来获得仍然在 GF 中的数字 。

在伽罗瓦域中，负数与正数具有相同的值，因此 **-n = n**。这意味着在 GF 中的加法和减法是一样的。伽罗瓦域中的加法和减法是通过正常的加减运算，然后再进行模运算来实现的。由于我们使用的是位模2算法(如二维码规范中所述)，所以这与执行XOR（异或^）操作是一样的。例如: 

1 + 1 = 2 % 2 = 0 等价于 1 ^ 1 = 0 

0 + 1 = 1 % 2 = 1 等价于 0 ^ 1 = 1 

 为了对二维码进行编码，GF(256)中的所有加减法都是通过将两个数字进行异或操作来进行的。 

**使用字节模100011101生成2的幂**

GF(256) 中的所有数字本身都必须在0到255的范围内，因此$2^8$ 对于 GF 来说似乎太大了，因为它等于256。 

二维码规范要求使用字节模 100011101 算法(其中 100011101 是一个二进制数，相当于十进制数的 285)。这意味着当一个数字是256或更大时，它应该与285异或操作。即：**$2^8$ = 256 ^ 285 = 29** 。 当$2^9$时，需要使用$2^8$的值来运算：**$2^9$ = $2^8$ * 2 = 29 * 2 = 58**。

（使用此过程，GF(256)中的所有数字都可以用2n表示，其中n是0 <= n <= 255范围内的数字。使用字节模100011101可以确保所有的值都在0到255的范围内。 ）

下一小节将生成所有的 $2^0$ ~ $2^{255}$的值来简化 GF(256) 中的乘法。

### 2. 日志和反日志

 在GF(256)中执行乘法只需要生成2的所有乘方， 以及相应的逆对数。

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

因为这些值不会变，所以可以用数组保存起来，直接使用即可。

```java
/**
 * 获取一个key的幂
 * @param k
 * @return
 */
public static int getPower(int k){
    int powers[] = {1, 2, 4, 8, 16, 32, 64, 128, 29, 58, 116, 232, 205, 135, 19, 38, 76, 152, 45, 90, 180, 117, 234, 201, 143, 3, 6, 12, 24, 48, 96, 192, 157, 39, 78, 156, 37, 74, 148, 53, 106, 212, 181, 119, 238, 193, 159, 35, 70, 140, 5, 10, 20, 40, 80, 160, 93, 186, 105, 210, 185, 111, 222, 161, 95, 190, 97, 194, 153, 47, 94, 188, 101, 202, 137, 15, 30, 60, 120, 240, 253, 231, 211, 187, 107, 214, 177, 127, 254, 225, 223, 163, 91, 182, 113, 226, 217, 175, 67, 134, 17, 34, 68, 136, 13, 26, 52, 104, 208, 189, 103, 206, 129, 31, 62, 124, 248, 237, 199, 147, 59, 118, 236, 197, 151, 51, 102, 204, 133, 23, 46, 92, 184, 109, 218, 169, 79, 158, 33, 66, 132, 21, 42, 84, 168, 77, 154, 41, 82, 164, 85, 170, 73, 146, 57, 114, 228, 213, 183, 115, 230, 209, 191, 99, 198, 145, 63, 126, 252, 229, 215, 179, 123, 246, 241, 255, 227, 219, 171, 75, 150, 49, 98, 196, 149, 55, 110, 220, 165, 87, 174, 65, 130, 25, 50, 100, 200, 141, 7, 14, 28, 56, 112, 224, 221, 167, 83, 166, 81, 162, 89, 178, 121, 242, 249, 239, 195, 155, 43, 86, 172, 69, 138, 9, 18, 36, 72, 144, 61, 122, 244, 245, 247, 243, 251, 235, 203, 139, 11, 22, 44, 88, 176, 125, 250, 233, 207, 131, 27, 54, 108, 216, 173, 71, 142, 1};
    return powers[k];
}

/**
 * 获取key的逆log
 * @param k
 * @return
 */
public static int getAntilog(int k){
    // antiLog[0] 不存在,负无穷
    int antiLog[] = {-2147483648, 0, 1, 25, 2, 50, 26, 198, 3, 223, 51, 238, 27, 104, 199, 75, 4, 100, 224, 14, 52, 141, 239, 129, 28, 193, 105, 248, 200, 8, 76, 113, 5, 138, 101, 47, 225, 36, 15, 33, 53, 147, 142, 218, 240, 18, 130, 69, 29, 181, 194, 125, 106, 39, 249, 185, 201, 154, 9, 120, 77, 228, 114, 166, 6, 191, 139, 98, 102, 221, 48, 253, 226, 152, 37, 179, 16, 145, 34, 136, 54, 208, 148, 206, 143, 150, 219, 189, 241, 210, 19, 92, 131, 56, 70, 64, 30, 66, 182, 163, 195, 72, 126, 110, 107, 58, 40, 84, 250, 133, 186, 61, 202, 94, 155, 159, 10, 21, 121, 43, 78, 212, 229, 172, 115, 243, 167, 87, 7, 112, 192, 247, 140, 128, 99, 13, 103, 74, 222, 237, 49, 197, 254, 24, 227, 165, 153, 119, 38, 184, 180, 124, 17, 68, 146, 217, 35, 32, 137, 46, 55, 63, 209, 91, 149, 188, 207, 205, 144, 135, 151, 178, 220, 252, 190, 97, 242, 86, 211, 171, 20, 42, 93, 158, 132, 60, 57, 83, 71, 109, 65, 162, 31, 45, 67, 216, 183, 123, 164, 118, 196, 23, 73, 236, 127, 12, 111, 246, 108, 161, 59, 82, 41, 157, 85, 170, 251, 96, 134, 177, 187, 204, 62, 90, 203, 89, 95, 176, 156, 169, 160, 81, 11, 245, 22, 235, 122, 117, 44, 215, 79, 174, 213, 233, 230, 231, 173, 232, 116, 214, 244, 234, 168, 80, 88, 175};
    return antiLog[k];
}

```



### 3. 多项式加减乘

#### 3.1 普通多项式的四则运算

加法部分思路参考 [多项式加法，用单链表实现](https://www.cnblogs.com/erlongi/p/10759921.html)

为什么说是普通多项式呢？因为GF(256) 的四则运算和算术上的四则运算是不同的。先来看看普通的多项式四则运算，具体实现略去（之后专门写一篇多项式的文章），这里看看运算的测试结果：

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

#### 3.2 GF(256) 中多项式的三则运算

为什么又是三则运算呢，因为除法比加减乘难一点，在下一节多项式生成好之后，下下一节再测试除法。（加减其实一样，因为 -n = n, 所以减就是加，都是执行异或操作。）

```java
public static void main(String[] args){
    PolynomialGF a = new PolynomialGF();
    a.addItem(3,2).addItem(0,1).addItem(90,3);
    a.addItem(10,2);
    a.addItem(7,0);
    System.out.println(a); // +2^{90}x^{3}+2^{115}x^{2}+x+2^{7}

    PolynomialGF b = new PolynomialGF();
    b.addItem(0,3).addItem(3,2).addItem(5,1).addItem(3,-3);
    System.out.println(b); // +x^{3}+2^{3}x^{2}+2^{5}x

    // 加法操作
    PolynomialGF c = a.add(b);
    System.out.println(c); // +2^{62}x^{3}+2^{10}x^{2}+2^{138}x+2^{7}

    // 乘法操作
    PolynomialGF d = a.mul(b);
    System.out.println(d); // +2^{90}x^{6}+2^{73}x^{5}+2^{225}x^{4}+2^{171}x^{3}+2^{143}x^{2}+2^{12}x
}
```



### 4. 消息多项式

消息多项式使用来自数据编码步骤的数据码字作为其系数。例如，如果转换为整数的数据码字是25、218和35，则消息多项式将是$25x^2 + 218x + 35$

生成消息多项式代码：

```java
public class xxx{
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

### 4. 生成器多项式

生成器多项式是通过相乘得到的多项式： $(x - α^0) ... (x - α^{n-1})$ 。

```java
public class xxx{
    public static void main(String[] args){
        // 生成器多项式 （2个纠错码）
        PolynomialGF gp = new ErrorCorrectionCoding().generatorPolynomial(2);
        System.out.println(gp); // +x^{2}+α^{25}x+α^{1}
    }

    /**
     * （n个错误纠正码的）生成器多项式 (x - α^0) ... (x - α^{n-1}) 
     * 递归实现，上一版本是普通实现
     * @param ECC_Number 纠错码字数(number of error correction codewords)
     * @return
     */
    public PolynomialGF generatorPolynomial(int ECC_Number){
        if(ECC_Number<1){
            return null;
        }

        PolynomialGF t = new PolynomialGF();

        // n=0
        if(ECC_Number==1){
            t.addItem(0, 1).addItem(0,0);
            return t;
        }else{
            t = new PolynomialGF().addItem(0, 1).addItem(ECC_Number-1,0);
            return t.mul(generatorPolynomial(ECC_Number-1));
        }
    }
}
```



### 5. 多项式的除法（用生成器多项式除消息多项式示例）

```java
public static void main(String[] args){

    // 纠错码数
    int e_n = 10;

    // 消息多项式
    int[] code = {32, 91, 11, 120, 209, 114, 220, 77, 67, 64, 236, 17, 236, 17, 236, 17};
    PolynomialGF mp = new ErrorCorrectionCoding().messagePolynomial(code);
    System.out.println(mp); // +α^{5}x^{15}+α^{92}x^{14}+α^{238}x^{13}+α^{78}x^{12}+α^{161}x^{11}+α^{155}x^{10}+α^{187}x^{9}+α^{145}x^{8}+α^{98}x^{7}+α^{6}x^{6}+α^{122}x^{5}+α^{100}x^{4}+α^{122}x^{3}+α^{100}x^{2}+α^{122}x+α^{100}
    
    // 生成器多项式 （10个纠错码）
    PolynomialGF gp = new ErrorCorrectionCoding().generatorPolynomial(e_n);
    System.out.println(gp); // +x^{10}+α^{251}x^{9}+α^{67}x^{8}+α^{46}x^{7}+α^{61}x^{6}+α^{118}x^{5}+α^{70}x^{4}+α^{64}x^{3}+α^{94}x^{2}+α^{32}x+α^{45}

    // 消息多项式 * x^n
    PolynomialGF xn = new PolynomialGF(new Node(0, e_n));
    mp = mp.mul(xn);
    System.out.println(mp); // +α^{5}x^{25}+α^{92}x^{24}+α^{238}x^{23}+α^{78}x^{22}+α^{161}x^{21}+α^{155}x^{20}+α^{187}x^{19}+α^{145}x^{18}+α^{98}x^{17}+α^{6}x^{16}+α^{122}x^{15}+α^{100}x^{14}+α^{122}x^{13}+α^{100}x^{12}+α^{122}x^{11}+α^{100}x^{10}

    System.out.println("多项式除法，商和余数分别为：");
    PolynomialGF quotient = new PolynomialGF(), remainder = new PolynomialGF();
    mp.div(gp, quotient, remainder);
    Coef.outputFormatValue = true; // 设置系数的输出格式
    System.out.println(quotient); // +32x^{15}+89x^{14}+61x^{13}+138x^{12}+243x^{11}+149x^{10}+135x^{9}+183x^{8}+59x^{7}+184x^{6}+51x^{5}+41x^{4}+179x^{3}+70x^{2}+84x+107
    System.out.println(remainder); // +196x^{9}+35x^{8}+39x^{7}+119x^{6}+235x^{5}+215x^{4}+231x^{3}+226x^{2}+93x+23

    System.out.println("纠错码为：");
    int[] e = remainder.getCoefs();
    System.out.println(Arrays.toString(e)); // [196, 35, 39, 119, 235, 215, 231, 226, 93, 23]
}
```

**首先准备消息多项式和生成器多项式**

消息多项式应该是：
$+32x^{15}+91x^{14}+11x^{13}+120x^{12}+209x^{11}+114x^{10}+220x^{9}+77x^{8}+67x^{7}+64x^{6}+236x^{5}+17x^{4}+236x^{3}+17x^{2}+236x+17$

为了统一格式，将每一项的系数是数值转成了 $α^n$ (α=2) 的格式：

$+α^{5}x^{15}+α^{92}x^{14}+α^{238}x^{13}+α^{78}x^{12}+α^{161}x^{11}+α^{155}x^{10}+α^{187}x^{9}+α^{145}x^{8}+α^{98}x^{7}+α^{6}x^{6}+α^{122}x^{5}+α^{100}x^{4}+α^{122}x^{3}+α^{100}x^{2}+α^{122}x+α^{100}$

为了确保前导项的指数在除法期间不会变得太小，将消息多项式乘以 $x^n$，其中n是所需的错误纠正码的数量。在这种情况下，n是10，对于10个纠错码，将消息多项式乘以x10，得到 

$+α^{5}x^{25}+α^{92}x^{24}+α^{238}x^{23}+α^{78}x^{22}+α^{161}x^{21}+α^{155}x^{20}+α^{187}x^{19}+α^{145}x^{18}+α^{98}x^{17}+α^{6}x^{16}+α^{122}x^{15}+α^{100}x^{14}+α^{122}x^{13}+α^{100}x^{12}+α^{122}x^{11}+α^{100}x^{10}$



生成器多项式(纠错码数为10)是：
$+x^{10}+α^{251}x^{9}+α^{216}x^{8}+α^{78}x^{7}+α^{69}x^{6}+α^{76}x^{5}+α^{114}x^{4}+α^{64}x^{3}+α^{94}x^{2}+α^{32}x+α^{45}$​

**使用多项式除法计算出余数**

$+196x^{9}+35x^{8}+39x^{7}+119x^{6}+235x^{5}+215x^{4}+231x^{3}+226x^{2}+93x+23$

余数的系数就是要生成的纠错码：

[196, 35, 39, 119, 235, 215, 231, 226, 93, 23]



### 6. 生成纠错码方法封装

```java
/**
 * 生成纠错码
 * @param dataCode data codewords 数据码
 * @param eNum  error correction codewords 纠错码个数
 * @return
 */
public static int[] getCode(int dataCode[], int eNum){
    if(eNum<1 || dataCode.length<1){
        return null;
    }

    // 消息多项式
    PolynomialGF mp = new ErrorCorrectionCoding().messagePolynomial(dataCode);

    // 生成器多项式 （nNum个纠错码）
    PolynomialGF gp = new ErrorCorrectionCoding().generatorPolynomial(eNum);

    // 消息多项式 * x^n
    PolynomialGF xn = new PolynomialGF(new Node(0, eNum));
    mp = mp.mul(xn);

    // 多项式除法，求余数");
    PolynomialGF quotient = new PolynomialGF();
    PolynomialGF remainder = new PolynomialGF();
    mp.div(gp, quotient, remainder);

    // 纠错码
    int[] e = remainder.getCoefs();
    return e;
}

---------------------
封装后调用：
// 数据码 和 纠错码数
int [] codeWords = {32, 91, 11, 120, 209, 114, 220, 77, 67, 64, 236, 17, 236, 17, 236, 17};
int e_n = 10;
getCode(codeWords, e_n))
```

bingo!