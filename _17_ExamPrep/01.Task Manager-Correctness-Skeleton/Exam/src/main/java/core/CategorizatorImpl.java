package core;

import models.Category;

import java.util.*;
import java.util.stream.Collectors;

public class CategorizatorImpl implements Categorizator {

    LinkedHashMap<String, Category> categoriesById = new LinkedHashMap<>();
    Map<String, Category> parentByCategoryId = new HashMap<>();
    Map<String, LinkedHashSet<Category>> childrenByCategoryId = new HashMap<>();

    @Override
    public void addCategory(Category category) {
        if (contains(category)) {
            throw new IllegalArgumentException();
        }
        categoriesById.put(category.getId(), category);
        childrenByCategoryId.put(category.getId(), new LinkedHashSet<>());

    }

    @Override
    public void assignParent(String childCategoryId, String parentCategoryId) {
        Category child = getById(childCategoryId);
        Category parent = getById(parentCategoryId);

        if (child == null || parent == null) {
            throw new IllegalArgumentException();
        }

        Category previousParent = parentByCategoryId.put(child.getId(), parent);
        if (previousParent == parent) {
            throw new IllegalArgumentException();
        }
        childrenByCategoryId.get(parent.getId()).add(child);
    }

    @Override
    public void removeCategory(String categoryId) {
        Category toDelete = categoriesById.remove(categoryId);
        if (toDelete == null) {
            throw new IllegalArgumentException();
        }

        LinkedHashSet<Category> childrenToDelete = new LinkedHashSet<>(childrenByCategoryId.get(toDelete.getId()));
        for (Category category : childrenToDelete) {
            removeCategory(category.getId());
        }

        Category parent = parentByCategoryId.get(toDelete.getId());
        if (parent != null) {
            LinkedHashSet<Category> parentCategoryChildren = childrenByCategoryId.get(parent.getId());
            parentCategoryChildren.remove(toDelete);
        }
    }


    @Override
    public boolean contains(Category category) {
        return contains(category.getId());
    }


    @Override
    public int size() {
        return categoriesById.size();
    }

    private void fillChildren(String categoryId, List<Category> allChildren) {
        LinkedHashSet<Category> directChildren = childrenByCategoryId.get(categoryId);
        for (Category directChild : directChildren) {
            allChildren.add(directChild);
            fillChildren(directChild.getId(), allChildren);
        }
    }

    @Override
    public Iterable<Category> getChildren(String categoryId) {
        if (!contains(categoryId)) {
            throw new IllegalArgumentException();
        }

        List<Category> allChildren = new ArrayList<>();

        fillChildren(categoryId, allChildren);
        return allChildren;
    }

    @Override
    public Iterable<Category> getHierarchy(String categoryId) {
        List<Category> hierarchy = new ArrayList<>();

        Category category = getById(categoryId);
        if (category == null) {
            throw new IllegalArgumentException();
        }

        while (category != null) {
            hierarchy.add(category);
            category = parentByCategoryId.get(category.getId());
        }

        Collections.reverse(hierarchy);
        return hierarchy;
    }

    Map<String, Long> depthByCategoryId = new HashMap<>();

    @Override
    public Iterable<Category> getTop3CategoriesOrderedByDepthOfChildrenThenByName() {
        for (Category category : categoriesById.values()) {
            if (parentByCategoryId.get(category.getId()) == null) {
                calculateDepth(category);
            }
        }
        return categoriesById.values().stream()
                .sorted(
                        Comparator.comparing((Category c) -> depthByCategoryId.get(c.getId()), Comparator.reverseOrder())
                                .thenComparing((Category c) -> c.getName())
                )
                .limit(3)
                .collect(Collectors.toList());

    }

    private long calculateDepth(Category category) {
        long maxChildDepth = 0;
        for (Category childCategory : childrenByCategoryId.get(category.getId())) {
            long childDepth = calculateDepth(childCategory);
            if (maxChildDepth < childDepth) {
                maxChildDepth = childDepth;
            }
        }

        long depth = 1 + maxChildDepth;
        depthByCategoryId.put(category.getId(), depth);

        return depth;
    }

    private Category getById(String id) {
        return categoriesById.get(id);
    }

    private boolean contains(String categoryId) {
        return getById(categoryId) != null;
    }
}
