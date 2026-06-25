import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.Queue;
import java.util.ArrayDeque;

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
    public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
        if (root == null) {
            return new LinkedList<>();
        }
        int flag = 0; // 0 left, 1 right
        Queue<TreeNode> q = new LinkedList<>();
        q.add(root);
        List<List<Integer>> ret = new ArrayList<>();
        while (!q.isEmpty()) {
            int cnt = q.size();
            List<Integer> tmp = new ArrayList<>();
            while (cnt-- != 0) {
                TreeNode node = q.peek();
                q.poll();
                tmp.add(node.val);
                q.add(node.left);
                q.add(node.right);
            }
            if (flag == 0) {
                flag = 1;
                ret.add(tmp);
            } else {
                flag = 0;
                Collections.reverse(tmp);
                ret.add(tmp);
            }
        }
        return ret;
    }

    public int widthOfBinaryTree(TreeNode root) {
        if (root == null) {
            return 0;
        }
        int ret = 0;
        Queue<TreeNode> q = new LinkedList<>();
        q.add(root);
        while (!q.isEmpty()) {
            int cnt = q.size();
            ret = Math.max(cnt, ret);
            List<Integer> list = new ArrayList<>();
            while (cnt-- != 0) {
                TreeNode node = q.peek();
                q.poll();
                list.add(node.val);
                if (node.left != null) {
                    q.add(node.left);
                }
                if (node.right != null) {
                    q.add(node.right);
                }
            }
        }
        return ret;
    }
}
