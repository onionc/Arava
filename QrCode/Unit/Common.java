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
            int num = s.charAt(i)-'0' == 1 ? 1 : 0;
            total = (total<<1)+ num;
        }
        return total;
    }

    /**
     * 交错读取数据。从二维数组交错读取数据，转为一维数组
     * @param data 原数据
     * @return
     */
    public static int[] interleaveData(int data[][]){
        // 求出总长度
        int length = 0;
        for(int r[] : data){
            length+=r.length;
        }

        int r2[] = new int[length]; // 结果
        int ri=0; // r2的索引
        int i=0,j=0,imax=data.length; // imax为数据行
        int noFlag = 0; // 无数据标志
        while(noFlag<imax){ // 当每一行都没有获取到数据 noFlag>=imax，则结束
            if(i<imax && j<data[i].length){
                r2[ri++] = data[i][j];
                noFlag=0;
            }else{
                noFlag++;
            }
            i++;
            if(i>=imax){
                i=0;
                j++;
            }
        }
        return r2;
    }

    /**
     * 一维数组升维到二维
     * @param sourceData
     * @param row
     * @param column
     * @param priorityRow 优先模式：true 行优先, false 列优先
     * @return
     */
    public static int[][] ascendToArr(int sourceData[], int row, int column, boolean priorityRow){
        int data[][] = new int[row][column];
        int si = 0;
        if(priorityRow){
            for(int i=0; i<row; i++){
                for(int j=0; j<column; j++){
                    if(si<sourceData.length){
                        data[i][j] = sourceData[si++];
                    }
                }
            }
        }else{
            for(int i=0; i<column; i++){
                for(int j=0; j<row; j++){
                    if(si<sourceData.length){
                        data[j][i] = sourceData[si++];
                    }
                }
            }
        }
        
        return data;
    } 

    /**
     * 合并（拼接）两个数组
     * @param a
     * @param b
     * @return
     */
    public static int[] joinArr(int a[], int b[]){
        int r[] = new int[a.length+b.length];
        int i=0;
        for(int ai:a){
            r[i++] = ai;
        }
        for(int bi:b){
            r[i++] = bi;
        }
        return r;
    }

    /**
     * 将int类型的二维数组，转为boolean类型（0 -> false, !0 -> true）
     * @param data
     * @return
     */
    public static boolean[][] intToBoolInMatrix(int data[][]){
        boolean b[][] = new boolean[data.length][];
        for(int i=0; i<data.length; i++){
            b[i] = new boolean[data[i].length];
            for(int j=0; j<data[i].length; j++){
                b[i][j] = data[i][j]==0 ? false : true;
            }
        }
        return b;
    }

    /**
     * 在二维数组data中的[x,y]位置开始填充数据 fillData
     * @param data
     * @param x
     * @param y
     * @param fillData
     */
    public static void setMatrix(int data[][], int x, int y, int fillData[][]){
        for(int i=x; i<data.length; i++){
            for(int j=y; j<data[i].length; j++){
                // fillData[i-x][j-y] 存在
                // System.out.printf("%d %d = %d %d\n",i,j,i-x,j-y);
                if(i-x<fillData.length && j-y<fillData[i-x].length){
                    data[i][j] = fillData[i-x][j-y];
                }else if(i-x>=fillData.length){
                    return;
                }else if(j-y>=fillData[i-x].length){
                    break;
                }
            }
        }
    }

    /**
     * 在二维数组data中的[x,y]位置开始填充数据 fillData, 包含数据验证
     * 数据为-1才可以写入，或者要写入的块和原数据一致。否则原数组不变，返回false。
     * @param data
     * @param x
     * @param y
     * @param fillData
     */
    public static boolean setMatrixExcept(int data[][], int x, int y, int fillData[][]){
        int temp[][] = new int[data.length][];
        for(int i=0; i<data.length; i++){
            temp[i] = data[i].clone();
        }
        
        end:
        for(int i=x; i<data.length; i++){
            for(int j=y; j<data[i].length; j++){
                // fillData[i-x][j-y] 存在
                if(i-x<fillData.length && j-y<fillData[i-x].length){
                    if(data[i][j]!=-1 && data[i][j]!=fillData[i-x][j-y]) return false; // 当前位置为底色并且要填充的数据不一样，则退出。（如果和填充的数据一样，则没关系，可以覆盖）
                    temp[i][j] = fillData[i-x][j-y];
                }else if(i-x>=fillData.length){
                    break end; // 跳过后续执行
                }else if(j-y>=fillData[i-x].length){
                    break; // 跳过本行数据
                }
            }
        }
        
        for(int i=0; i<data.length; i++){
            data[i] = temp[i].clone();
        }

        return true;
    }

    /**
     * 通过字符串数组生成二维数组
     * 每个字符串生成一维int数组，字符串数组则生成二维数组
     * @param sA
     * @return
     */
    public static int[][] genMatrixByStr(String sA[]){
        int data[][] = new int[sA.length][];
        for(int i=0; i<sA.length; i++){
            data[i] = Common.strSplitToInt(sA[i], 1);
        }
        return data;
    }

    /**
     * 通过行列和值生成二维数组
     * @param x
     * @param y
     * @param v
     * @return
     */
    public static int[][] genMatrix(int x, int y, int v2){
        int data[][] = new int[x][y];
        for(int i=0; i<x; i++){
            for(int j=0; j<y; j++){
                data[i][j] = v2;
            }
        }
        return data;
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
        System.out.println(Common.binStrToInt("010101011")); // 171

        // 字符串解析每个字节为int数组
        System.out.println(Arrays.toString(Common.getIntByStr("0101010110101010"))); // [85, 170]

        // 交错读取数据
        int data1[][] = {{1,2,3},{4,5},{6,7,8,9,0}};
        int data2[] = Common.interleaveData(data1);
        System.out.println(Arrays.toString(data2)); // [1, 4, 6, 2, 5, 7, 3, 8, 9, 0]

        // 合并两个数组
        int data3[] = {1,2,3};
        int data4[] = {4,5,6};
        System.out.println(Arrays.toString(Common.joinArr(data3, data4))); // [1, 2, 3, 4, 5, 6]

        // int类型二维数组转boolean
        int data5[][] = {{1, 1, 0}, {0, 5, 1}};
        boolean data6[][] =  Common.intToBoolInMatrix(data5);
        System.out.println(Arrays.deepToString(data6)); // [[true, true, false], [false, true, true]]
    
        // 二维数组替换
        int data7[][] = {
            {0,0,0,0},
            {0,0,0,0}
        };
        int data8[][] = {
            {1,2,3},
            {1,5}
        };
        Common.setMatrix(data7, 0, 2, data8);
        System.out.println(Arrays.deepToString(data7)); // [[0, 0, 1, 2], [0, 0, 1, 5]]

        // 通过字符串数组生成二维int数组
        String data9[] = {"2222"};
        System.out.println(Arrays.deepToString(Common.genMatrixByStr(data9))); // [[2, 2, 2, 2]]
        String data10[] = {"111", "122", "3"};
        System.out.println(Arrays.deepToString(Common.genMatrixByStr(data10))); // [[1, 1, 1], [1, 2, 2], [3]]
        // 通过行列信息生成二维数组
        System.out.println(Arrays.deepToString(Common.genMatrix(10,1,2))); // [[2], [2], [2], [2], [2], [2], [2], [2], [2], [2]]
        System.out.println(Arrays.deepToString(Common.genMatrix(1,10,2))); // [[2, 2, 2, 2, 2, 2, 2, 2, 2, 2]]

        // 一维数组升维到二维
        int data11[] = {1,2,3,4,5,6,7,8,9};
        System.out.println(Arrays.deepToString(Common.ascendToArr(data11, 3, 4, false))); // [[1, 4, 7, 0], [2, 5, 8, 0], [3, 6, 9, 0]]
        
    }
}