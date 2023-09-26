package implementations;

import interfaces.AbstractTree;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class Tree<E> implements AbstractTree<E> {

    private E key;
    private Tree<E> parent;
    private List<Tree<E>> children;

    public Tree(E key) {
        this.key = key;
        this.children = new ArrayList<>();
       /* this.children.addAll(Arrays.asList(children));

        for (int i = 0; i < children.length; i++) {
            children[i].setParent(this);
        }*/
    }


    @Override
    public void setParent(Tree<E> parent) {
        this.parent = parent;
    }

    @Override
    public void addChild(Tree<E> child) {
        this.children.add(child);
    }

    @Override
    public Tree<E> getParent() {
        return this.parent;
    }

    @Override
    public E getKey() {
        return this.key;
    }

    @Override
    public String getAsString() {
        StringBuilder builder = new StringBuilder();

        traverseTreeWithRecurrence(builder, 0, this);
        String result = builder.toString().trim();
        System.out.println();
        return result;
    }

    private void traverseTreeWithRecurrence(StringBuilder builder, int indent, Tree<E> tree) {
        builder.append(this.getPadding(indent))
                .append(tree.getKey())
                .append(System.lineSeparator());


        for (Tree<E> child : tree.children) {
            traverseTreeWithRecurrence(builder, indent + 2, child);
        }

    }

    private String getPadding(int size) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(" ");
        }

        return sb.toString();
    }

    public String traverseWithBFS() {
        StringBuilder builder = new StringBuilder();

        Deque<Tree<E>> queue = new ArrayDeque<>();

        queue.push(this);

        int ident = 0;

        while (!queue.isEmpty()) {
            Tree<E> tree = queue.poll();

            if (tree.getParent() != null && tree.getParent().getKey().equals(this.getKey())) {
                ident = 2;
            } else if (tree.children.size() == 0) {
                ident = 4;
            }

            builder.append(getPadding(ident))
                    .append(tree.getKey())
                    .append(System.lineSeparator());

            for (Tree<E> child : tree.children) {
                queue.offer(child);
            }
        }

        return builder.toString().trim();
    }

    @Override
    public List<E> getLeafKeys() {
        return null;
    }

    @Override
    public List<E> getMiddleKeys() {
        return null;
    }

    @Override
    public Tree<E> getDeepestLeftmostNode() {
        return null;
    }

    @Override
    public List<E> getLongestPath() {
        return null;
    }

    @Override
    public List<List<E>> pathsWithGivenSum(int sum) {
        return null;
    }

    @Override
    public List<Tree<E>> subTreesWithGivenSum(int sum) {
        return null;
    }
}



