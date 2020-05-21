/**
 * 题目：5. 最长回文子串
 * 描述：给定一个字符串 s，找到 s 中最长的回文子串
 * 思路：标签中有一个动态规划的标签，看了下不太了解。
 * 准备找到最小的串AA,或者ABA,再向外扩展。47-52ms感觉还是略暴力，不优雅。

 */

class Solution {
    String s;
    int len;


    private boolean compare(int a, int b){
        return s.charAt(a) == s.charAt(b);
    }
    public String longestPalindrome(String s) {
        this.s = s;
        this.len = s.length();
        int width=1;

        if(this.len<=1){
            return s;
        }

        int maxLeft=0;
        int w=0;
        for(int left=0; left<this.len-1;){
            
            if(left+2<this.len){
                w = explode(left, left+2);
                
                if((2*w+1)>width){
                    maxLeft = left-w+1;
                    width = 2*w+1;
                }
            }

            w = explode(left, ++left);
            if(2*w>width){
                maxLeft = left-w;
                width =  (2*w);
            }
        }
        return s.substring(maxLeft, maxLeft+width);
    }

    private int explode(int left,int right){
        int w=0;
        while(left>=0 && right<len){
            if(!compare(left, right)) break;
            else{
                left--;
                right++;
                w++;
            }
            
        }
        return w;
    }
}

class In{
    String data;
    String result;
    public In(String input, String result){
        this.data = input;
        this.result = result;
    }
}

class LongestPalindromeTest{

    public static void main(String[] args) {

        In inputs[] = {
            new In("b", "b"),
            new In("bb", "bb"),
            new In("bab", "bab"),
            new In("babad", "bab"),
            new In("cbbd", "bb"),
            new In("123456654", "456654"),
            new In("14565422", "45654"),
            new In("", ""),
            new In("aaaa", "aaaa"),
        };
        for(In input : inputs){
            Solution s = new Solution();
            System.out.printf(input.data + "\t");
            System.out.println(input.result.equals(s.longestPalindrome(input.data)));
        }
    }
}