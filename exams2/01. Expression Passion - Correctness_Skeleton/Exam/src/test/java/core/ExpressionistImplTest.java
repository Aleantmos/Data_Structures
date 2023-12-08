package core;

import models.Expression;
import models.ExpressionType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ExpressionistImplTest {
    ExpressionistImpl expressionist = new ExpressionistImpl();

    @Before
    public void init() {

        Expression A = new Expression();
        A.setId("a");
        A.setType(ExpressionType.OPERATOR);
        A.setValue("+");
        expressionist.addExpression(A);

        Expression B = new Expression();
        B.setId("b");
        B.setType(ExpressionType.VALUE);
        B.setValue("5");
        expressionist.addExpression(B, A.getId());

        Expression C = new Expression();
        C.setId("c");
        C.setType(ExpressionType.VALUE);
        C.setValue("10");
        expressionist.addExpression(C, A.getId());

        Expression D = new Expression();
        D.setId("d");
        D.setType(ExpressionType.VALUE);
        D.setValue("15");
        expressionist.addExpression(D, B.getId());

        Expression H = new Expression();
        H.setId("h");
        H.setType(ExpressionType.VALUE);
        H.setValue("2");
        expressionist.addExpression(H, D.getId());

        Expression I = new Expression();
        I.setId("i");
        I.setType(ExpressionType.VALUE);
        I.setValue("3");
        expressionist.addExpression(I, D.getId());



        Expression E = new Expression();
        E.setId("e");
        expressionist.addExpression(E, B.getId());


        Expression F = new Expression();
        F.setId("f");
        expressionist.addExpression(F, C.getId());
        Expression G = new Expression();
        G.setId("g");
        expressionist.addExpression(G, C.getId());
    }


    @Test(expected = IllegalArgumentException.class)
    public void test_AddRootExpressionThrows() {
        Expression H = new Expression();
        expressionist.addExpression(H);
    }

    @Test
    public void test_AddExpression() {
        Expression H = new Expression();
        expressionist.addExpression(H, "f");
        assertTrue(expressionist.contains(H));
    }
    @Test(expected = IllegalArgumentException.class)
    public void test_AddExpressionThrowBecauseFull() {
        Expression X = new Expression();
        X.setId("x");
        expressionist.addExpression(X, "b");
    }

    @Test
    public void test_AddExpressionNoRoot() {
        ExpressionistImpl expressionistTest = new ExpressionistImpl();
        Expression X = new Expression();
        X.setId("x");

        expressionistTest.addExpression(X, "x");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_AddExpressionThrowBecauseNoParent() {
        Expression X = new Expression();
        X.setId("x");
        expressionist.addExpression(X, "z");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_AddExpressionThrowBecauseRootExists() {
        Expression X = new Expression();
        X.setId("x");
        expressionist.addExpression(X);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_AddExpressionThrows() {
        Expression H = new Expression();
        expressionist.addExpression(H, "m");
    }

    @Test
    public void test_Contains() {
        Expression A = expressionist.getExpression("h");
        assertTrue(expressionist.contains(A));
    }

    @Test
    public void test_Size() {
        int size = expressionist.size();
        assertEquals(7, size);
    }

    @Test
    public void sizeAfterRemove() {
        int sizeBefore = expressionist.size();

        expressionist.removeExpression("d");

        int size = expressionist.size();

        assertEquals(sizeBefore - 1, size);
    }

    @Test
    public void getExpression() {
        Expression expression = new Expression();
        expression.setId("l");

        expressionist.addExpression(expression, "f");

        Expression l = expressionist.getExpression("l");
        assertEquals(expression, l);

    }

    @Test(expected = IllegalArgumentException.class)
    public void test_GetExpressionThrow() {
        Expression l = expressionist.getExpression("y");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_RemoveExpressionThrow() {
        expressionist.removeExpression("b");
        expressionist.getExpression("b");
    }

    @Test
    public void test_RemoveExpressionRoot() {
        expressionist.removeExpression("a");

        assertEquals(0, expressionist.size());
    }
    @Test
    public void test_RemoveExpression() {
        expressionist.removeExpression("i");
        Expression parent = expressionist.getExpression("d");
        assertEquals(parent.getLeftChild(), expressionist.getExpression("i"));
    }

    @Test
    public void evaluate() {
        expressionist.removeExpression("b");
        String evaluate = expressionist.evaluate();
        System.out.println(evaluate);
        assertEquals("(10 + )", evaluate);
    }
}