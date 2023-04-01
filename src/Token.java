package src;

import java.util.ArrayList;

public class Token {

    private String lexeme;
    private ArrayList<Token> value;
    private String function; 
    private boolean terminal;

    public Token(String lexeme, ArrayList<Token> value, boolean terminal) {
        this.lexeme = lexeme;
        this.value = value;
        this.terminal = terminal;
    }

    /* Getters and setters */
    public String getLexeme() {
        return lexeme;
    }

    public String getFunction() {
        return function;
    }

    public boolean isTerminal() {
        return terminal;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    @Override
    public String toString() {
        String print = "";
        print += "\n{";
        print += "\n\tlexeme: \"" + lexeme+ "\""; 
        print += "\n\tvalue: [";
        for (Token t: value) {
            print += t.getLexeme() + ", ";
        }
        print += "]";
        print += "\n\tfunction: " + function;
        print += "}";
        return print;
    }
    
}
