/*
Austin Cundiff


 */
package binarysearchtree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class BSTree<Key extends Comparable<Key>, Value> {
    private Node root;             // root of BST

    private class Node {
        private Key key;           // sorted by key
        private Value val;         // associated data
        private Node left, right;  // left and right subtrees
        private int N;             // number of nodes in subtree

        public Node(Key key, Value val, int N) {
            this.key = key;
            this.val = val;
            this.N = N;
        }
    }

    
    public BSTree() {
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    
    public int size() {
        return size(root);
    }

    // return number of key-value pairs in BST rooted at node
    private int size(Node node) {
        if (node == null) return 0;
        else return node.N;
    }

   
    public boolean contains(Key key) {
        return get(key) != null;
    }

    
    public Value get(Key key) {
        return get(root, key);
    }

    private Value get(Node node, Key key) {
        if (node == null) 
            return null;
        int cmp = key.compareTo(node.key);
        if      (cmp < 0) 
            return get(node.left, key);
        else if (cmp > 0) 
            return get(node.right, key);
        else              
            return node.val;
    }

   
    public void put(Key key, Value val) {
        if (val == null) {
            delete(key);
            return;
        }
        root = put(root, key, val);
        assert checkTree();
    }

    private Node put(Node node, Key key, Value val) {
        if (node == null) return new Node(key, val, 1);
        int cmp = key.compareTo(node.key);
        if      (cmp < 0) node.left  = put(node.left,  key, val);
        else if (cmp > 0) node.right = put(node.right, key, val);
        else              node.val   = val;
        node.N = 1 + size(node.left) + size(node.right);
        return node;
    }


    
    public void deleteMin() {
        if (isEmpty()) throw new NoSuchElementException("No such element");
        root = deleteMin(root);
        assert checkTree();
    }

    private Node deleteMin(Node node) {
        if (node.left == null) return node.right;
        node.left = deleteMin(node.left);
        node.N = size(node.left) + size(node.right) + 1;
        return node;
    }

    
    public void deleteMax() {
        if (isEmpty()) throw new NoSuchElementException("Symbol table underflow");
        root = deleteMax(root);
        assert checkTree();
    }

    private Node deleteMax(Node node) {
        if (node.right == null) return node.left;
        node.right = deleteMax(node.right);
        node.N = size(node.left) + size(node.right) + 1;
        return node;
    }

    
    public void delete(Key key) {
        root = delete(root, key);
        assert checkTree();
    }

    private Node delete(Node node, Key key) {
        if (node == null) return null;
        int cmp = key.compareTo(node.key);
        
        if      (cmp < 0) 
            node.left  = delete(node.left,  key);
        else if (cmp > 0) 
            node.right = delete(node.right, key);
        else { 
            if (node.right == null) 
                return node.left;
            if (node.left  == null) 
                return node.right;
            Node t = node;
            node = min(t.right);
            node.right = deleteMin(t.right);
            node.left = t.left;
        } 
        node.N = size(node.left) + size(node.right) + 1;
        return node;
    } 


    
    public Key min() {
        if (isEmpty()) 
            throw new NoSuchElementException("called min() with empty symbol table");
        return min(root).key;
    } 

    private Node min(Node node) { 
        if (node.left == null) return node; 
        else                return min(node.left); 
    } 

    //returns largest key
    public Key max() {
        if (isEmpty()) throw new NoSuchElementException("called max() with empty symbol table");
        return max(root).key;
    } 

    private Node max(Node node) {
        if (node.right == null) return node; 
        else                 return max(node.right); 
    } 

    //returns largest key <= key
    public Key floor(Key key) {
        if (isEmpty()) throw new NoSuchElementException("called floor() with empty symbol table");
        Node node = floor(root, key);
        if (node == null) return null;
        else return node.key;
    } 
    
    private Node floor(Node node, Key key) {
        if (node == null) return null;
        int cmp = key.compareTo(node.key);
        if (cmp == 0) return node;
        if (cmp <  0) return floor(node.left, key);
        Node t = floor(node.right, key); 
        if (t != null) return t;
        else return node; 
    } 

   //returns smallest key > = parameter key
    public Key ceiling(Key key) {
        if (isEmpty()) throw new NoSuchElementException("called ceiling() with empty symbol table");
        Node node = ceiling(root, key);
        if (node == null) return null;
        else return node.key;
    }

    private Node ceiling(Node node, Key key) {
        if (node == null) return null;
        int cmp = key.compareTo(node.key);
        if (cmp == 0) return node;
        if (cmp < 0) { 
            Node t = ceiling(node.left, key); 
            if (t != null) return t;
            else return node; 
        } 
        return ceiling(node.right, key); 
    } 

    
    public Key select(int k) {
        if (k < 0 || k >= size()) throw new IllegalArgumentException();
        Node node = select(root, k);
        return node.key;
    }

    // Return key of rank k. 
    private Node select(Node node, int k) {
        if (node == null) return null; 
        int t = size(node.left); 
        if      (t > k) return select(node.left,  k); 
        else if (t < k) return select(node.right, k-t-1); 
        else            return node; 
    } 

   
    public int rank(Key key) {
        return rank(key, root);
    } 

    // Number of keys in the subtree less than key.
    private int rank(Key key, Node node) {
        if (node == null) return 0; 
        int cmp = key.compareTo(node.key); 
        if      (cmp < 0) return rank(key, node.left); 
        else if (cmp > 0) return 1 + size(node.left) + rank(key, node.right); 
        else              return size(node.left); 
    } 

   
    public Iterable<Key> keys() {
        return keys(min(), max());
    }

    //return list of keys between lo and hi
    public Iterable<Key> keys(Key lo, Key hi) {
        ArrayList<Key> queue = new ArrayList<>();
        keys(root, queue, lo, hi);
        return queue;
    } 
    
    private void keys(Node node, List<Key> queue, Key lo, Key hi) { 
        if (node == null) return; 
        int tempLo = lo.compareTo(node.key); 
        int tempHigh = hi.compareTo(node.key); 
        if (tempLo < 0) keys(node.left, queue, lo, hi); 
        if (tempLo <= 0 && tempHigh >= 0) queue.add(node.key); 
        if (tempHigh > 0) keys(node.right, queue, lo, hi); 
    } 

    //returns size of tree
    public int size(Key lo, Key hi) {
        if (lo.compareTo(hi) > 0) return 0;
        if (contains(hi)) return rank(hi) - rank(lo) + 1;
        else              return rank(hi) - rank(lo);
    }

   //returns height of the tree
    public int getHeight() {
        return getHeight(root);
    }
    private int getHeight(Node node) {
        if (node == null) return -1;
        return 1 + Math.max(getHeight(node.left), getHeight(node.right));
    }

    //returns keys in level order
    public Iterable<Key> levelOrder() {
        ArrayList<Key> keys = new ArrayList<Key>();
        ArrayList<Node> queue = new ArrayList<Node>();
        queue.add(root);
        while (!queue.isEmpty()) {
            Node node = queue.remove(0);
            if (node == null) continue;
            keys.add(node.key);
            queue.add(node.left);
            queue.add(node.right);
        }
        return keys;
    }

  //does the tree exist? lets check
    private boolean checkTree() {
        if (!treeExists())            System.out.println("Not in symmetric order");
        if (!isSizeConsistent()) System.out.println("Subtree counts not consistent");
        if (!isRankConsistent()) System.out.println("Ranks not consistent");
        return treeExists() && isSizeConsistent() && isRankConsistent();
    }

    
    private boolean treeExists() {
        return treeExists(root, null, null);
    }

    
    private boolean treeExists(Node node, Key min, Key max) {
        if (node == null) return true;
        
        if (min != null && node.key.compareTo(min) <= 0) return false;
        
        
        if (max != null && node.key.compareTo(max) >= 0) return false;
        
        return treeExists(node.left, min, node.key) && treeExists(node.right, node.key, max);
    } 

    
    private boolean isSizeConsistent() 
    { 
        return isSizeConsistent(root); 
    }
    
    
    private boolean isSizeConsistent(Node node) {
        if (node == null) return true;
       
        if (node.N != size(node.left) + size(node.right) + 1) return false;
        
        return isSizeConsistent(node.left) && isSizeConsistent(node.right);
    } 

    
    private boolean isRankConsistent() {
        for (int k = 0; k < size(); k++)
            if (k != rank(select(k))) return false;
        for (Key key : keys())
            if (key.compareTo(select(rank(key))) != 0) return false;
        return true;
    }

       public static char[] array;
    
    public static void main(String[] args) 
    {  
        try {
            characterHandler();
        } catch (IOException ex) {
            Logger.getLogger(BSTree.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    public static void characterHandler() throws IOException {
    
    String filename="test.txt";
        // replace this with a known encoding if possible
        Charset encoding = Charset.defaultCharset();
       
            File file = new File(filename);
            handleFile(file, encoding);
        
    
    }
    public static void handleFile(File file, Charset encoding)
            throws IOException {
        try (InputStream in = new FileInputStream(file);
             Reader reader = new InputStreamReader(in, encoding);
             
             Reader buffer = new BufferedReader(reader)) {
            handleCharacters(buffer);
        }
    }

    private static void handleCharacters(Reader reader)
            throws IOException {
        BSTree<String, Integer> st = new BSTree<String, Integer>();
         
        int r, count = 0;
        String temp;
        while ((r = reader.read()) != -1) {
            char ch = (char) r;
            temp = ""+ch;
            if(!temp.equals(" ")) {
            st.put(temp, count);
            count++;
            }
        }
        
       
        
        for (String s : st.levelOrder())
            System.out.println(s + " " + st.get(s));

        System.out.println();

        for (String s : st.keys())
            System.out.println(s + " " + st.get(s));
        
    }
}
