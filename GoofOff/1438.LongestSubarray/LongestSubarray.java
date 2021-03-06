
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 题目：绝对差不超过限制的最长连续子数组
 * 描述：一个整数数组 nums ，和一个表示限制的整数 limit，请你返回最长连续子数组的长度，该子数组中的任意两个元素之间的绝对差必须小于或者等于 limit。如果不存在满足条件的子数组，则返回 0。
 * 
 * 思路：滑动窗口
 * 
 */

class Solution {

    SortedMap<Integer, Integer> sub;
    int total;

    public Solution(){
        sub = new TreeMap<>();
        total = 0;
    }
    private boolean subtractItem(int key){
        Integer i = sub.get(key);
        if(i == null){
            System.out.println(key);
            return false;
        }
        total--;
        if(i == 1){
            sub.remove(key);
            return false;
        }else{
            sub.merge(key, -1, Integer::sum);
            return true;
        }

    }

    public int longestSubarray(int[] nums, int limit) {
        int maxLen = 0;


        int min=0, max=0;
        int left=0, right=0, d=-1;
        while( left<nums.length ){
            // 首次
            if(d<0){
                sub.put(nums[0], 1);
                total++;
                min=max=nums[0];
            }else{
                // 右侧增加一项
                sub.merge(nums[right],1, Integer::sum);
                total++;
                // if(nums[right]>=min && nums[right]<=max && max-min<=limit){
                //     System.out.printf("O");
                // }else if(nums[right]>=max && nums[right]-min<=limit){
                //     System.out.printf(">");

                // }else if(nums[right]<=min && max-nums[right]<=limit){
                //     System.out.printf("<");
                // }else{
                //     // 右移,删除左侧项
                //     subtractItem(nums[left++]);
                    
                //     System.out.printf("-");
                // }


                min = sub.firstKey();
                max = sub.lastKey();
                
                if(max-min > limit){
                    subtractItem(nums[left++]);
                    min = sub.firstKey();
                    max = sub.lastKey();
                }

            }
            d = max - min;
            
            //System.out.printf("%b,(%d)(%d) %d %d %d (d=%d-%d)diff=%d<%d  maxlen=%d<%d\n",(d<=limit && maxLen<total), 
            //    a,nums[right],             
            //    left, right, sub.size(), max,min,d, limit , maxLen,total);

            if(d<=limit && maxLen<total){
                maxLen = total;
            }
            
            if(right++>=nums.length-1){
                break;
            }

        }

        System.out.println(maxLen);
        return maxLen;
    }
}
class In{
    int nums[];
    int limit;
    int result;
    public In(int n[], int limit, int result){
        this.nums = n;
        this.limit = limit;
        this.result = result;
    }
}

class KLengthApartTest{
    /**
     * 读取5号的大文件
     * @return
     * @throws IOException
     */
    public static int[] getData() throws IOException{
        Path ps = Paths.get("./data5.txt");
        String fileData = Files.readString(ps);

        String data5s[] = fileData.split(",");
        int data5[] = new int[data5s.length];
        for(int i=0; i<data5.length; i++){
            data5[i] = Integer.valueOf(data5s[i]);
        }
        return data5;
    }
    public static void main(String[] args) throws IOException{

        In inputs[] = {
            new In(new int[]{8,2,4,7}, 4, 2),
            new In(new int[]{10,1,2,4,7,2}, 5, 4),
            new In(new int[]{4,2,2,2,4,4,2,2}, 0, 3),
            new In(new int[]{6559,7634,2021,5158,4294,2267,2774,5406,2157,2957,8688,5049,3839,9084,7003,7669,7504,8364,9047,5097,2782,8519,8312,5060,9700,3388,4630,2533,106,5579,1951,6971,6227,8402,2932,7202,7364,8737,7248,5960,3382,886,1396,1759,1595,8819,6350,2250,7265,6508,2982,7783,7356,8552,9726,597,9620,3178,9674,7373,8001,6386,4764,5715,2844,9796,4478,2956,452,9117,993,1064,9013,3766,7302,5227,7236,5494,1666,6363,882,7673,4559,6708,7624,5428,8315,1574,2691,6575,8865,7000,7828,8283,2441,5235,2392,5734,2249,3171,3686,3541,7439,2552,1695,1687,6967,862,7432,2486,6137,9179,5270,4590,6522,233,6350,7981,7297,4943,4966,9523,6341,5131,4746,2854,1644,8018,2945,7304,2513,7516,1729,8817,2022,8785,5220,5184,8099,9159,5588,9526,5240,9214,9705,7580,5297,7937,6458,8787,3574,2722,3949,8791,3280,9741,4020,846,5259,1009,5379,6878,487,5641,5891,5924,9835,9754,6699,3427,4729,3680,5138,6512,6301,2878,138,6369,5738,9900,3641,3833,1080,5248,6303,3339,4812,1569,7111,65,5752,1528,6683,2511,1826,8743,2940,5577,2364,6154,7160,5334,8884,8568,4542,3418,3178,2595,2700,8813,2630,2404,1964,7243,9584,8555,2129,7067,2193,5470,9941,5199,9744,2247,340,5876,2854,3708,3851,8143,9707,8008,562,1471,2189,8731,7683,9254,2340,3425,5907,2259,4589,5711,1063,6286,7786,489,8668,7566,3228,3947,3378,83,4982,691,4375,8503,3104,1178,1735,1805,6559,5438,1718,7298,9450,8565,7912,4598,2339,7065,6087,5717,6287,6741,7147,6031,8995,3375,3206,1070,2373,3900,3932,342,3950,6449,7484,7222,9303,1563,888,2824,8639,125,727,7049,4563,6807,2176,3862,6134,6795,5601,9585,775,8296,7447,2118,8572,5965,9581,985,5724,1921,4022,2041,7156,2437,313,5988,2129,2466,4723,406,7740,439,7915,1100,9661,7062,1505,7698,8915,9875,8214,1588,2109,8826,6449,9864,8928,6720,3962,8575,6224,7825,5198,7174,1914,1102,3452,9697,6051,1710,1576,5815,1732,3210,1821,9681,1434,59,8395,7076,3842,1499,5036,7989,1013,9396,7030,9183,4887,6424,671,9753,475,4535,7838,7175,3587,4402,5270,7827,7236,8750,897,4406,8417,2848,7176,7464,4845,9588,9666,2992,5259,8356,6518,6780,9175,9780,6528,7822,3616,5020,281,1035,2423,8087,1898,5892,4247,5068,8868,4965,2270,1751,8223,2261,7457,623,7304,7695,9335,5337,8880,6084,2051,240,8811,3302,4402,1062,7569,4642,197,4357,8224,355,9269,6578,1836,1604,5280,9920,9386,7330,1500,8166,8680,5632,9651,716,3080,2779,911,1906,700,1922,9095,8680,4999,3286,2653,8738,2461,3735,3561,1470,8253,5782,9220,7133,4372,2207,4685,9466,4066,1140,9766,4340,932,357,7076,4626,7777,6913,7152,1113,3036,3520,558,7036,6792,5813,5816,8383,2429,2932,3703,3760,973,5989,1098,7315,5274,3566,3590,4223,2540,8536,6250,1757,7826,31,7736,5115,4301,4771,3417,2176,6235,574,6268,7489,8771,399,2160,3795,8081,1624,4851,7146,3292,8455,5472,1612,4077,3973,8056,9420,1900,2913,8135,5874,9745,7098,6559,9071,6912,391,6542,2319,6976,2085,994,4792,6368,7046,1563,3303,9802,4447,7845,9756,6919,4332,8335,1747,5339,2508,9195,8813,4255,402,9201,1591,6075,7442,4341,1552,4655,2278,1388,6658,6234,457,7847,9548,2130,9177,6413,8061,3818,8346,462,6161,1014,6727,9843,1377,4094,650,7444,1373,4811,649,5696,696,1120,1200,743,9580,5026,5808,5045,1488,8806}, 6706, 17),
            new In(getData(), 6466408, 28),
            new In(new int[]{8,7,4,2,8,1,7,7}, 8, 8),
        };
        for(In input : inputs){
            Solution s = new Solution();

            System.out.println(input.result==s.longestSubarray(input.nums, input.limit));
        }
    }
}