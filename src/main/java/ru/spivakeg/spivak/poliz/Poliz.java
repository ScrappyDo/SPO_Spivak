package ru.spivakeg.spivak.poliz;

import ru.spivakeg.spivak.lex.Lexems;
import ru.spivakeg.spivak.token.Token;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class Poliz {

    private LinkedList<Token> poliz = new LinkedList<>();
    Queue<Token> tokens = new LinkedList<>();

    public Poliz(LinkedList<Token> tokens) {
        this.tokens = tokens;
    }
    public  LinkedList<Token> makePoliz() {
        while (!tokens.isEmpty()) {
            Token token = tokens.peek();
            if (token.getLexems() != Lexems.WHILE_KW && token.getLexems() != Lexems.IF_KW) {
                makePolizFromExpr(tokens);
            }
            else if (token.getLexems() == Lexems.WHILE_KW){
                makePolizFromWhile(tokens);
            } else {
                makePolizFromIf(tokens);
            }
        }

        return poliz;
    }

    private void makePolizFromWhile(Queue<Token> input) {
        Queue<Token> boolExpr = new LinkedList<>();
        input.poll();
        Token token = input.poll();
        int index = poliz.size();
        while (token.getLexems() != Lexems.OPEN_BRACE) {
            boolExpr.add(token);
            token = input.poll();
        }

        makePolizFromExpr(boolExpr);
        poliz.add(new Token(Lexems.MARK_INDEX, Integer.toString(p(poliz.size(), input))));
        poliz.add(new Token(Lexems.MARK, "!F"));

        Queue<Token> expr = new LinkedList<>();
        token = input.poll();
        while (token.getLexems() != Lexems.CLOSE_BRACE) {
            if (token.getLexems() == Lexems.WHILE_KW) {
                makePolizFromExpr(expr);
                makePolizFromWhile(input);
            }
            if (token.getLexems() != Lexems.WHILE_KW)
                expr.add(token);
            token = input.poll();
        }
        makePolizFromExpr(expr);

        poliz.add(new Token(Lexems.MARK_INDEX, Integer.toString(index)));
        poliz.add(new Token(Lexems.MARK, "!"));
    }

    private void makePolizFromIf(Queue<Token> input) {
        Queue<Token> boolExpr = new LinkedList<>();
        input.poll();
        Token token = input.poll();
        int index = poliz.size();
        while (token.getLexems() != Lexems.OPEN_BRACE) {
            boolExpr.add(token);
            token = input.poll();
        }

        makePolizFromExpr(boolExpr);
        poliz.add(new Token(Lexems.MARK_INDEX, Integer.toString(p(poliz.size(), input))));
        Token mark = poliz.getLast();
        poliz.add(new Token(Lexems.MARK, "!F"));

        Queue<Token> expr = new LinkedList<>();
        token = input.poll();
        while (token.getLexems() != Lexems.CLOSE_BRACE) {
            if (token.getLexems() == Lexems.WHILE_KW) {
                makePolizFromExpr(expr);
                makePolizFromWhile(input);
            }
            if (token.getLexems() == Lexems.IF_KW) {
                makePolizFromExpr(expr);
                makePolizFromWhile(input);
            }
            if (token.getLexems() != Lexems.WHILE_KW && token.getLexems() != Lexems.IF_KW)
                expr.add(token);
            token = input.poll();
        }
        makePolizFromExpr(expr);

        poliz.add(mark);
        poliz.add(new Token(Lexems.MARK, "!if"));
    }


    private int p(int size, Queue<Token> tokens) {
        int p = size;
        int i = 1;

        Queue<Token> newtokens = new LinkedList<>(tokens);
        Token newtoken = newtokens.poll();

        while (i > 0){
            assert newtoken != null;
            if (newtoken.getLexems() == Lexems.WHILE_KW) {
                i++;
                p--;
            }
            if (newtoken.getLexems() == Lexems.CLOSE_BRACE) {
                i--;
            }
            newtoken = newtokens.poll();
            assert newtoken != null;
            if (newtoken.getLexems() != Lexems.END_LINE) {
                p++;
            }
        }
        p+=3;

        return p;
    }

    private void makePolizFromExpr(Queue<Token> input) {
        Stack<Token> stack = new Stack<>();

        while (!input.isEmpty()) {
            Token token = input.peek();

            if (token.getLexems() == Lexems.WHILE_KW || token.getLexems() == Lexems.IF_KW) {
                break;
            }

            if (token.getLexems() == Lexems.TYPE) {
                poliz.add(token);
            }

            token = input.poll();

            //Если лексема является числом или переменной, добавляем ее в ПОЛИЗ-массив.
            if (token.getLexems() == Lexems.VAR || token.getLexems() == Lexems.DIGIT) {
                poliz.add(token);
            }

            //Если лексема является бинарной операцией, тогда:
            if (token.getLexems() == Lexems.OP || token.getLexems() == Lexems.LOG_OPERATIONS
                    || token.getLexems() == Lexems.ASSIGN_OPERATIONS || token.getLexems() == Lexems.FUN_OPERATIONS) {
                if (!stack.empty()) {
                    while (!stack.empty() && getPriorOfOp(token.getValue()) >= getPriorOfOp(stack.peek().getValue())) {
                        poliz.add(stack.pop());
                    }
                }
                stack.push(token);
            }

            //Если лексема является открывающей скобкой, помещаем ее в стек.
            if (token.getLexems() == Lexems.OPEN_BRACKET) {
                stack.push(token);
            }

            if (token.getLexems() == Lexems.CLOSE_BRACKET) {
                if (!stack.empty()) {
                    while (!stack.empty() && stack.peek().getLexems() != Lexems.OPEN_BRACKET) {
                        poliz.add(stack.pop());
                    }
                    if (!stack.empty() && stack.peek().getLexems() == Lexems.OPEN_BRACKET) {
                        stack.pop();
                    }
                }
            }

            if (token.getLexems() == Lexems.END_LINE) {
                while (!stack.empty()) {
                    poliz.add(stack.pop());
                }
            }
        }

        while (!stack.empty()) {
            poliz.add(stack.pop());
        }
    }

    private int getPriorOfOp(String op) {
        if (op.equals("*") || op.equals("/"))
            return 0;
        else if (op.equals("*") || op.equals("/"))
            return 1;
        else if (op.equals("+") || op.equals("-"))
            return 2;
        else if (op.equals(">") || op.equals(">=") || op.equals("<") || op.equals("<=") || op.equals("==") || op.equals("!="))
            return 3;
        else
            return 4;
    }
}
