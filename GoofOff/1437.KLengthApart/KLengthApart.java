/**
 * 题目：所有的1的间隔是否至少为k
 * 描述：给你一个由若干 0 和 1 组成的数组 nums 以及整数 k。如果所有 1 都至少相隔 k 个元素，则返回 True ；否则，返回 False。
 * 思路：获取到左侧的1和右侧的1，则计算间隔并比对（小于k则返回false）。计算之后右侧变左侧，继续寻找下一个1。
 */
class Solution {
    public boolean kLengthApart(int[] nums, int k) {
        int total = 0;
        int prev = -1;
        int next = -1;

        for(int i=0; i<nums.length; i++){

            if(prev==-1 && nums[i]==1){
                prev=i;
                total=0;
            }else if(prev>-1 && nums[i]==1 ){
                next=i;
            }else{
                total++;
            }
            //System.out.printf("%d %d %d %d\n",nums[i], prev,next,total);

            if(prev>-1 && next>-1){
                //System.out.printf("-- %d %d %d\n",prev,next,total);
                if(total<k) return false;
                prev=next;
                next=-1;
                total=0;
            }
        }
        return true;
    }
}

class In{
    int nums[];
    int k;
    boolean result;
    public In(int n[], int k, boolean result){
        this.nums = n;
        this.k = k;
        this.result = result;
    }
}

class KLengthApartTest{
    public static void main(String[] args){
        Solution s = new Solution();

        In inputs[] = {
            new In(new int[]{1,0,0,0,1,0,0,1}, 2, true),
            new In(new int[]{1,0,0,1,0,1}, 2, false),
            new In(new int[]{1,1,1,1,1}, 0, true),
            new In(new int[]{0,1,0,1}, 1, true),
            new In(new int[]{1,0,1}, 2, false)
        };
        for(In input : inputs){
            System.out.println(input.result==s.kLengthApart(input.nums, input.k));
        }
    }
}