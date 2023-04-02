package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Yalex_reader{

    private String file_name = "";
    private BufferedReader reader;
    private ArrayList<String> info = new ArrayList<>();
    private ArrayList<String> lets = new ArrayList<>();
    private ArrayList<String> rules = new ArrayList<>();

    private ArrayList<Token> tokens = new ArrayList<>();

    private ArrayList<Symbol> regex = new ArrayList<>();

    private Symbol LEFTPARAM = new Symbol('(');
    private Symbol RIGHTPARAM = new Symbol(')');

    public Yalex_reader(String filename) {
        this.file_name = filename;

        LEFTPARAM.setOperator(true);
        RIGHTPARAM.setOperator(true);
    }

    public ArrayList<Symbol> read() {
        fileToInfo();
        separateGroups();
        letsToTokens();
        rulesToRegex();

        return regex;
    }

    private void fileToInfo() {
        try {
            reader = new BufferedReader(new FileReader(file_name));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    // if it's not empty 
                    String temp_line = "";
                    char[] characters = line.toCharArray();
                    // searching and eliminating the comment
                    boolean inComment = false;
                    for (int i = 0; i < characters.length; i++) {
                        if (!inComment) {
                            // check for comment
                            if (characters[i] == '(') {
                                if (characters[i+1] == '*') {
                                    inComment = true;

                                    // skip *
                                    i ++;

                                } else { 
                                    // copy to info without comments
                                    temp_line+= characters[i]; }
                            } else {
                                // copy to info without comments
                                temp_line += characters[i];
                            }
                        } else {
                            // check if comment section has ended
                            if (characters[i] == '*') {
                                if (characters[i + 1] == ')') {
                                    inComment = false;

                                    // skip )
                                    i ++;
                                }
                            }
                        }
                        
                    }
                    // add line to info 
                    if (!temp_line.trim().isEmpty()) info.add(temp_line);

                }
            }
            reader.close();

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void separateGroups() {
        int pos = 0;
        for (int i = 0; i < info.size(); i++) {
            String cofiguration = info.get(i).split(" ")[0];
            // check for lets
            if (cofiguration.equals("let")) {
                lets.add(info.get(i));

            } else if (cofiguration.equals("rule")) {
                pos = i;
                break;
            }
        }

        for (int i = pos + 1 ; i < info.size(); i++) {
            rules.add(info.get(i));

        }

    }

    private void letsToTokens() {
        for (int i = 0; i < lets.size(); i++) {
            String[] line = lets.get(i).split(" ", 4);
            for (int s = 0; s < line.length; s++) {
                // System.out.print(line[s] + "("+ s +") ");
            }

            // 0: let
            // 1: name
            // 2: = 
            // 3: value

            String tokenName = line[1];
            char[] value = line[3].toCharArray();

            Token currentToken = new Token(tokenName, false);
            Token or = new Token("|", true);

            if (value[0] == '[') {
                // It's a simple definition
                // first we remove the 's
                String value_string = "";
                for (int c = 0; c < value.length - 1; c++) {
                    if (value[c] != '[' && value[c] != '[' && value[c] != '\'') {
                        value_string += value[c];
                    }
                }
                value = value_string.toCharArray();

                for (int c = 0; c < value.length; c++) {
                    if (c + 1 >= value.length) {
                        // End fo list
                        Token t = new Token(""+value[c], true);
                        currentToken.addValueToken(t);
                    }
                    else {
                        if (value[c + 1] == '-') {
                            // is a secuence
                            if (Character.isDigit(value[c]) && Character.isDigit(value[c + 2])) {
                                for (
                                    int digit = Character.getNumericValue(value[c]); 
                                    digit < Character.getNumericValue(value[c+2]) + 1; 
                                    digit ++ 
                                ) {
                                    String lexemeName = Integer.toString(digit);
                                    Token t = new Token(lexemeName, true);
                                    currentToken.addValueToken(t);

                                    // check if is not the last | to not include it
                                    currentToken.addValueToken(or);
                                }
                                c += 2; // skip the - and the last value

                            } else if (Character.isLetter(value[c]) && Character.isLetter(value[c + 2])) {
                                int begining = Alphabet.valueOf(""+value[c]).ordinal();
                                int end = Alphabet.valueOf(""+value[c+2]).ordinal() + 1;

                                Alphabet[] subset = Arrays.copyOfRange(
                                    Alphabet.values(),
                                    begining,
                                    end
                                );
                                for (Alphabet x: subset) {
                                    Token t = new Token(x.toString(), true);
                                    currentToken.addValueToken(t);

                                    // check if is not the last | to not include it
                                    currentToken.addValueToken(or);
                                }
                                c += 2; // skip the - and the last value


                            }
                        } else if (value[c] == '\\') {
                            if (value[c + 1] == 't') {
                                String lexemeName = "\t";
                                Token t = new Token(lexemeName, true);
                                currentToken.addValueToken(t);

                                // check if is not the last | to not include it
                                if (c + 2 < value.length) currentToken.addValueToken(or);

                                c += 1;

                            } else if (value[c + 1] == 'n') {
                                String lexemeName = "\n";
                                Token t = new Token(lexemeName, true);
                                currentToken.addValueToken(t);

                                // check if is not the last | to not include it
                                if (c + 2 < value.length) currentToken.addValueToken(or);

                                c += 1;

                            } else if (value[c + 1] == 's') {
                                String lexemeName = " ";
                                Token t = new Token(lexemeName, true);
                                currentToken.addValueToken(t);

                                // check if is not the last | to not include it
                                if (c + 2 < value.length) currentToken.addValueToken(or);

                                c += 1;
                            }

                        } else {
                            Token t = new Token(""+value[c], true);
                            currentToken.addValueToken(t);

                            // check if is not the last | to not include it
                            if (c + 1 < value.length) currentToken.addValueToken(or);
                            
                        }
                    }
                }

                // Delete the last or
                currentToken.deleteLastOr();
                

            } else {
                // check for other lexemes in value
                String foundLex = "";
                String lastFoundLex = "";
                int foundLatestTokenPos = 0;
                // char[] value_copy = new char[value.length];
                // System.arraycopy(value, 0, value_copy, 0, value.length);
                while (value.length > 0) {
                    for (int c = 0; c < value.length; c++) {
                        foundLex += value[c];


                        // Check if lexeme exists
                        if (tokenExist(tokens, foundLex)) {

                            foundLatestTokenPos = c;
                            lastFoundLex = new String(foundLex);
                        }
                    }
                    // check for change
                    if (0 == foundLatestTokenPos) {
                        // no lexeme was found
                        String currentChar = "" + value[0];

                        Token t = new Token(currentChar, true);

                        currentToken.addValueToken(t);

                        // Copy value to array
                        String value_temp = "";
                        for (char val_c: value) value_temp += val_c;

                        // remove the newest found lexeme from value
                        value_temp = value_temp.substring(foundLatestTokenPos+1);

                        // return to value
                        value = value_temp.toCharArray();

                        foundLatestTokenPos = 0;
                        lastFoundLex = "";
                        foundLex = "";

                    } else if (!lastFoundLex.equals("")) {
                        // A lexeme was found
                        Token t = getToken(tokens, lastFoundLex);

                        // Add the new token to the curren token's value
                        currentToken.addValueToken(new Token("(", true));
                        currentToken.addValueToken(t);
                        currentToken.addValueToken(new Token(")", true));
                        
                        // Copy value to array
                        String value_temp = "";
                        for (char val_c: value) value_temp += val_c;

                        // remove the newest found lexeme from value
                        value_temp = value_temp.substring(foundLatestTokenPos+1);

                        // retrun to value
                        value = value_temp.toCharArray();

                        foundLatestTokenPos = 0;
                        lastFoundLex = "";
                        foundLex = "";

                    }
                }
                
            }

            // Add current token to the list of existing tokens
            tokens.add(currentToken);
    
        }

    }

    /** */
    private void rulesToRegex() {

        Symbol or = new Symbol('|');
        or.setOperator(true);

        System.out.println("\n_______Rules_______");

        for (String line: rules) {
            line = line.replaceAll("\\s+", " ");
            line = line.substring(1);
            if (line.charAt(0) == '|') line = line.substring(2);
            line = line.replaceAll("\'", "");
            System.out.println(line);

            String[] line_arr = line.split(" ", 2);

            // line_arr[0]: token name
            // line_arr[1]: function (optional)
            
            // determine whether or not the token exists or not
            if (tokenExist(tokens, line_arr[0])) {
                // token is already in the token arraylist
                Token currentToken = getToken(tokens, line_arr[0]);
                
                ArrayList<Symbol> temp = new ArrayList<>();
                temp.add(LEFTPARAM);
                temp.addAll(Production(currentToken, new ArrayList<Symbol>()));
                temp.add(RIGHTPARAM);
                System.out.println("\nProd:\n");
                for (Symbol s: temp) System.out.print(s.c_id);
                System.out.println();

                regex.addAll(temp);
                regex.add(or);

                // update and add function
                if (line_arr.length > 1) {
                    Token tempToken = currentToken;
                    tempToken.setFunction(line_arr[1]);
                    
                    // update
                    tokens.set(tokens.indexOf(currentToken), tempToken);

                }

            } else {
                // token needs to be added

                Token currentToken = new Token(line_arr[0], true);

                Symbol sym = new Symbol(line_arr[0].charAt(0));
                
                System.out.println("\nProd: " + sym);
                // Add to regex
                regex.add(sym);
                regex.add(or);

                // Check for function 
                if (line_arr.length > 1) {
                    currentToken.setFunction(line_arr[1]);
                }
                // Add to tokens
                tokens.add(currentToken);
            }

            
        }

        // removing the last or
        if (regex.get(regex.size() - 1).c_id == '|') {
            regex.remove(regex.size() - 1);
        }

        System.out.println("\n_______Lexemes_______");
        for (Token t: tokens) {
            System.out.println(t);
        }

        System.out.println("\n_______Regex_______");
        System.out.println();
        for (Symbol s: regex) {
            System.out.print(s);
        }
        System.out.println();

        

    }

    /**
     * 
     * @param toks
     * @param lex
     * @return
     */
    private boolean tokenExist(ArrayList<Token> toks, String lex) {
        for (Token t: toks) {
            if (t.getLexeme().equals(lex)) return true;
        }

        return false;
    }

    /**
     * 
     * @param toks
     * @param lex
     * @return
     */
    private Token getToken(ArrayList<Token> toks, String lex)  {
        for (Token t: toks) {
            if (t.getLexeme().equals(lex)) return t;
        }

        return null;
    }

    /**
     * Syntactic Analysis by Recursive dowfall
     * 
     * @param current_t
     * @param s
     * @return
     */
    public ArrayList<Symbol> Production(Token current_t, ArrayList<Symbol> s) {

        Symbol sym = new Symbol(' ');

        for (Token t: current_t.getValue()) {
            // System.out.println("visiting: " + t.getLexeme());
            if (!t.isTerminal()){
                s = Production(t, s);
            } 
            else {
                sym = new Symbol(t.getLexeme().charAt(0));

                // determine if it´s an operator
                if (
                    t.getLexeme().charAt(0) == '|' || t.getLexeme().charAt(0) == '(' ||
                    t.getLexeme().charAt(0) == ')' || t.getLexeme().charAt(0) == '+' ||
                    t.getLexeme().charAt(0) == '*' || t.getLexeme().charAt(0) == '?'
                ) {
                    sym.setOperator(true);
                }
                s.add(sym);
            }
        }
        return s;
        
    }

}

enum Alphabet {
    A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z,
    a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z
}
