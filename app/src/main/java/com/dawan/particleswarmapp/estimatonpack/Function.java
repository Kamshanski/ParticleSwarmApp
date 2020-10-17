package com.dawan.particleswarmapp.estimatonpack;

import com.dawan.particleswarmapp.U;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class Function {
    private Expression expression;
    private static final String X = "x";
    private static final String Y = "y";

    private Function(String formula) {
        this.expression = new ExpressionBuilder(formula)
                .variables(X, Y)
                .build();
    }

    public double of(double x, double y) {
        expression.setVariable(X, x)
                .setVariable(Y, y);
        return expression.evaluate();
    }

    public static Function getInstance(String formula) {
        Function instance;
        try {
            instance = new Function(formula);
        } catch (IllegalArgumentException e) {
            U.d("Wrong formula input");
            return new Function(U.formula);
        }
        return instance;
    }
}
