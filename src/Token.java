package src;

import java.util.ArrayList;

public class Token {

    private String lexeme;
    private ArrayList<Token> value = new ArrayList<>();
    private String function; 
    private boolean terminal;
    private boolean isOperator = false;

    public Token(String lexeme, ArrayList<Token> value, boolean terminal) {
        this.lexeme = lexeme;
        this.value = value;
        this.terminal = terminal;
    }

    public Token(String lexeme, Boolean terminal) {
        this.lexeme = lexeme;
        this.terminal = terminal;
    }

    public void deleteLastOr() {
        if (value.get(value.size()-1).lexeme.equals("|")) {
            value.remove(value.size()-1);
        }
    }

    /* Getters and setters */
    public String getLexeme() {
        return lexeme;
    }

    public String getFunction() {
        return function;
    }

    public ArrayList<Token> getValue() {
        return value;
    }

    public boolean isOperator() {
        return isOperator;
    }

    public boolean isTerminal() {
        return terminal;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public void setAsOperator() {
        this.isOperator = true;
    }

    public void setOperator(boolean isOperator) {
        this.isOperator = isOperator;
    }

    public void addValueToken(Token t) {
        value.add(t);
    }

    public void addValueToken(Token t, int pos) {
        value.add(pos, t);
    }

    @Override
    public String toString() {
        String print = "";
        print += "{";
        print += "\n\tlexeme: \"" + lexeme+ "\""; 
        print += "\n\tvalue: [";
        for (Token t: value) {
            print += t.getLexeme() + " ";
        }
        print += "]";
        print += "\n\tfunction: " + function;
        print += "\n}";
        return print;
    }
    
}
