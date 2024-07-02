import java.util.UUID;

public class Generator {
    private final static char[] CHARACTERS = ("0123456789"+"abcdefghijklmnopqrstuvwxyz"+"abcdefghijklmnopqrstuvwxyz".toUpperCase()).toCharArray();
    public static String getRandomUUID() {
        return String.valueOf(UUID.randomUUID());
    }
    public static String getRandomString(int length) {
        String str = "";
        for (int i=0; i<length; i++) {
            int randomIndex = (int) (Math.random()*CHARACTERS.length);
            str = str + CHARACTERS[randomIndex];
        }

        return str;
    }
    public static int getRandomNumber(int min, int max) {
        return (int) (min+Math.random()*(max-min));
    }
}
