package src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class Controller {
    public static void main(String[] Args) {

        // instances
        InfixToPostfix itp = new InfixToPostfix();

        Yalex_reader yr = new Yalex_reader("input/slr-1.yal");
        ArrayList<Symbol> regex = yr.read();
        
        System.out.println("\n______Concat_______");
        
        Stack<Symbol> postfix = itp.convert(regex);

        HashMap<Integer, Symbol> alphabet = itp.getDic();

        // Adding #. to the end of the regex
        Symbol end = new Symbol('#');
        Symbol concat = new Symbol('.');
        concat.setOperator(true);
        postfix.add(end);
        postfix.add(concat);

        String test = "";
        for (Symbol s: postfix) {
            test += String.valueOf(s.c_id);
        }
        System.out.println("\n______Regex Postfix_______");
        System.out.println(test);

        System.out.println("\nSyntactic Tree");
        SintacticTree sintacticTree = new SintacticTree(postfix);
        // sintacticTree.printTree(sintacticTree.getRoot());
        sintacticTree.TreePrinter(sintacticTree.getRoot(), "", true);
        AFD automata = new AFD(alphabet, sintacticTree);
        System.out.println(automata);

    }

}