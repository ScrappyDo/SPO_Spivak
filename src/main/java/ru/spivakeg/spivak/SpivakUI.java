package ru.spivakeg.spivak;

import ru.spivakeg.spivak.parser.Parser;
import ru.spivakeg.spivak.lex.Lexer;
import ru.spivakeg.spivak.token.Token;
import ru.spivakeg.spivak.poliz.Poliz;
import ru.spivakeg.spivak.poliz.PolizCalculation;

import java.io.File;
import java.util.LinkedList;
import java.util.Scanner;

public class SpivakUI {

    public static void main(String[] args) throws Exception {
        File file = new File("src/spivak.txt");
        Scanner inp = new Scanner(file);
        Lexer lexer = new Lexer(inp.nextLine());
        LinkedList<Token> tokens = lexer.tokens();
        while (inp.hasNext()) {
            lexer = new Lexer(inp.nextLine());
            tokens.addAll(lexer.tokens());
        }

        System.out.println("Токен: ");
        for (Token token: tokens)
            System.out.println(token);

        Parser parser = new Parser(tokens);
        parser.lang();

        System.out.println("\nПолиз:");
        Poliz poliz = new Poliz(tokens);
        LinkedList<Token> testPoliz = poliz.makePoliz();
        for (Token token : testPoliz) {
            System.out.println(token.toString());
        }

        PolizCalculation.calculate(testPoliz);

    }
}
