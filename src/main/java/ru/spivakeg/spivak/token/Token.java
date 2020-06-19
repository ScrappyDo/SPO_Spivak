package ru.spivakeg.spivak.token;

import ru.spivakeg.spivak.lex.Lexems;

public class Token {

    private final Lexems lexems;
    private final String value;

    public Token(Lexems type, String value) {
        this.lexems = type;
        this.value = value;
    }

    public Lexems getLexems() {
        return lexems;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Token [" + "lexem = " + lexems + ", value = '" + value + '\'' + ']';
    }
}
