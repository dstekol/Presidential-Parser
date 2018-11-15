import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {
    static ArrayList<String> wordStrings = new ArrayList<>();;
    public static void parse(String text) {
        ArrayList<String> textWords = prepareList(text);
        splitNonWords(textWords);
        wordStrings.addAll(textWords);
        for(int i=0; i<textWords.size(); i++) {
            Word.addInfo(textWords, i);
        }
    }
    
    
    private static ArrayList<String> prepareList(String text) {
        List<String> tWords = Arrays.asList(text.split(" "));
        ArrayList<String> textWords = new ArrayList<>();
        textWords.addAll(tWords);
        for(int i=0; i<textWords.size(); i++) {
            textWords.set(i, textWords.get(i).trim());
            if(textWords.get(i).equals("")) {
                textWords.remove(i);
                i--;
            }
        }
        return textWords;
    }
    
    public static void splitNonWords(ArrayList<String> textWords) {
        String currentStr;
        boolean isActualWord;
        for(int i=0; i<textWords.size(); i++) {
            currentStr = textWords.get(i);
            isActualWord = isWordChar(currentStr.charAt(0));
            for(int j = 0; j<currentStr.length(); j++) {
                if(isWordChar(currentStr.charAt(j))!=isActualWord) {
                    textWords.set(i, currentStr.substring(0, j));
                    textWords.add(i+1, currentStr.substring(j));
                    break;
                }
            }
        }
    }


    public static String readFile(String s) throws IOException {
        FileReader fr = new FileReader(s);
        BufferedReader br = new BufferedReader(fr);
        String text = "";
        String line = br.readLine();
        while(line!=null) {
            
            text += line + " ";
            line = br.readLine();
            
        }
        fr.close();
        return text;
    }
    
    public static boolean isWordChar(char c) {
        return Character.isLetter(c) || c=='\'';
    }
}
