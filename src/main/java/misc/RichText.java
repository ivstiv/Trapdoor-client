package misc;

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
    private Map<String, String> activeStyles = new HashMap<>();
    private ArrayList<Text> newText = new ArrayList<>();

    public RichText(String text) {
        this.text = text;
    }

    public ArrayList<Text> translateCodes() {
        if(text.indexOf(SPECIAL_CHAR) > -1) {
            String[] tokens = text.split("(?="+SPECIAL_CHAR+"[a-z1-4])");

            for(String token : tokens) {
                //System.out.println(token);
                // this changes activeStyles and returns a text without codes
                String clearToken =  extractStyles(token);
                if(!clearToken.isEmpty()) {
                    Text coloredText = applyStyles(clearToken);
                    newText.add(coloredText);
                }
            }
            return newText;
        }else{
            Text text2 = new Text(text);
            text2.setFill(Color.WHITESMOKE);
            text2.setFont(Font.font(customFont,customSize));
            newText.add(text2);
            return newText;
        }
    }

    private String extractStyles(String token) {
        Pattern patternStyle = Pattern.compile(SPECIAL_CHAR+"[a-z1-4]");
        Matcher styleMatch = patternStyle.matcher(token);

        while(styleMatch.find()) {
            int pos = styleMatch.start();
            Entry<String, String> style = getStyle(token.charAt(pos+1));
            if(style.getKey().equalsIgnoreCase("RESET")) // if the key is reset empty the styles up to now
                activeStyles.clear();
            activeStyles.put(style.getKey(), style.getValue());
            token = token.substring(pos+2);          // i had problem with delete() in a StringBuilder
            styleMatch = patternStyle.matcher(token);// thats why i am updating the matcher now in a String :/
        }
        return token;
    }

    private Entry<String, String> getStyle(char ch) {
        if(!Character.isDigit(ch))
            return ColorCode.valueOf(String.valueOf(ch)).getEntry();
        switch(ch){
            case '1':
                return ColorCode.BOLD.getEntry();
            case '2':
                return ColorCode.ITALIC.getEntry();
            case '3':
                return ColorCode.UNDERLINED.getEntry();
            case '4':
                return ColorCode.STRIKETHROUGH.getEntry();
            default:
                return ColorCode.BOLD.getEntry(); // this can't be reached because of the regex but just in case
            // TODO: 28-Oct-18 I can add more numbers affecting the size of the text 
        }
    }

    private Text applyStyles(final String t) {
        Text token = new Text(t);
        token.setFill(Color.WHITESMOKE);
        String family1 = (this.customFont.isEmpty()) ? token.getFont().getFamily() : this.customFont;
        double size1 = (this.customSize == -1) ? token.getFont().getSize() : this.customSize;
        token.setFont(Font.font(family1, size1));
        
        if(activeStyles.containsKey("FILL")) {
            token.setFill(Color.valueOf(activeStyles.get("FILL")));
        }
        if(activeStyles.containsKey("BOLD")) {
            String family = token.getFont().getFamily();
            FontWeight style = FontWeight.valueOf("BOLD");
            double size = token.getFont().getSize();
            token.setFont(Font.font(family, style, size));
        }
        if(activeStyles.containsKey("ITALIC")) {
            String family = token.getFont().getFamily();
            FontPosture style = FontPosture.valueOf("ITALIC");
            double size = token.getFont().getSize();
            token.setFont(Font.font(family, style, size));
        }
        if(activeStyles.containsKey("UNDERLINED"))
                    token.setUnderline(true);
        if(activeStyles.containsKey("STRIKETHROUGH"))
                    token.setStrikethrough(true);
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
