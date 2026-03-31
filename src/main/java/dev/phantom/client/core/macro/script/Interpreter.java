package dev.phantom.client.core.macro.script;

import dev.phantom.client.core.macro.script.builtins.ActionBuiltins;
import dev.phantom.client.core.macro.script.builtins.ChatBuiltins;
import dev.phantom.client.core.macro.script.builtins.InputBuiltins;
import dev.phantom.client.core.macro.script.builtins.IntegrationBuiltins;
import dev.phantom.client.core.macro.script.builtins.InventoryBuiltins;
import dev.phantom.client.core.macro.script.builtins.LookBuiltins;
import dev.phantom.client.core.macro.script.builtins.ModuleBuiltins;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interpreter {

    // -------------------------------------------------------------------------
    // Builtin function interface
    // -------------------------------------------------------------------------

    @FunctionalInterface
    public interface BuiltinFunction {
        Object call(List<Object> args) throws Exception;
    }

    // -------------------------------------------------------------------------
    // ScriptRuntimeException
    // -------------------------------------------------------------------------

    public static class ScriptRuntimeException extends RuntimeException {
        public ScriptRuntimeException(String message) {
            super(message);
        }

        public ScriptRuntimeException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // -------------------------------------------------------------------------
    // State
    // -------------------------------------------------------------------------

    private final ScriptContext context;
    private final Map<String, BuiltinFunction> builtins = new HashMap<>();

    public Interpreter(ScriptContext context) {
        this.context = context;
        registerAllBuiltins();
    }

    private void registerAllBuiltins() {
        ActionBuiltins.registerAll(this);
        InventoryBuiltins.registerAll(this);
        LookBuiltins.registerAll(this);
        ModuleBuiltins.registerAll(this);
        ChatBuiltins.registerAll(this);
        InputBuiltins.registerAll(this);
        IntegrationBuiltins.registerAll(this);
    }

    /** Register a named builtin function. */
    public void registerBuiltin(String name, BuiltinFunction fn) {
        builtins.put(name, fn);
    }

    // -------------------------------------------------------------------------
    // Execution entry points
    // -------------------------------------------------------------------------

    public void execute(ASTNode.Program program) {
        for (ASTNode stmt : program.statements) {
            executeStatement(stmt);
            if (context.isShouldBreak() || context.isShouldContinue()) {
                break;
            }
            checkInterrupted();
        }
    }

    public void executeStatement(ASTNode node) {
        checkInterrupted();

        if (node instanceof ASTNode.ExpressionStatement es) {
            eval(es.expression);

        } else if (node instanceof ASTNode.Assignment assign) {
            Object value = eval(assign.value);
            context.setVar(assign.name, value);

        } else if (node instanceof ASTNode.IfStatement ifStmt) {
            Object condition = eval(ifStmt.condition);
            if (isTruthy(condition)) {
                executeBlock(ifStmt.thenBlock);
            } else if (ifStmt.elseBlock != null) {
                executeBlock(ifStmt.elseBlock);
            }

        } else if (node instanceof ASTNode.LoopStatement loop) {
            Object countObj = eval(loop.count);
            int count = toInt(countObj);
            for (int i = 0; i < count; i++) {
                checkInterrupted();
                context.setShouldContinue(false);
                executeBlock(loop.body);
                if (context.isShouldBreak()) {
                    context.setShouldBreak(false);
                    break;
                }
                context.setShouldContinue(false);
            }

        } else if (node instanceof ASTNode.WhileStatement whileStmt) {
            while (isTruthy(eval(whileStmt.condition))) {
                checkInterrupted();
                context.setShouldContinue(false);
                executeBlock(whileStmt.body);
                if (context.isShouldBreak()) {
                    context.setShouldBreak(false);
                    break;
                }
                context.setShouldContinue(false);
            }

        } else if (node instanceof ASTNode.Block block) {
            executeBlock(block);

        } else if (node instanceof ASTNode.BreakStatement) {
            context.setShouldBreak(true);

        } else if (node instanceof ASTNode.ContinueStatement) {
            context.setShouldContinue(true);

        } else {
            // Treat as expression
            eval(node);
        }
    }

    private void executeBlock(ASTNode blockNode) {
        List<ASTNode> stmts;
        if (blockNode instanceof ASTNode.Block block) {
            stmts = block.statements;
        } else {
            // Single statement wrapped directly
            executeStatement(blockNode);
            return;
        }

        for (ASTNode stmt : stmts) {
            executeStatement(stmt);
            if (context.isShouldBreak() || context.isShouldContinue()) {
                break;
            }
            checkInterrupted();
        }
    }

    // -------------------------------------------------------------------------
    // Evaluation
    // -------------------------------------------------------------------------

    public Object eval(ASTNode node) {
        checkInterrupted();

        if (node instanceof ASTNode.NumberLiteral n) {
            return n.value;
        }

        if (node instanceof ASTNode.StringLiteral s) {
            return s.value;
        }

        if (node instanceof ASTNode.BooleanLiteral b) {
            return b.value;
        }

        if (node instanceof ASTNode.Identifier id) {
            return context.getVar(id.name);
        }

        if (node instanceof ASTNode.BinaryOp bin) {
            return evalBinaryOp(bin);
        }

        if (node instanceof ASTNode.UnaryOp un) {
            return evalUnaryOp(un);
        }

        if (node instanceof ASTNode.FunctionCall call) {
            return evalFunctionCall(call);
        }

        if (node instanceof ASTNode.Assignment assign) {
            Object value = eval(assign.value);
            context.setVar(assign.name, value);
            return value;
        }

        if (node instanceof ASTNode.ExpressionStatement es) {
            return eval(es.expression);
        }

        // Statements that don't produce values
        executeStatement(node);
        return null;
    }

    // -------------------------------------------------------------------------
    // Binary operations
    // -------------------------------------------------------------------------

    private Object evalBinaryOp(ASTNode.BinaryOp bin) {
        // Short-circuit logical operators
        if (bin.op.equals("&&")) {
            Object left = eval(bin.left);
            if (!isTruthy(left)) return false;
            return isTruthy(eval(bin.right));
        }
        if (bin.op.equals("||")) {
            Object left = eval(bin.left);
            if (isTruthy(left)) return true;
            return isTruthy(eval(bin.right));
        }

        Object left = eval(bin.left);
        Object right = eval(bin.right);

        return switch (bin.op) {
            case "+" -> {
                if (left instanceof String || right instanceof String) {
                    yield stringify(left) + stringify(right);
                }
                yield toDouble(left) + toDouble(right);
            }
            case "-" -> toDouble(left) - toDouble(right);
            case "*" -> toDouble(left) * toDouble(right);
            case "/" -> {
                double divisor = toDouble(right);
                if (divisor == 0.0) throw new ScriptRuntimeException("Division by zero");
                yield toDouble(left) / divisor;
            }
            case "%" -> {
                double divisor = toDouble(right);
                if (divisor == 0.0) throw new ScriptRuntimeException("Modulo by zero");
                yield toDouble(left) % divisor;
            }
            case "==" -> objectsEqual(left, right);
            case "!=" -> !((Boolean) objectsEqual(left, right));
            case "<" -> toDouble(left) < toDouble(right);
            case ">" -> toDouble(left) > toDouble(right);
            case "<=" -> toDouble(left) <= toDouble(right);
            case ">=" -> toDouble(left) >= toDouble(right);
            default -> throw new ScriptRuntimeException("Unknown binary operator: " + bin.op);
        };
    }

    private Boolean objectsEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        if (a instanceof Double da && b instanceof Double db) return da.equals(db);
        return a.equals(b);
    }

    // -------------------------------------------------------------------------
    // Unary operations
    // -------------------------------------------------------------------------

    private Object evalUnaryOp(ASTNode.UnaryOp un) {
        Object operand = eval(un.operand);
        return switch (un.op) {
            case "!" -> !isTruthy(operand);
            case "-" -> -toDouble(operand);
            default -> throw new ScriptRuntimeException("Unknown unary operator: " + un.op);
        };
    }

    // -------------------------------------------------------------------------
    // Function calls
    // -------------------------------------------------------------------------

    private Object evalFunctionCall(ASTNode.FunctionCall call) {
        BuiltinFunction fn = builtins.get(call.name);
        if (fn == null) {
            throw new ScriptRuntimeException("Unknown function: " + call.name);
        }

        List<Object> evaledArgs = new java.util.ArrayList<>();
        for (ASTNode arg : call.args) {
            evaledArgs.add(eval(arg));
        }

        try {
            return fn.call(evaledArgs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ScriptRuntimeException("Macro interrupted in '" + call.name + "'");
        } catch (ScriptRuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new ScriptRuntimeException("Error in builtin '" + call.name + "': " + e.getMessage(), e);
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /** Truthy: non-null, non-false, non-zero. */
    public static boolean isTruthy(Object value) {
        if (value == null) return false;
        if (value instanceof Boolean b) return b;
        if (value instanceof Double d) return d != 0.0;
        if (value instanceof String s) return !s.isEmpty();
        return true;
    }

    public static double toDouble(Object value) {
        if (value instanceof Double d) return d;
        if (value instanceof Boolean b) return b ? 1.0 : 0.0;
        if (value instanceof String s) {
            try { return Double.parseDouble(s); }
            catch (NumberFormatException e) { return 0.0; }
        }
        if (value == null) return 0.0;
        return 0.0;
    }

    public static int toInt(Object value) {
        return (int) toDouble(value);
    }

    public static String stringify(Object value) {
        if (value == null) return "null";
        if (value instanceof Double d) {
            // Print integers without trailing .0
            if (d == Math.floor(d) && !Double.isInfinite(d)) {
                return String.valueOf(d.longValue());
            }
            return d.toString();
        }
        return value.toString();
    }

    private static void checkInterrupted() {
        if (Thread.currentThread().isInterrupted()) {
            throw new ScriptRuntimeException("Macro interrupted");
        }
    }
}
