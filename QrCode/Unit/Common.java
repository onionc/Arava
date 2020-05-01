package Unit;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * 公共函数类
 */
public class Common {
    /**
     * 将一个十进制数据转二进制，不足指定宽度在左侧补0，返回字符串
     * 类似这种格式化的效果 (php 代码：sprintf("%'010b", 13);)
     * @param width 输出宽度。如果小于数据本身宽度，则取数据本身宽。所以只想转二进制但不知宽度时，可设置为1
     * @param value 十进制数据
     * @return 返回字符串
     */
    public static String formatBin(int width, int value){
        String bs = Integer.toBinaryString(value);
        return String.format("%"+width+"s", bs).toString().replace(' ', '0');
    }

    /**
     * 按指定长度分割字符串
     * @param s
     * @param length
     * @return
     */
    public static String[] strSplit(String s, int length){
        if(length<1){
            return null;
        }
        int sLen = s.length();
        int sum = sLen/length;
        if(sum*length<sLen) sum++;

        String r[] = new String[sum];
        for(int i=0,j=0; i<sLen; i+=length,j++){
            if(j<sum-1){
                r[j] = s.substring(i, i+length);
            }else{
                r[j] = s.substring(i);
            }
        }
        return r;
    }

    /**
     * 分割字符串，并把每一项转为int
     * @param s
     * @param length
     * @return
     */
    public static int[] strSplitToInt(String s, int length){
        String sa[] = Common.strSplit(s, length);
        int r[] = new int[sa.length];
        for(int i=0; i<sa.length; i++){
            try{
                r[i] = Integer.valueOf(sa[i]);
            }catch(NumberFormatException ignore){
                r[i] = 0;
            }
        }
        return r;
    }
    
    /**
     * 分割字符串，并把每一项转为二进制字符串，取了每一项多余的前缀0
     * @param s
     * @param length
     * @return
     */
    public static String[] strSplitToBin(String s, int length){
        int ia[] = Common.strSplitToInt(s, length);
        String r[] = new String[ia.length];
        for(int i=0; i<ia.length; i++){
            r[i] = Integer.toBinaryString(ia[i]);
        }
        return r;
    }

    /**
     * 获取字符串的 utf-8 字节码，按字节分割，返回int数组
     * @param s
     * @return
     */
    public static int[] getUtf8Bytes(String s){
        try{
            byte b[] = s.getBytes("utf-8");
            int bytes[] = new int[b.length];
            for(int i = 0; i < b.length; i++) {
                bytes[i] = b[i] & 0xFF;
                // System.out.printf("%x %s\n", bytes[i], Common.formatBin(10,bytes[i]));
            }
            return bytes;
        }catch(UnsupportedEncodingException e){
            return null;
        }
    }

    /**
     * 字符串转字节
     * @param s
     * @return 返回 二进制 字符串数组
     */
    public static String[] strToBytes(String s){
        int bytes[] = getUtf8Bytes(s);
        String r[] = new String[bytes.length];
        for(int i=0; i < bytes.length; i++){
            r[i] = Common.formatBin(8, bytes[i]);
        }
        return r;
    }

    /**
     * 将字符串s每八位转为int，返回数组
     * @param s
     * @return
     */
    public static int[] getIntByStr(String s){
        int r[] = new int[s.length()/8];
        for(int i=0; i<r.length; i++){
            r[i] = Common.binStrToInt(s.substring(i*8, (i+1)*8));
        }
        return r;
    }

    /**
     * bin 二进制字符串转 int
     * 注：溢出不处理
     * @param s
     * @return
     */
    public static int binStrToInt(String s){
        int total = 0;
        for(int i=0; i<s.length(); i++){
            int num = s.charAt(i)-'0' == 1?1:0;
            total = (total<<1)+ num;
        }
        return total;
    }

}


class CommonTest{
    public static void main(String[] args){
        // 十进制数字转二进制，不足左侧补0
        System.out.println(Common.formatBin(10, 13)); // 0000001101
        System.out.println(Common.formatBin(1, 13)); // 1101

        // 分割字符串
        String s1[] = Common.strSplit("hello", 2);
        System.out.println(Arrays.toString(s1)); // [he, ll, o] 

        // 分割字符串，并转为int
        int i1[] = Common.strSplitToInt("86705309", 3); // [867, 53, 9]
        System.out.println(Arrays.toString(i1));

        // 分割字符串，并在转为int之后转二进制字符串
        String b1[] = Common.strSplitToBin("86705309", 3); // [1101100011, 110101, 1001]
        System.out.println(Arrays.toString(b1));

        // 测试字符串的utf-8编码
        System.out.println(Arrays.toString(Common.getUtf8Bytes("H"))); // [72] 即0x48
        System.out.println(Arrays.toString(Common.getUtf8Bytes("中"))); // [228, 184, 173] = 0xe4b8ad
        // 字符串的utf-8编码，输出二进制
        System.out.println(Arrays.toString(Common.strToBytes("中"))); // [11100100, 10111000, 10101101]

        // 二进制字符串转Int
        System.out.println(Common.binStrToInt("010101011"));

        // 字符串解析每个字节为int数组
        
        System.out.println(Arrays.toString(Common.getIntByStr("0101010110101010")));

    }
}