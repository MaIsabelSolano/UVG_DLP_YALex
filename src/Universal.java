package src;

import java.util.ArrayList;

public class Universal {

    static ArrayList<Token> tokens = new ArrayList<>();

    public static boolean tokenExist(ArrayList<Token> toks, String lex) {
        for (Token t: toks) {
            if (t.getLexeme().equals(lex)) return true;
        }

        return false;
    }

    public static Token getToken(ArrayList<Token> toks, String lex)  {
        for (Token t: toks) {
            if (t.getLexeme().equals(lex)) return t;
        }

        return null;
    }
    
}
