import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

// class Solution {
//     public String destCity(List<List<String>> paths) {
//         List<String> s1 = new ArrayList<>();
//         List<String> s2 = new ArrayList<>();

//         Iterator iter = paths.iterator();
//         while(iter.hasNext()){
//             List<String> a = (List<String>)iter.next();
//             s2.add(a.get(0));
//             s1.add(a.get(1));
//         }
        
//         for(int i=0,j=0;i<s1.size();i++){
//             String t = s1.get(i);
//             for(j=0;j<s2.size();j++){
//                 if(t.equals(s2.get(j))) break;
//             }
//             if(j==s2.size()){
//                 return t;
//             }
//         }
//         return "";
//     }
// }

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

class Solution {
    public String destCity(List<List<String>> paths) {
        int len = paths.size();
        String s1[] = new String[len];
        String s2[] = new String[len];

        for(int i=0; i<paths.size();i++){
            s2[i] = paths.get(i).get(0);
            s1[i] = paths.get(i).get(1);
        }
  
        for(int i=0,j=0;i<s1.length;i++){
            String t = s1[i];
            for(j=0;j<s2.length;j++){
                if(t.equals(s2[j])) break;
            }
            if(j==s2.length){
                return t;
            }
        }
        return "";
    }
}

class DestCityTest{
    public static void main(String[] args){
        Solution s = new Solution();
        List<List<String>> paths = new ArrayList<>();
        // insert 1
        List<String> a = new ArrayList<>(); 
        a.add("B");
        a.add("C");
        paths.add(a);
        // insert 2
        a = Arrays.asList("D", "B");
        paths.add(a);
        // insert 3
        paths.add(Arrays.asList("C", "A"));
        
        System.out.println(s.destCity(paths));
    }
}