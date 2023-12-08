package core;

import models.Expression;
import models.ExpressionType;

import java.util.HashMap;

public class ExpressionistImpl implements Expressionist {
    private Expression root = null;
    private HashMap<String, Expression> expressions = new HashMap<>();
    private HashMap<String, Expression> parents = new HashMap<>();
    @Override
    public void addExpression(Expression expression) {
        if (this.root != null) {
            throw new IllegalArgumentException();
        }

        this.root = expression;
        expressions.put(expression.getId(), expression);
    }

    @Override
    public void addExpression(Expression expression, String parentId) {
        Expression parent = getExpression(parentId);

        Expression leftChild = parent.getLeftChild();
        Expression rightChild = parent.getRightChild();

        if (leftChild == null) {
            parent.setLeftChild(expression);
        } else if (rightChild == null) {
            parent.setRightChild(expression);
        } else {
            throw new IllegalArgumentException();
        }
        expressions.put(expression.getId(), expression);
        parents.put(expression.getId(), parent);
    }

    @Override
    public boolean contains(Expression expression) {
        return getById(expression.getId()) != null;
    }

    @Override
    public int size() {
        return expressions.size();
    }

    @Override
    public Expression getExpression(String expressionId) {
        Expression byId = getById(expressionId);

        if (byId == null) {
            throw new IllegalArgumentException();
        }
        return byId;
    }

    @Override
    public void removeExpression(String expressionId) {
        Expression toRemove = getExpression(expressionId);
        Expression startNode = root;

        if (toRemove.getId().equals(startNode.getId())) {
            removeIteratively(root);
            this.root = null;
        } else if (expressions.containsKey(expressionId)){
            Expression parentNode = parents.get(expressionId);

            Expression leftChild = parentNode.getLeftChild();
            Expression rightChild = parentNode.getRightChild();

            if (leftChild.getId().equals(expressionId)) {
                parentNode.setLeftChild(rightChild);
                removeIteratively(leftChild);
                parentNode.setRightChild(null);

            } else {
                removeIteratively(rightChild);
            }

        } else {
            throw new IllegalArgumentException();
        }

    }

    private void removeIteratively(Expression node) {
        if (node == null) {
            return;
        }
        expressions.remove(node.getId());
        parents.remove(node.getId());

        removeIteratively(node.getLeftChild());

        removeIteratively(node.getRightChild());
    }

    @Override
    public String evaluate() {
        Expression start = root;

        return getResult(start);
    }

    private String getResult(Expression node) {
        String resultStr = "";

        if (node == null) {
            return "";
        }

        if (node.getType().equals(ExpressionType.OPERATOR)) {
         resultStr = "(" + getResult(node.getLeftChild()) + " " + node.getValue() + " " + getResult(node.getRightChild()) + ")";
        } else if (node.getType().equals(ExpressionType.VALUE)) {
            return node.getValue();
        }
        return resultStr;
    }

    private Expression getById(String expressionId) {
        return expressions.get(expressionId);
    }
}
