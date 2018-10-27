package sample.misc;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RichText {

    private char SPECIAL_CHAR = '&';
    private String text;
    private double customSize = -1; // if this is not set i wont do anything
    private String customFont = ""; // if this is not set i wont do anything
    private Map<String, String> activeStyles = new HashMap<>(); // FILL,FONT,STYLE,RESET
    private ArrayList<Text> newText = new ArrayList<>();

    public RichText(String text) {
        this.text = text;
    }

    public ArrayList<Text> translateCodes() {

        if(text.indexOf(SPECIAL_CHAR) > -1) {
            String[] tokens = text.split("(?=&[a-z1-4])");

            for(String token : tokens) {
                System.out.println("Token:"+token);
                String clearToken =  extractStyles(token); // this changes activeStyles
                if(!clearToken.isEmpty()) {
                    Text coloredText = applyStyles(clearToken); // this applies active styles
                    newText.add(coloredText);
                }
            }
            return newText;
        }else{
            newText.add(new Text(text));
            return newText;
        }
    }

    private Entry<String, String> getStyle(char ch) {
        switch(ch) {
            case 'r':
                return new Entry<>("RESET","RESET");
            case '1':
                return new Entry<>("WEIGHT","BOLD");
            case '2':
                return new Entry<>("POSTURE","ITALIC");
            case 'a':
                return new Entry<>("FILL","AQUA");
            case 'b':
                return new Entry<>("FILL","#0000ff");
            case 'c':
                return new Entry<>("FILL","CADETBLUE");
            case 'd':
                return new Entry<>("FILL","CYAN");
            case 'e':
                return new Entry<>("FILL","#0000ff");
            case 'f':
                return new Entry<>("FILL","#0000ff");
            case 'g':
                return new Entry<>("FILL","GOLDENROD");
            case 'h':
                return new Entry<>("FILL","GRAY");
            case 'i':
                return new Entry<>("FILL","#0000ff");
            case 'j':
                return new Entry<>("FILL","#0000ff");
            case 'k':
                return new Entry<>("FILL","#0000ff");
            case 'l':
                return new Entry<>("FILL","LIGHTGRAY");
            case 'm':
                return new Entry<>("FILL","#0000ff");
            case 'n':
                return new Entry<>("FILL","#0000ff");
            case 'o':
                return new Entry<>("FILL","#0000ff");
            case 'p':
                return new Entry<>("FILL","#0000ff");
            case 'q':
                return new Entry<>("FILL","#0000ff");
            case 's':
                return new Entry<>("FILL","#0000ff");
            case 't':
                return new Entry<>("FILL","#0000ff");
            case 'u':
                return new Entry<>("FILL","#0000ff");
            case 'v':
                return new Entry<>("FILL","#0000ff");
            case 'w':
                return new Entry<>("FILL","#0000ff");
            case 'x':
                return new Entry<>("FILL","WHITESMOKE");
            case 'y':
                return new Entry<>("FILL","#0000ff");
            case 'z':
                return new Entry<>("FILL","#0000ff");
            default:
                return new Entry<>("FILL","#ffffff");
        }
    }

    private Text applyStyles(final String t) {
        Text token = new Text(t);
        String family1 = (this.customFont.isEmpty()) ? token.getFont().getFamily() : this.customFont;
        double size1 = (this.customSize == -1) ? token.getFont().getSize() : this.customSize;
        token.setFont(Font.font(family1, size1));

        if(activeStyles.containsKey("RESET")) {
            activeStyles.clear();
            token.setFill(Color.WHITESMOKE);
        }
        if(activeStyles.containsKey("FILL")) {
            token.setFill(Color.valueOf(activeStyles.get("FILL")));
        }
        if(activeStyles.containsKey("WEIGHT")) {
            String family = (this.customFont.isEmpty()) ? token.getFont().getFamily() : this.customFont;
            FontWeight style = FontWeight.valueOf(activeStyles.get("WEIGHT"));
            double size = (this.customSize == -1) ? token.getFont().getSize() : this.customSize;
            token.setFont(Font.font(family, style, size));
        }
        if(activeStyles.containsKey("POSTURE")) {
            String family = (this.customFont.isEmpty()) ? token.getFont().getFamily() : this.customFont;
            FontPosture style = FontPosture.valueOf(activeStyles.get("POSTURE"));
            double size = (this.customSize == -1) ? token.getFont().getSize() : this.customSize;
            token.setFont(Font.font(family, style, size));
        }
        return token;
    }

    private String extractStyles(String token) {
        // a lot of changes over a string creates a lot of instances in the heap
        //StringBuilder sb = new StringBuilder(token);
        Pattern patternStyle = Pattern.compile(SPECIAL_CHAR+"[a-z1-4]");
        Matcher styleMatch = patternStyle.matcher(token);

        while(styleMatch.find()) {
            int pos = styleMatch.start();
            Entry<String, String> style = getStyle(token.charAt(pos+1));
            activeStyles.put(style.getKey(), style.getValue());
            System.out.println("Before:"+token);
            token = token.substring(pos+2);          // i had problem with delete() in a StringBuilder
            styleMatch = patternStyle.matcher(token);// thats why i am updating the matcher now in a String :/
            System.out.println("After:"+token);
        }
        return token;
    }

    public void setCustomFont(String family) {
        this.customFont = family;
    }
    public void setCustomSize(double size) {
        if(size > 0)
            this.customSize = size;
    }
}
