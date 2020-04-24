##  纠错算法
首先介绍伽罗瓦域，接着使用日志和反日志来简化GF(256)中的乘法。
因为纠错算法中需要使用多项式进行运算，需要处理多项式的四则运算。
纠错编码的多项式长除法，使用两种多项式，一个是消息多项式 (message polynomial)，另一个是生成器多项式 (generator polynomial)。消息多项式将被一个生成器多项式除；生成器多项式是通过相乘得到的多项式 $(x - α^0) ... (x - α^{n-1})$ 。

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

### 3. 多项式加减乘

### 4. 消息多项式

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

### 4. 生成器多项式

生成器多项式是通过相乘得到的多项式： $(x - α^0) ... (x - α^{n-1})$ 。

TODO

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

5.1 首先准备消息多项式和生成器多项式

消息多项式应该是：
$+32x^{15}+91x^{14}+11x^{13}+120x^{12}+209x^{11}+114x^{10}+220x^{9}+77x^{8}+67x^{7}+64x^{6}+236x^{5}+17x^{4}+236x^{3}+17x^{2}+236x+17$

为了统一格式，将每一项的系数是数值转成了 $α^n$ (α=2) 的格式：

$+α^{5}x^{15}+α^{92}x^{14}+α^{238}x^{13}+α^{78}x^{12}+α^{161}x^{11}+α^{155}x^{10}+α^{187}x^{9}+α^{145}x^{8}+α^{98}x^{7}+α^{6}x^{6}+α^{122}x^{5}+α^{100}x^{4}+α^{122}x^{3}+α^{100}x^{2}+α^{122}x+α^{100}$

为了确保前导项的指数在除法期间不会变得太小，将消息多项式乘以 $x^n$，其中n是所需的错误纠正码的数量。在这种情况下，n是10，对于10个纠错码，将消息多项式乘以x10，得到 

$+α^{5}x^{25}+α^{92}x^{24}+α^{238}x^{23}+α^{78}x^{22}+α^{161}x^{21}+α^{155}x^{20}+α^{187}x^{19}+α^{145}x^{18}+α^{98}x^{17}+α^{6}x^{16}+α^{122}x^{15}+α^{100}x^{14}+α^{122}x^{13}+α^{100}x^{12}+α^{122}x^{11}+α^{100}x^{10}$



生成器多项式(纠错码数为10)是：
$+x^{10}+α^{251}x^{9}+α^{216}x^{8}+α^{78}x^{7}+α^{69}x^{6}+α^{76}x^{5}+α^{114}x^{4}+α^{64}x^{3}+α^{94}x^{2}+α^{32}x+α^{45}$​

5.2 使用多项式除法计算出余数

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
```

