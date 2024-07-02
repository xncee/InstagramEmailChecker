package threading;

import java.util.ArrayList;
import java.util.List;

public class Threading extends Thread {

    public static void startAll(List<Threading> threads) {
        for (Threading t: threads) {
            t.start();
        }
    }
}
