package dev.phantom.client.core.macro.script;

import java.util.ArrayList;
import java.util.List;

public abstract class ASTNode {

    // =========================================================================
    // Expression nodes
    // =========================================================================

    public static class NumberLiteral extends ASTNode {
        public final double value;

        public NumberLiteral(double value) {
            this.value = value;
        }
    }

    public static class StringLiteral extends ASTNode {
        public final String value;

        public StringLiteral(String value) {
            this.value = value;
        }
    }

    public static class BooleanLiteral extends ASTNode {
        public final boolean value;

        public BooleanLiteral(boolean value) {
            this.value = value;
        }
    }

    public static class Identifier extends ASTNode {
        public final String name;

        public Identifier(String name) {
            this.name = name;
        }
    }

    public static class BinaryOp extends ASTNode {
        public final ASTNode left;
        public final String op;
        public final ASTNode right;

        public BinaryOp(ASTNode left, String op, ASTNode right) {
            this.left = left;
            this.op = op;
            this.right = right;
        }
    }

    public static class UnaryOp extends ASTNode {
        public final String op;
        public final ASTNode operand;

        public UnaryOp(String op, ASTNode operand) {
            this.op = op;
            this.operand = operand;
        }
    }

    public static class FunctionCall extends ASTNode {
        public final String name;
        public final List<ASTNode> args;

        public FunctionCall(String name, List<ASTNode> args) {
            this.name = name;
            this.args = args;
        }

        public FunctionCall(String name) {
            this.name = name;
            this.args = new ArrayList<>();
        }
    }

    // =========================================================================
    // Statement nodes
    // =========================================================================

    public static class Program extends ASTNode {
        public final List<ASTNode> statements;

        public Program(List<ASTNode> statements) {
            this.statements = statements;
        }

        public Program() {
            this.statements = new ArrayList<>();
        }
    }

    public static class Assignment extends ASTNode {
        public final String name;
        public final ASTNode value;

        public Assignment(String name, ASTNode value) {
            this.name = name;
            this.value = value;
        }
    }

    public static class IfStatement extends ASTNode {
        public final ASTNode condition;
        public final ASTNode thenBlock;
        public final ASTNode elseBlock; // may be null

        public IfStatement(ASTNode condition, ASTNode thenBlock, ASTNode elseBlock) {
            this.condition = condition;
            this.thenBlock = thenBlock;
            this.elseBlock = elseBlock;
        }
    }

    public static class LoopStatement extends ASTNode {
        public final ASTNode count;
        public final ASTNode body;

        public LoopStatement(ASTNode count, ASTNode body) {
            this.count = count;
            this.body = body;
        }
    }

    public static class WhileStatement extends ASTNode {
        public final ASTNode condition;
        public final ASTNode body;

        public WhileStatement(ASTNode condition, ASTNode body) {
            this.condition = condition;
            this.body = body;
        }
    }

    public static class Block extends ASTNode {
        public final List<ASTNode> statements;

        public Block(List<ASTNode> statements) {
            this.statements = statements;
        }

        public Block() {
            this.statements = new ArrayList<>();
        }
    }

    public static class BreakStatement extends ASTNode {
        public BreakStatement() {}
    }

    public static class ContinueStatement extends ASTNode {
        public ContinueStatement() {}
    }

    public static class ExpressionStatement extends ASTNode {
        public final ASTNode expression;

        public ExpressionStatement(ASTNode expression) {
            this.expression = expression;
        }
    }
}
