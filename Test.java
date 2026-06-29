import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.Queue;
import java.util.ArrayDeque;
import java.util.HashSet;

public class Test {
    public static void main(String[] args) {
    }
}

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode() {
    }

    TreeNode(int val) {
        this.val = val;
    }

    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}

class Solution {
    public int minMutation(String startGene, String endGene, String[] bank) {
        int ret = 0;
        int m = bank.length;
        if(startGene.equals(endGene)){
            return 0;
        }
        if(m == 0){
            return 0;
        }
        String ds = "ACGT";
        Queue<String> q = new LinkedList<>();
        HashSet<String> hash = new HashSet<>();
        HashSet<String> hashQ = new HashSet<>();
        for(String str: bank){
            hash.add(str);
        }
        q.add(startGene);
        hashQ.add(startGene);

        while(!q.isEmpty()){
            ret++;
            int size = q.size();
            while(size-- != 0){
                String s = q.poll();
                for(int i = 0; i < 8; i++){
                    for(int j = 0; j < 4; j++){
                        char[] tmp = s.toCharArray();
                        tmp[i] = ds.charAt(j);
                        String st = new String(tmp);
                        if(hashQ.contains(st)){
                            continue;
                        }
                        hashQ.add(st);
                        if(endGene.equals(st)){
                            return ret;
                        }
                        if(hash.contains(st)){
                            q.add(st);
                        }
                    }
                }
            }
        }
        return -1;
    }
}
