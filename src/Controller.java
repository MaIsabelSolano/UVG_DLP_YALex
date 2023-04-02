package src;

import java.util.ArrayList;
import java.util.Stack;

public class Controller {
    public static void main(String[] Args) {

        // instances
        InfixToPostfix itp = new InfixToPostfix();

        Yalex_reader yr = new Yalex_reader("input/slr-1.yal");
        ArrayList<Symbol> regex = yr.read();
        
        System.out.println("\n______to Postfix_______");
        
        Stack<Symbol> postfix = itp.convert(regex);
        String test = "";
        for (Symbol s: postfix) {
            test += String.valueOf(s.c_id);
        }
        System.out.println("\n______Regex Postfix_______");
        System.out.println(test);

    }

}