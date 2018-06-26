package px.leetcode.twoSum;

import java.util.HashMap;
import java.util.Map;

/*     Given an array of integers, return indices of the two numbers such that they add up to a specific target.

        You may assume that each input would have exactly one solution, and you may not use the same element twice.

        Example:

        Given nums = [2, 7, 11, 15], target = 9,

        Because nums[0] + nums[1] = 2 + 7 = 9,
        return [0, 1].
*/
public class TwoSum {

    /**
     *
     * Time complexity:O(n2)
     * Space complexity:O(1)*
     * @param array
     * @param target
     * @return
     */
    public static int[] getTwoIndex(final int[] array,final int target){
        int length = array.length;
        if(length<2){
            return null;
        }
        for(int i=0;i<length;i++){
            int x=array[i];
            for(int j=i+1;j<length;j++){
                int y=array[j];
                if(x+y==target){
                    return new int[]{i,j};
                }
            }
        }
        return null;
    }
    /**
     *
     * Time complexity:O(n)
     * Space complexity:O(n)
     * @param array
     * @param target
     * @return
     */
    public static int [] getTwoIndexOfMap(final int[] array,final int target){
        int length = array.length;
        if(length<2){
            return null;
        }
        Map<Integer,Integer> result=new HashMap<Integer, Integer>();
        for(int i=0;i<length;i++){
            int temp=target-array[i];
            if(result.containsKey(temp)){
                int text = result.get(temp).intValue();
                return new int[]{text,i};
            }else{
                result.put(array[i],i);
            }
        }
        return null;
    }



    public static void main(String[] args) {
        int [] array={2,7,11,15};
        int target=22;
        int[] twoIndex = getTwoIndexOfMap(array, target);
        if(twoIndex!=null){
            System.out.println(String.format("[%d,%d]",twoIndex[0],twoIndex[1]));
        }else{
            System.out.println("not found");
        }

    }

}
