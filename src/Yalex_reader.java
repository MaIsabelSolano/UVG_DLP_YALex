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

    public Yalex_reader(String filename) {
        this.file_name = filename;
    }

    public void read() {
        fileToInfo();
        separateGroups();
        letsToTokens();
        rulesToToken();
        tokensToSymbolRegex();
    }

    private void fileToInfo() {
        try {
            reader = new BufferedReader(new FileReader(file_name));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
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

            // DELETE LATER
            System.out.println("no comments:");
            for (int i = 0; i < info.size(); i++) {
                System.out.println(info.get(i));
            }
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
                                }
                                c += 2; // skip the - and the last value


                            }
                        } else if (value[c] == '\\') {
                            if (value[c + 1] == 't') {
                                String lexemeName = "\t";
                                Token t = new Token(lexemeName, true);
                                currentToken.addValueToken(t);

                                c += 1;

                            } else if (value[c + 1] == 'n') {
                                String lexemeName = "\n";
                                Token t = new Token(lexemeName, true);
                                currentToken.addValueToken(t);

                                c += 1;
                            }

                        } else {
                            Token t = new Token(""+value[c], true);
                            currentToken.addValueToken(t);
                            
                        }
                    }
                }
                

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
                        if (tokenExist(Universal.tokens, foundLex)) {

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

                        // retrun to value
                        value = value_temp.toCharArray();

                        foundLatestTokenPos = 0;
                        lastFoundLex = "";
                        foundLex = "";

                    } else if (!lastFoundLex.equals("")) {
                        // A lexeme was found
                        Token t = getToken(Universal.tokens, lastFoundLex);

                        // Add the new token to the curren token's value
                        currentToken.addValueToken(t);
                        
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
            Universal.tokens.add(currentToken);
    
        }

        System.out.println("\n_______Lexemes_______");
        for (Token t: Universal.tokens) {
            System.out.println(t);
        }

    }

    private void rulesToToken() {

    }

    private void tokensToSymbolRegex() {}

    private boolean tokenExist(ArrayList<Token> toks, String lex) {
        for (Token t: toks) {
            if (t.getLexeme().equals(lex)) return true;
        }

        return false;
    }

    private Token getToken(ArrayList<Token> toks, String lex)  {
        for (Token t: toks) {
            if (t.getLexeme().equals(lex)) return t;
        }

        return null;
    }

}

enum Alphabet {
    A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z,
    a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z
}
