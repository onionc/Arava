package Unit;

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