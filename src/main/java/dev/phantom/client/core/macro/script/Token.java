package dev.phantom.client.core.macro.script;

public class Token {

    public enum TokenType {
        // Literals
        NUMBER,
        STRING,
        BOOLEAN,

        // Names
        IDENTIFIER,
        KEYWORD,
        BUILTIN_FUNC,

        // Grouping / punctuation
        LPAREN,
        RPAREN,
        LBRACE,
        RBRACE,
        SEMICOLON,
        COMMA,

        // Assignment
        ASSIGN,

        // Arithmetic
        PLUS,
        MINUS,
        MULTIPLY,
        DIVIDE,
        PERCENT,

        // Comparison
        EQ,
        NEQ,
        LT,
        GT,
        LEQ,
        GEQ,

        // Logical
        AND,
        OR,
        NOT,

        // Whitespace / structure
        NEWLINE,
        EOF,
        COMMENT
    }

    private final TokenType type;
    private final String value;
    private final int line;
    private final int column;

    public Token(TokenType type, String value, int line, int column) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return "Token{" + type + ", '" + value + "', line=" + line + ", col=" + column + "}";
    }
}
