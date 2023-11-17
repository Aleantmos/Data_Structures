package main;

import java.util.*;
import java.util.stream.Collectors;

public class Hierarchy<T> implements IHierarchy<T> {
    private Map<T, HierarchyNode<T>> data;
    private HierarchyNode<T> root;

    public Hierarchy(T element) {
        this.data = new HashMap<>();
        HierarchyNode<T> root = new HierarchyNode<>(element);
        this.root = root;
        this.data.put(element, root);
    }

    @Override
    public int getCount() {
        return this.data.size();
    }

    @Override
    public void add(T element, T child) {
        HierarchyNode<T> parent = checkElementExistence(element);

        if (this.data.containsKey(child)) {
            throw new IllegalArgumentException();
        }

        HierarchyNode<T> addChild = new HierarchyNode<>(child);

        addChild.setParent(parent);
        parent.getChildren().add(addChild);

        this.data.put(child, addChild);
    }

    @Override
    public void remove(T element) {
        HierarchyNode<T> toRemove = checkElementExistence(element);

        if (toRemove.getParent() == null) {
            throw new IllegalStateException();
        }

        HierarchyNode<T> parent = toRemove.getParent();
        List<HierarchyNode<T>> children = toRemove.getChildren();

        for (HierarchyNode<T> child : children) {
            child.setParent(parent);
        }
        parent.getChildren().addAll(children);
        parent.getChildren().remove(toRemove);

        this.data.remove(toRemove.getValue());
    }

    @Override
    public Iterable<T> getChildren(T element) {
        HierarchyNode<T> node = checkElementExistence(element);
        return node.getChildren().stream()
                .map(HierarchyNode::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public T getParent(T element) {
        HierarchyNode<T> node = checkElementExistence(element);
        HierarchyNode<T> parent = node.getParent();

        return  parent == null ? null : parent.getValue();
    }

    @Override
    public boolean contains(T element) {
        return this.data.containsKey(element);
    }

    @Override
    public Iterable<T> getCommonElements(IHierarchy<T> other) {
        List<T> result = new ArrayList<>();

        for (T key : this.data.keySet()) {
            if (other.contains(key)) {
                result.add(key);
            }
        }
        return result;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            Deque<HierarchyNode<T>> deque = new ArrayDeque<>(
                    Collections.singletonList(root)
            );


            @Override
            public boolean hasNext() {
                return deque.size() > 0;
            }

            @Override
            public T next() {
                HierarchyNode<T> nextElement = deque.poll();
                deque.addAll(nextElement.getChildren());

                return nextElement.getValue();
            }
        };
    }

    private HierarchyNode<T> checkElementExistence(T element) {
        HierarchyNode<T> node = getElementByKey(element);

        if (node == null) {
            throw new IllegalArgumentException();
        }
        return node;
    }

    private HierarchyNode<T> getElementByKey(T element) {
        return this.data.get(element);
    }
}
