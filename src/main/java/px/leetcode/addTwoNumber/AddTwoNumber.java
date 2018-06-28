package px.leetcode.addTwoNumber;

/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode(int x) { val = x; }
 * }
 */
public class AddTwoNumber {
    
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        int carry=0;
        ListNode resultNode=new ListNode(0);
        ListNode temp=resultNode;
        while (l1!=null || l2!=null){
            int result=carry;
            if(l1!=null){
                result+=l1.val;
                l1=l1.next;
            }
            if(l2!=null){
                result+=l2.val;
                l2=l2.next;
            }
            carry=result/10;
            temp.next=new ListNode(result%10);
            temp=temp.next;

        }
        if(carry==1){
            temp.next=new ListNode(carry);
        }
        return resultNode.next;
    }

    public static void main(String[] args) {
        ListNode listNode1=new ListNode(1);
        ListNode listNode2=new ListNode(8);
        listNode1.next=listNode2;

        ListNode listNode4=new ListNode(0);
        AddTwoNumber addTwoNumber=new AddTwoNumber();
        ListNode listNode = addTwoNumber.addTwoNumbers(listNode1, listNode4);
        while (listNode!=null){
            System.out.println(listNode.val);
            listNode=listNode.next;
        }
    }
}
class ListNode {
    int val;
    ListNode next;
    ListNode(int x) { val = x; }
}
