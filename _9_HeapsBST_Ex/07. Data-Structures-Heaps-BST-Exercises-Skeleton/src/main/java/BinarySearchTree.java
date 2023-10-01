import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.function.Consumer;

import java.util.List;

public class BinarySearchTree<E extends Comparable<E>> {
    private Node<E> root;

    public BinarySearchTree() {}

    public BinarySearchTree(E element) {
        this.root = new Node<>(element);
    }

    public BinarySearchTree(Node<E> otherRoot) {
        this.root = new Node<>(otherRoot);
    }

    public static class Node<E> {
        private E value;
        private Node<E> leftChild;
        private Node<E> rightChild;
        private int count;

        public Node(E value) {
            this.value = value;
            this.count = 1;
        }

        public Node(Node<E> other) {
            this.value = other.value;
            this.count = other.count;

            if (other.getLeft() != null) {
                this.leftChild = new Node<>(other.getLeft());
            }

            if (other.getRight() != null) {
                this.rightChild = new Node<>(other.getRight());
            }
        }

        public Node<E> getLeft() {
            return this.leftChild;
        }

        public Node<E> getRight() {
            return this.rightChild;
        }

        public E getValue() {
            return this.value;
        }

        public int getCount() {
            return this.count;
        }
    }

    public void eachInOrder(Consumer<E> consumer) {
        nodeInOrder(this.root, consumer);
    }

    private void nodeInOrder(Node<E> node, Consumer<E> consumer) {
        if (node == null) {
            return;
        }

        nodeInOrder(node.getLeft(), consumer);
        consumer.accept(node.getValue());
        nodeInOrder(node.getRight(), consumer);
    }

    public Node<E> getRoot() {
        return this.root;
    }

    public void insert(E element) {
        if (this.root == null) {
            this.root = new Node<>(element);
        } else {
            insertHelper(this.root, element);
        }
    }

    private void insertHelper(Node<E> node, E element) {
        if (isGreater(element, node)) {
            if (node.getRight() == null) {
                node.rightChild = new Node<>(element);
            } else {
                insertHelper(node.getRight(), element);
            }
        } else if (isLess(element, node)) {
            if (node.getLeft() == null) {
                node.leftChild = new Node<>(element);
            } else {
                insertHelper(node.getLeft(), element);
            }
        }
        node.count++;
    }

    public boolean contains(E element) {
        Node<E> nodeFound = containsHelper(this.root, element);

        return nodeFound != null;
    }

    private Node<E> containsHelper(Node<E> node, E element) {
        if (node == null) {
            return null;
        }

        if (isEqual(element, node)) {
            return new Node<>(node);
        } else if (isGreater(element, node)) {
            return containsHelper(node.getRight(), element);
        }

        return containsHelper(node.getLeft(), element);
    }

    public BinarySearchTree<E> search(E element) {
        Node<E> eNode = containsHelper(this.root, element);
        if (eNode == null) {
            return null;
        }
        return new BinarySearchTree<>(eNode);
    }

    public List<E> range(E lower, E upper) {
        List<E> result = new ArrayList<>();

        if (this.root == null) {
            return result;
        }

        Deque<Node<E>> queue = new ArrayDeque<>();

        queue.offer(this.root);

        while (!queue.isEmpty()) {
            Node<E> curr = queue.poll();

            if (curr.getLeft() != null) {
                queue.offer(curr.getLeft());
            }

            if (curr.getRight() != null) {
                queue.offer(curr.getRight());
            }

            if (isLess(lower, curr) && isGreater(upper, curr)) {
                result.add(curr.getValue());
            } else if (isEqual(lower, curr) || isEqual(upper, curr)) {
                result.add(curr.getValue());
            }
        }
        return result;
    }

    public void deleteMin() {
        ensureNonEmpty();

        if (this.root.getLeft() == null) {
            this.root = this.root.getRight();
            return;
        }

        Node<E> current = this.root;

        while (current.getLeft().getLeft() != null) {
            current.count--;
            current = current.getLeft();
        }

        current.count--;
        current.leftChild = current.getLeft().getRight();
    }

    public void deleteMax() {
        ensureNonEmpty();

        if (this.root.getRight() == null) {
            this.root = this.root.getLeft();
            return;
        }

        Node<E> current = this.root;

        while (current.getRight().getRight() != null) {
            current.count--;
            current = current.getRight();
        }

        current.count--;
        current.rightChild = current.getRight().getLeft();
    }

    public int count() {
        return this.root == null ? 0 : this.root.count;
    }

    public int rank(E element) {
        return helperRank(this.root, element);
    }

    private int helperRank(Node<E> node, E element) {
        if (node == null) {
            return 0;
        }
        if (isLess(element, node)) {
            return helperRank(node.getLeft(), element);
        } else if (isEqual(element, node)) {
            return getNodeCount(node.getLeft());
        }

        return getNodeCount(node.getLeft()) + 1 + helperRank(node.getRight(), element);
    }

    private int getNodeCount(Node<E> node) {
        return node == null ? 0 : node.getCount();
    }

    public E ceil(E element) {
        if (this.root == null) {
            return null;
        }

        Node<E> curr = this.root;
        Node<E> nearestBigger = null;

        while (curr != null) {
            if (isLess(element, curr)) {
                nearestBigger = curr;
                curr = curr.getLeft();
            } else if (isGreater(element, curr)) {
                curr = curr.getRight();
            } else {
                Node<E> right = curr.getRight();
                if (right != null && nearestBigger != null) {
                    nearestBigger = isLess(right.getValue(), nearestBigger) ? right: nearestBigger;
                } else if (nearestBigger == null) {
                    nearestBigger = right;
                }
                break;
            }
        }

        return nearestBigger == null ? null : nearestBigger.value;
    }

    public E floor(E element) {
        if (this.root == null) {
            return null;
        }

        Node<E> curr = this.root;

        Node<E> nearestSmaller = null;

        while (curr != null) {
            if (isGreater(element, curr)) {
                nearestSmaller = curr;
                curr = curr.getRight();
            } else if (isLess(element, curr)) {
                curr = curr.getLeft();
            } else {
                Node<E> left = curr.getLeft();

                if (curr.getLeft() != null && nearestSmaller != null) {
                    nearestSmaller = isGreater(left.getValue(), nearestSmaller) ? left : nearestSmaller;
                } else if (nearestSmaller == null) {
                    nearestSmaller = left;
                }
                break;
            }
        }

        return nearestSmaller == null ? null : nearestSmaller.value;
    }

    private boolean isGreater(E element, Node<E> node) {
        return element.compareTo(node.getValue()) > 0;
    }

    private boolean isLess(E element, Node<E> node) {
        return element.compareTo(node.getValue()) < 0;
    }

    private boolean isEqual(E element, Node<E> node) {
        return element.compareTo(node.getValue()) == 0;
    }

    private void ensureNonEmpty() {
        if (this.root == null) {
            throw new IllegalArgumentException();
        }
    }
}
