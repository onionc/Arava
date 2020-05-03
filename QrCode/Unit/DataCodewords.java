package Unit;

import java.util.Arrays;

/**
 * 数据码字 Data Code words
 */
public class DataCodewords {
    
    /**
     * 生成纠错码
     * @param dataEncoding 数据编码
     * @param version 版本
     * @param level 容错等级
     * @return
     */
    public static int[][] getDC(String dataEncoding, int version, Data.LEVEL level){
        // 结果
        int dcFullData[][][]; // 三维数据 [group][block][dcArr] 存储的数据码字，预留
        int dcData[][]; // 二维数组存储的数据码字 [][dcArr]
        
        int[] eccAndBlocks = Data.ECC_AND_BLOCKS[(version-1)*4+level.ordinal()];
        // 获取分组（group = 1 or 2），每一组下的块个数（block）和每块的字数(DcNum, Number of Data Codewords)
        int g1Block = eccAndBlocks[Data.ECC_AND_BLOCKS_COLUMN.G1_BLOCKS_NUM.ordinal()];
        int g1DcNum = eccAndBlocks[Data.ECC_AND_BLOCKS_COLUMN.G1_DC_NUM.ordinal()];
        int g2Block = eccAndBlocks[Data.ECC_AND_BLOCKS_COLUMN.G2_BLOCKS_NUM.ordinal()];
        int g2DcNum = eccAndBlocks[Data.ECC_AND_BLOCKS_COLUMN.G2_DC_NUM.ordinal()];
        int groups = g2Block>0 ? 2 : 1;

        dcData = new int[g1Block+g2Block][];
        int dcIndex = 0;

        int flag = 0;
        // 根据每个分组下的块进行分组
        dcFullData = new int[groups][][]; // 初始化ecc数据数组
        dcFullData[0] = new int[g1Block][g1DcNum];
        for(int i=0; i<g1Block; i++){
            // System.out.printf("%d %d %d %d\n", dataEncoding.length(), flag, g1DcNum, flag+g1DcNum*8);
            dcFullData[0][i] = Common.getIntByStr(dataEncoding.substring(flag, flag+g1DcNum*8));
            flag += g1DcNum*8;

            dcData[dcIndex++] = dcFullData[0][i].clone();
        }
        if(groups==2){ // 2 group
            dcFullData[1] = new int[g2Block][g2DcNum];
            for(int i=0; i<g2Block; i++){
                // System.out.printf("%d %d %d\n", flag, g2DcNum, flag+g2DcNum*8);
                dcFullData[1][i] = Common.getIntByStr(dataEncoding.substring(flag, flag+g2DcNum*8));
                flag += g2DcNum*8;

                dcData[dcIndex++] = dcFullData[1][i].clone();
            }
        }
        
        return dcData;
    }

    
    public static void main(String[] args){
        /*
        // 测试 HELLO WORLD 编码数据 1-M
        String s2 = "00100000010110110000101101111000110100010111001011011100010011010100001101000000111011000001000111101100000100011110110000010001";
        int r2[][] = DataCodewords.getDC(s2, 1, Data.LEVEL.M);
        System.out.println(Arrays.deepToString(r2)); // [[32, 91, 11, 120, 209, 114, 220, 77, 67, 64, 236, 17, 236, 17, 236, 17]]
        // 测试 HELLO WORLD 编码数据 1-Q
        String s3 = "00100000010110110000101101111000110100010111001011011100010011010100001101000000111011000001000111101100";
        int r3[][] = DataCodewords.getDC(s3, 1, Data.LEVEL.Q);
        System.out.println(Arrays.deepToString(r3)); // [[32, 91, 11, 120, 209, 114, 220, 77, 67, 64, 236, 17, 236]]
        */

        // 测试示例数据 5-Q
        String s1 = "0100001101010101010001101000011001010111001001100101010111000010011101110011001000000110000100100000011001100111001001101111011011110110010000100000011101110110100001101111001000000111001001100101011000010110110001101100011110010010000001101011011011100110111101110111011100110010000001110111011010000110010101110010011001010010000001101000011010010111001100100000011101000110111101110111011001010110110000100000011010010111001100101110000011101100000100011110110000010001111011000001000111101100";
        int r[][] = DataCodewords.getDC(s1, 5, Data.LEVEL.Q);
        System.out.println(Arrays.deepToString(r)); //[[67, 85, 70, 134, 87, 38, 85, 194, 119, 50, 6, 18, 6, 103, 38], [246, 246, 66, 7, 118, 134, 242, 7, 38, 86, 22, 198, 199, 146, 6], [182, 230, 247, 119, 50, 7, 118, 134, 87, 38, 82, 6, 134, 151, 50, 7], [70, 247, 118, 86, 194, 6, 151, 50, 224, 236, 17, 236, 17, 236, 17, 236]]
        // 交错 DC 算法
        int r2[] = new int[s1.length()/8];
        int ri=0;
        int i=0,j=0,imax=r.length;
        int noFlag = 0;
        while(true){
            if(noFlag==imax){
                break;
            }

            if(i<imax && j<r[i].length){
                r2[ri++] = r[i][j];
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
        System.out.println(Arrays.toString(r2));
        

        // 用上面数据生成纠错码
        // for(int group[] : r){
        //     for(int block[] : group){
        //        // System.out.println(Arrays.toString(ErrorCorrectionCoding.getCode(block, 18)));
        //     }
        // }
        
        
    }

}