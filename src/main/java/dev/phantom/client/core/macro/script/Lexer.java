package dev.phantom.client.core.macro.script;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Lexer {

    private static final Set<String> KEYWORDS = Set.of(
            "if", "else", "loop", "while", "break", "continue", "var"
    );

    private static final Set<String> BUILTIN_FUNCS = Set.of(
            "sleep", "jump", "sneak", "sprint", "use_item", "left_click", "right_click",
            "swap_to_item", "swap_to_slot", "look_at", "look_at_nearest_player",
            "enable_module", "disable_module", "send_chat", "send_command",
            "key_press", "key_release", "worldedit", "litematica"
    );

    private String source;
    private int pos;
    private int line;
    private int column;
    private List<Token> tokens;

    public List<Token> tokenize(String source) {
        this.source = source;
        this.pos = 0;
        this.line = 1;
        this.column = 1;
        this.tokens = new ArrayList<>();

        while (pos < source.length()) {
            skipWhitespace();
            if (pos >= source.length()) break;

            char c = source.charAt(pos);

            // Newlines
            if (c == '\n') {
                tokens.add(new Token(Token.TokenType.NEWLINE, "\\n", line, column));
                advance();
                line++;
                column = 1;
                continue;
            }

            // Comments
            if (c == '/' && pos + 1 < source.length()) {
                char next = source.charAt(pos + 1);
                if (next == '/') {
                    skipLineComment();
                    continue;
                } else if (next == '*') {
                    skipBlockComment();
                    continue;
                }
            }

            // Numbers
            if (Character.isDigit(c) || (c == '.' && pos + 1 < source.length() && Character.isDigit(source.charAt(pos + 1)))) {
                readNumber();
                continue;
            }

            // Strings
            if (c == '"' || c == '\'') {
                readString(c);
                continue;
            }

            // Identifiers / keywords / builtins / booleans
            if (Character.isLetter(c) || c == '_') {
                readIdentifierOrKeyword();
                continue;
            }

            // Operators and punctuation
            readOperatorOrPunctuation();
        }

        tokens.add(new Token(Token.TokenType.EOF, "", line, column));
        return tokens;
    }

    // -------------------------------------------------------------------------
    // Whitespace
    // -------------------------------------------------------------------------

    /** Skips spaces and tabs (NOT newlines — those are tokens). */
    private void skipWhitespace() {
        while (pos < source.length()) {
            char c = source.charAt(pos);
            if (c == ' ' || c == '\t' || c == '\r') {
                advance();
            } else {
                break;
            }
        }
    }

    // -------------------------------------------------------------------------
    // Comments
    // -------------------------------------------------------------------------

    private void skipLineComment() {
        // consume '//'
        advance();
        advance();
        while (pos < source.length() && source.charAt(pos) != '\n') {
            advance();
        }
    }

    private void skipBlockComment() {
        // consume '/*'
        advance();
        advance();
        while (pos < source.length()) {
            if (source.charAt(pos) == '*' && pos + 1 < source.length() && source.charAt(pos + 1) == '/') {
                advance(); // *
                advance(); // /
                break;
            }
            if (source.charAt(pos) == '\n') {
                line++;
                column = 1;
            }
            advance();
        }
    }

    // -------------------------------------------------------------------------
    // Number
    // -------------------------------------------------------------------------

    private void readNumber() {
        int startLine = line;
        int startCol = column;
        StringBuilder sb = new StringBuilder();
        boolean hasDot = false;

        while (pos < source.length()) {
            char c = source.charAt(pos);
            if (Character.isDigit(c)) {
                sb.append(c);
                advance();
            } else if (c == '.' && !hasDot && pos + 1 < source.length() && Character.isDigit(source.charAt(pos + 1))) {
                hasDot = true;
                sb.append(c);
                advance();
            } else {
                break;
            }
        }

        tokens.add(new Token(Token.TokenType.NUMBER, sb.toString(), startLine, startCol));
    }

    // -------------------------------------------------------------------------
    // String
    // -------------------------------------------------------------------------

    private void readString(char quote) {
        int startLine = line;
        int startCol = column;
        advance(); // consume opening quote
        StringBuilder sb = new StringBuilder();

        while (pos < source.length()) {
            char c = source.charAt(pos);
            if (c == quote) {
                advance(); // consume closing quote
                break;
            } else if (c == '\\') {
                advance(); // consume backslash
                if (pos < source.length()) {
                    char esc = source.charAt(pos);
                    switch (esc) {
                        case 'n' -> sb.append('\n');
                        case 't' -> sb.append('\t');
                        case 'r' -> sb.append('\r');
                        case '\\' -> sb.append('\\');
                        case '"' -> sb.append('"');
                        case '\'' -> sb.append('\'');
                        default -> { sb.append('\\'); sb.append(esc); }
                    }
                    advance();
                }
            } else {
                if (c == '\n') {
                    line++;
                    column = 1;
                }
                sb.append(c);
                advance();
            }
        }

        tokens.add(new Token(Token.TokenType.STRING, sb.toString(), startLine, startCol));
    }

    // -------------------------------------------------------------------------
    // Identifiers / keywords / builtins
    // -------------------------------------------------------------------------

    private void readIdentifierOrKeyword() {
        int startLine = line;
        int startCol = column;
        StringBuilder sb = new StringBuilder();

        while (pos < source.length()) {
            char c = source.charAt(pos);
            if (Character.isLetterOrDigit(c) || c == '_') {
                sb.append(c);
                advance();
            } else {
                break;
            }
        }

        String text = sb.toString();
        Token.TokenType type;

        if (text.equals("true") || text.equals("false")) {
            type = Token.TokenType.BOOLEAN;
        } else if (KEYWORDS.contains(text)) {
            type = Token.TokenType.KEYWORD;
        } else if (BUILTIN_FUNCS.contains(text)) {
            type = Token.TokenType.BUILTIN_FUNC;
        } else {
            type = Token.TokenType.IDENTIFIER;
        }

        tokens.add(new Token(type, text, startLine, startCol));
    }

    // -------------------------------------------------------------------------
    // Operators and punctuation
    // -------------------------------------------------------------------------

    private void readOperatorOrPunctuation() {
        int startLine = line;
        int startCol = column;
        char c = source.charAt(pos);

        switch (c) {
            case '(' -> { tokens.add(new Token(Token.TokenType.LPAREN, "(", startLine, startCol)); advance(); }
            case ')' -> { tokens.add(new Token(Token.TokenType.RPAREN, ")", startLine, startCol)); advance(); }
            case '{' -> { tokens.add(new Token(Token.TokenType.LBRACE, "{", startLine, startCol)); advance(); }
            case '}' -> { tokens.add(new Token(Token.TokenType.RBRACE, "}", startLine, startCol)); advance(); }
            case ';' -> { tokens.add(new Token(Token.TokenType.SEMICOLON, ";", startLine, startCol)); advance(); }
            case ',' -> { tokens.add(new Token(Token.TokenType.COMMA, ",", startLine, startCol)); advance(); }
            case '+' -> { tokens.add(new Token(Token.TokenType.PLUS, "+", startLine, startCol)); advance(); }
            case '-' -> { tokens.add(new Token(Token.TokenType.MINUS, "-", startLine, startCol)); advance(); }
            case '*' -> { tokens.add(new Token(Token.TokenType.MULTIPLY, "*", startLine, startCol)); advance(); }
            case '/' -> { tokens.add(new Token(Token.TokenType.DIVIDE, "/", startLine, startCol)); advance(); }
            case '%' -> { tokens.add(new Token(Token.TokenType.PERCENT, "%", startLine, startCol)); advance(); }
            case '=' -> {
                if (pos + 1 < source.length() && source.charAt(pos + 1) == '=') {
                    tokens.add(new Token(Token.TokenType.EQ, "==", startLine, startCol));
                    advance(); advance();
                } else {
                    tokens.add(new Token(Token.TokenType.ASSIGN, "=", startLine, startCol));
                    advance();
                }
            }
            case '!' -> {
                if (pos + 1 < source.length() && source.charAt(pos + 1) == '=') {
                    tokens.add(new Token(Token.TokenType.NEQ, "!=", startLine, startCol));
                    advance(); advance();
                } else {
                    tokens.add(new Token(Token.TokenType.NOT, "!", startLine, startCol));
                    advance();
                }
            }
            case '<' -> {
                if (pos + 1 < source.length() && source.charAt(pos + 1) == '=') {
                    tokens.add(new Token(Token.TokenType.LEQ, "<=", startLine, startCol));
                    advance(); advance();
                } else {
                    tokens.add(new Token(Token.TokenType.LT, "<", startLine, startCol));
                    advance();
                }
            }
            case '>' -> {
                if (pos + 1 < source.length() && source.charAt(pos + 1) == '=') {
                    tokens.add(new Token(Token.TokenType.GEQ, ">=", startLine, startCol));
                    advance(); advance();
                } else {
                    tokens.add(new Token(Token.TokenType.GT, ">", startLine, startCol));
                    advance();
                }
            }
            case '&' -> {
                if (pos + 1 < source.length() && source.charAt(pos + 1) == '&') {
                    tokens.add(new Token(Token.TokenType.AND, "&&", startLine, startCol));
                    advance(); advance();
                } else {
                    // Single & is not a valid token; skip with error-recovery
                    dev.phantom.client.PhantomClient.LOGGER.warn("[Phantom Lexer] Unexpected character '&' at line {}, col {}", startLine, startCol);
                    advance();
                }
            }
            case '|' -> {
                if (pos + 1 < source.length() && source.charAt(pos + 1) == '|') {
                    tokens.add(new Token(Token.TokenType.OR, "||", startLine, startCol));
                    advance(); advance();
                } else {
                    dev.phantom.client.PhantomClient.LOGGER.warn("[Phantom Lexer] Unexpected character '|' at line {}, col {}", startLine, startCol);
                    advance();
                }
            }
            default -> {
                dev.phantom.client.PhantomClient.LOGGER.warn("[Phantom Lexer] Unknown character '{}' at line {}, col {}", c, startLine, startCol);
                advance();
            }
        }
    }

    // -------------------------------------------------------------------------
    // Advance helper
    // -------------------------------------------------------------------------

    private void advance() {
        pos++;
        column++;
    }
}
