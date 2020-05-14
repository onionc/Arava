
/**
 * 题目：只出现一次的数字
 * 描述：int数组中除了某个元素只出现了一次外，其余每个元素均出现两次，找出那个例外。
 * 要求：O(n) 复杂度；不使用额外空间。注https://blog.csdn.net/skyline_sun/article/details/81911998，不使用额外空间则代表空间复杂度O(1)
 * 思路：想了几分钟突然想到用异或，相同的值因为相同的位抵消，中间过程中有不同的值的位会被记录，a^b^a=b。
 */

class Solution {
    public int singleNumber(int[] nums) {
        int x=0;
        for(int i:nums){
            x ^= i;
        }        
        return x;
    }
}

class SingleNumberTest{
    public static void main(String[] args){
        Solution s = new Solution();
        int input[][] = {
            {2,2,1},
            {4,1,2,1,2}
        };
        
        for(int r[] : input){
            System.out.println(s.singleNumber(r));
        }
    }
}