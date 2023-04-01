package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.security.auth.login.Configuration;

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
        boolean rule_conf = false;
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
            String[] line = lets.get(i).split(" ");

            // 0: let
            // 1: name
            // 2: = 
            // 3: value

            String tokenName = line[1];

            char[] value = line[3].toCharArray();

            if (value[3] == '[') {
                // It's a simple definition

            } else {
                // check for other lexemes in value
                String foundLex = "";
                int foundLatestTokenPos = 0;
                int pos = 0;
                String value_copy = new String(value);
                while (value_copy.trim() != null) {
                    for (int c = pos; c < value.length; c++) {
                        foundLex += c;

                        // Check if lexeme exists
                        if (Universal.tokenExist(foundLex)) {
                            Token t = Universal.getToken(foundLex);


                            foundLatestTokenPos = c;
                        }
                    }

                    // check for change
                    if (pos == foundLatestTokenPos) {
                        // no lexeme was found

                    } else {

                    }

                    pos = foundLatestTokenPos;
                }
                
            }

            
    
        }

    }

    private void rulesToToken() {

    }

    private void tokensToSymbolRegex() {}

}
