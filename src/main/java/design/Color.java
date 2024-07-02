package design;

public interface Color {
    // ANSI COLORS
    String RESET = "\033[0m";
    String BLACK = "\033[0;30m";
    String RED = "\033[0;31m";
    String GREEN = "\033[0;32m";
    String YELLOW = "\033[0;33m";
    String BLUE = "\033[0;34m";
    String PURPLE = "\033[0;35m";
    String CYAN = "\033[0;36m";
    String WHITE = "\033[0;37m";

    String BLACK_BACKGROUND = "\033[40m";
    String RED_BACKGROUND = "\033[41m";
    String GREEN_BACKGROUND = "\033[42m";
    String YELLOW_BACKGROUND = "\033[43m";
    String BLUE_BACKGROUND = "\033[44m";
    String PURPLE_BACKGROUND = "\033[45m";
    String CYAN_BACKGROUND = "\033[46m";
    String WHITE_BACKGROUND = "\033[47m";
}