package com.miraclebay.leetcode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Test {
    public static void main(String[] args) {

    }

    public List<Integer> inorderByNotTraversal(TreeNode root){
        if (root==null) return new ArrayList<>();
        Queue<TreeNode> queue = new LinkedList<>();
        TreeNode p = root;
        List<Integer> list = new ArrayList<>();
        queue.offer(p);
        while(!queue.isEmpty()){
            while(p!=null){
                queue.offer(p);
                p = p.left;
            }
            TreeNode q = queue.peek();
            list.add(q.val);
            if (q.right!=null){
                queue.offer(q.right);
                p = q.left;
            }
        }
        return list;
    }
    private List<List<Integer>> res = new LinkedList<>();
    public List<List<Integer>> pathSum(TreeNode root, int targetSum) {

    }

    public void pathSumSubTree(TreeNode root, int targetSum, List<Integer> path, int sum){
        if (sunNodeNum(root) == 0){
            if (root.val + sum == targetSum){
                path.add(root.val);
                res.add(path);
            }
            return;
        }else if (sunNodeNum(root) == 2){
            List<Integer> listLeft = new ArrayList<>(path);
            List<Integer> listRight = new ArrayList<>(path);
            pathSumSubTree(root.left,targetSum,listLeft,sum+ root.val);
            pathSumSubTree(root.right,targetSum,listRight,sum+ root.val);
        }else{
            List<Integer> list = new ArrayList<>(path);
            pathSumSubTree(root.left != null ? root.left:root.right, targetSum, list, sum+ root.val);
        }
    }

    public int sunNodeNum(TreeNode root){
        if (root.left == null && root.right ==null) return 0;
        if (root.left !=null && root.right != null) return 2;
        return 1;
    }


    class TreeNode {
      int val;
      TreeNode left;
      TreeNode right;
      TreeNode() {}
      TreeNode(int val) { this.val = val; }
      TreeNode(int val, TreeNode left, TreeNode right) {
          this.val = val;
          this.left = left;
          this.right = right;
      }
  }
}
