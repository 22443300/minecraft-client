package dev.phantom.client.core.macro.script;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    // -------------------------------------------------------------------------
    // ParseException
    // -------------------------------------------------------------------------

    public static class ParseException extends RuntimeException {
        private final int line;
        private final int column;

        public ParseException(String message, int line, int column) {
            super("Parse error at line " + line + ", col " + column + ": " + message);
            this.line = line;
            this.column = column;
        }

        public int getLine() { return line; }
        public int getColumn() { return column; }
    }

    // -------------------------------------------------------------------------
    // State
    // -------------------------------------------------------------------------

    private final List<Token> tokens;
    private int pos = 0;

    public Parser(List<Token> tokens) {
        // Filter out NEWLINE and COMMENT tokens — they are not used by the grammar
        this.tokens = new ArrayList<>();
        for (Token t : tokens) {
            if (t.getType() != Token.TokenType.NEWLINE && t.getType() != Token.TokenType.COMMENT) {
                this.tokens.add(t);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Navigation
    // -------------------------------------------------------------------------

    private Token current() {
        if (pos >= tokens.size()) {
            return new Token(Token.TokenType.EOF, "", 0, 0);
        }
        return tokens.get(pos);
    }

    private Token peek(int ahead) {
        int idx = pos + ahead;
        if (idx >= tokens.size()) {
            return new Token(Token.TokenType.EOF, "", 0, 0);
        }
        return tokens.get(idx);
    }

    private Token consume() {
        Token t = current();
        pos++;
        return t;
    }

    private Token expect(Token.TokenType type) {
        Token t = current();
        if (t.getType() != type) {
            throw new ParseException(
                    "Expected " + type + " but got " + t.getType() + " ('" + t.getValue() + "')",
                    t.getLine(), t.getColumn()
            );
        }
        return consume();
    }

    private boolean check(Token.TokenType type) {
        return current().getType() == type;
    }

    private boolean checkValue(Token.TokenType type, String value) {
        return current().getType() == type && current().getValue().equals(value);
    }

    private boolean match(Token.TokenType type) {
        if (check(type)) {
            consume();
            return true;
        }
        return false;
    }

    /** Optionally consume a semicolon if present. */
    private void optionalSemicolon() {
        if (check(Token.TokenType.SEMICOLON)) {
            consume();
        }
    }

    // -------------------------------------------------------------------------
    // Program
    // -------------------------------------------------------------------------

    public ASTNode.Program parseProgram() {
        List<ASTNode> statements = new ArrayList<>();
        while (!check(Token.TokenType.EOF)) {
            ASTNode stmt = parseStatement();
            if (stmt != null) {
                statements.add(stmt);
            }
        }
        return new ASTNode.Program(statements);
    }

    // -------------------------------------------------------------------------
    // Statements
    // -------------------------------------------------------------------------

    private ASTNode parseStatement() {
        Token cur = current();

        // Skip stray semicolons
        if (check(Token.TokenType.SEMICOLON)) {
            consume();
            return null;
        }

        if (cur.getType() == Token.TokenType.KEYWORD) {
            return switch (cur.getValue()) {
                case "if" -> parseIf();
                case "loop" -> parseLoop();
                case "while" -> parseWhile();
                case "var" -> parseVarDeclaration();
                case "break" -> {
                    consume();
                    optionalSemicolon();
                    yield new ASTNode.BreakStatement();
                }
                case "continue" -> {
                    consume();
                    optionalSemicolon();
                    yield new ASTNode.ContinueStatement();
                }
                default -> {
                    Token t = current();
                    throw new ParseException("Unexpected keyword: " + cur.getValue(), t.getLine(), t.getColumn());
                }
            };
        }

        // IDENTIFIER = expr  (assignment without var)
        if (cur.getType() == Token.TokenType.IDENTIFIER && peek(1).getType() == Token.TokenType.ASSIGN) {
            return parseAssignment();
        }

        // Function call: IDENTIFIER/BUILTIN_FUNC followed by '('
        if ((cur.getType() == Token.TokenType.IDENTIFIER || cur.getType() == Token.TokenType.BUILTIN_FUNC)
                && peek(1).getType() == Token.TokenType.LPAREN) {
            ASTNode call = parseFunctionCallExpression();
            optionalSemicolon();
            return new ASTNode.ExpressionStatement(call);
        }

        // Fall through: parse as expression statement
        ASTNode expr = parseExpression();
        optionalSemicolon();
        return new ASTNode.ExpressionStatement(expr);
    }

    // -------------------------------------------------------------------------
    // Block
    // -------------------------------------------------------------------------

    private ASTNode.Block parseBlock() {
        expect(Token.TokenType.LBRACE);
        List<ASTNode> stmts = new ArrayList<>();
        while (!check(Token.TokenType.RBRACE) && !check(Token.TokenType.EOF)) {
            ASTNode stmt = parseStatement();
            if (stmt != null) {
                stmts.add(stmt);
            }
        }
        expect(Token.TokenType.RBRACE);
        return new ASTNode.Block(stmts);
    }

    // -------------------------------------------------------------------------
    // if
    // -------------------------------------------------------------------------

    private ASTNode.IfStatement parseIf() {
        expect(Token.TokenType.KEYWORD); // "if"
        expect(Token.TokenType.LPAREN);
        ASTNode condition = parseExpression();
        expect(Token.TokenType.RPAREN);
        ASTNode thenBlock = parseBlock();
        ASTNode elseBlock = null;
        if (checkValue(Token.TokenType.KEYWORD, "else")) {
            consume(); // "else"
            if (checkValue(Token.TokenType.KEYWORD, "if")) {
                elseBlock = parseIf();
            } else {
                elseBlock = parseBlock();
            }
        }
        return new ASTNode.IfStatement(condition, thenBlock, elseBlock);
    }

    // -------------------------------------------------------------------------
    // loop
    // -------------------------------------------------------------------------

    private ASTNode.LoopStatement parseLoop() {
        expect(Token.TokenType.KEYWORD); // "loop"
        // Count can be a bare number/expression or wrapped in parens
        ASTNode count;
        if (check(Token.TokenType.LPAREN)) {
            consume();
            count = parseExpression();
            expect(Token.TokenType.RPAREN);
        } else {
            count = parseExpression();
        }
        ASTNode body = parseBlock();
        return new ASTNode.LoopStatement(count, body);
    }

    // -------------------------------------------------------------------------
    // while
    // -------------------------------------------------------------------------

    private ASTNode.WhileStatement parseWhile() {
        expect(Token.TokenType.KEYWORD); // "while"
        expect(Token.TokenType.LPAREN);
        ASTNode condition = parseExpression();
        expect(Token.TokenType.RPAREN);
        ASTNode body = parseBlock();
        return new ASTNode.WhileStatement(condition, body);
    }

    // -------------------------------------------------------------------------
    // var declaration: var IDENT = expr ;
    // -------------------------------------------------------------------------

    private ASTNode.Assignment parseVarDeclaration() {
        expect(Token.TokenType.KEYWORD); // "var"
        Token name = expect(Token.TokenType.IDENTIFIER);
        expect(Token.TokenType.ASSIGN);
        ASTNode value = parseExpression();
        optionalSemicolon();
        return new ASTNode.Assignment(name.getValue(), value);
    }

    // -------------------------------------------------------------------------
    // assignment: IDENT = expr ;
    // -------------------------------------------------------------------------

    private ASTNode.Assignment parseAssignment() {
        Token name = expect(Token.TokenType.IDENTIFIER);
        expect(Token.TokenType.ASSIGN);
        ASTNode value = parseExpression();
        optionalSemicolon();
        return new ASTNode.Assignment(name.getValue(), value);
    }

    // -------------------------------------------------------------------------
    // Function call (as expression, no trailing semicolon consumed here)
    // -------------------------------------------------------------------------

    private ASTNode.FunctionCall parseFunctionCallExpression() {
        Token name = consume(); // IDENTIFIER or BUILTIN_FUNC
        expect(Token.TokenType.LPAREN);
        List<ASTNode> args = new ArrayList<>();
        if (!check(Token.TokenType.RPAREN)) {
            args.add(parseExpression());
            while (check(Token.TokenType.COMMA)) {
                consume();
                args.add(parseExpression());
            }
        }
        expect(Token.TokenType.RPAREN);
        return new ASTNode.FunctionCall(name.getValue(), args);
    }

    // -------------------------------------------------------------------------
    // Expressions
    // -------------------------------------------------------------------------

    private ASTNode parseExpression() {
        return parseLogical();
    }

    private ASTNode parseLogical() {
        ASTNode left = parseComparison();
        while (check(Token.TokenType.AND) || check(Token.TokenType.OR)) {
            String op = consume().getValue();
            ASTNode right = parseComparison();
            left = new ASTNode.BinaryOp(left, op, right);
        }
        return left;
    }

    private ASTNode parseComparison() {
        ASTNode left = parseAdditive();
        while (check(Token.TokenType.EQ) || check(Token.TokenType.NEQ)
                || check(Token.TokenType.LT) || check(Token.TokenType.GT)
                || check(Token.TokenType.LEQ) || check(Token.TokenType.GEQ)) {
            String op = consume().getValue();
            ASTNode right = parseAdditive();
            left = new ASTNode.BinaryOp(left, op, right);
        }
        return left;
    }

    private ASTNode parseAdditive() {
        ASTNode left = parseMultiplicative();
        while (check(Token.TokenType.PLUS) || check(Token.TokenType.MINUS)) {
            String op = consume().getValue();
            ASTNode right = parseMultiplicative();
            left = new ASTNode.BinaryOp(left, op, right);
        }
        return left;
    }

    private ASTNode parseMultiplicative() {
        ASTNode left = parseUnary();
        while (check(Token.TokenType.MULTIPLY) || check(Token.TokenType.DIVIDE) || check(Token.TokenType.PERCENT)) {
            String op = consume().getValue();
            ASTNode right = parseUnary();
            left = new ASTNode.BinaryOp(left, op, right);
        }
        return left;
    }

    private ASTNode parseUnary() {
        if (check(Token.TokenType.NOT)) {
            consume();
            return new ASTNode.UnaryOp("!", parseUnary());
        }
        if (check(Token.TokenType.MINUS)) {
            consume();
            return new ASTNode.UnaryOp("-", parseUnary());
        }
        return parsePrimary();
    }

    private ASTNode parsePrimary() {
        Token cur = current();

        if (cur.getType() == Token.TokenType.NUMBER) {
            consume();
            return new ASTNode.NumberLiteral(Double.parseDouble(cur.getValue()));
        }

        if (cur.getType() == Token.TokenType.STRING) {
            consume();
            return new ASTNode.StringLiteral(cur.getValue());
        }

        if (cur.getType() == Token.TokenType.BOOLEAN) {
            consume();
            return new ASTNode.BooleanLiteral(cur.getValue().equals("true"));
        }

        // Function call or identifier
        if (cur.getType() == Token.TokenType.IDENTIFIER || cur.getType() == Token.TokenType.BUILTIN_FUNC) {
            if (peek(1).getType() == Token.TokenType.LPAREN) {
                return parseFunctionCallExpression();
            }
            consume();
            return new ASTNode.Identifier(cur.getValue());
        }

        if (cur.getType() == Token.TokenType.LPAREN) {
            consume();
            ASTNode expr = parseExpression();
            expect(Token.TokenType.RPAREN);
            return expr;
        }

        throw new ParseException(
                "Unexpected token: " + cur.getType() + " ('" + cur.getValue() + "')",
                cur.getLine(), cur.getColumn()
        );
    }
}
