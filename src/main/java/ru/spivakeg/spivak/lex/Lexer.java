package ru.spivakeg.spivak.lex;

import ru.spivakeg.spivak.token.Token;

import java.util.LinkedList;
import java.util.regex.Matcher;

public class Lexer {
    private final String rawInput;

    public Lexer(String rawInput) {
        this.rawInput = rawInput;
    }

    public String getRawInput() {
        return rawInput;
    }

    public LinkedList<Token> tokens() {
        String source = rawInput;
        int currentIndex = 0;
        int currentIndexFrom = 0;

        boolean okWaiting = true;
        LinkedList<Token> tokens = new LinkedList<>();
        Lexems prevLexems = null;

        String acc = "";
        while (currentIndex < source.length()) {
            acc = source.substring(currentIndexFrom, currentIndex + 1);
            Lexems currentLexems = null;
            for (Lexems lexems : Lexems.values()) {
                Matcher matcher = lexems.getPattern().matcher(acc);
                if (matcher.find()) {
                    currentLexems = lexems;
                }
            }
            if (currentLexems != null) {
                prevLexems = currentLexems;
            }
            if (okWaiting && currentLexems != null) {
                okWaiting = false;
            }
            if (okWaiting && currentLexems == null) {
                throw new RuntimeException("Incorrect source");
            }
            if (!okWaiting && currentLexems == null) {
                //отбрасываем пробелы
                int lastSymb = acc.length()-1;
                Character ch = acc.charAt(lastSymb);
                while (acc.charAt(lastSymb-1) == ' ')
                    lastSymb--;
                Token token = new Token(prevLexems, acc.substring(0, lastSymb));
                tokens.add(token);
                okWaiting = true;
                currentIndexFrom = currentIndex;
            } else {
                currentIndex = currentIndex + 1;
            }
        }

        Token token = new Token(prevLexems, acc);
        tokens.add(token);

        tokens.add(new Token(Lexems.END_LINE, ""));
        return tokens;
    }
}
