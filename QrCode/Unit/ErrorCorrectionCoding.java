package Unit;

import java.util.Arrays;

/**
 * 纠错算法
 */
public class ErrorCorrectionCoding{
    public static void main(String[] args){

        // 数据码 和 纠错码数
        int [] codeWords = {32, 91, 11, 120, 209, 114, 220, 77, 67, 64, 236, 17, 236, 17, 236, 17};
        int e_n = 10;

        // 消息多项式
        PolynomialGF mp = new ErrorCorrectionCoding().messagePolynomial(codeWords);
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
        
        // 封装后
        System.out.println(Arrays.toString(getCode(codeWords, e_n)));
    }

    /**
     * 消息多项式
     * @param code
     * @return
     */
    public PolynomialGF messagePolynomial(int[] code){
        PolynomialGF m = new PolynomialGF();
        for(int i=0; i < code.length; i++){
            m.addItem(code[i], code.length-i-1, true);
        }

        return m;
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
            Log.getLogger().info("add Item: " + t);

            return t;
        }else{
            t = new PolynomialGF().addItem(0, 1).addItem(ECC_Number-1,0);
            Log.getLogger().info("add Item: " + t);

            return t.mul(generatorPolynomial(ECC_Number-1));
        }
        
    }
    
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


}