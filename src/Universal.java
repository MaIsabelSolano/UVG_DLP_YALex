package src;

import java.util.ArrayList;

public class Universal {

    static ArrayList<Token> tokens = new ArrayList<>();

    static boolean tokenExist(String lex) {
        for (Token t: tokens) {
            if (t.getLexeme().equals(lex)) return true;
            break;
        }

        return false;
    }

    static Token getToken(String lex)  {
        for (Token t: tokens) {
            if (t.getLexeme().equals(lex)) return t;
        }

        return null;
    }
    
}
