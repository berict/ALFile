package berict.alfile;

import berict.alfile.main.MainFormController;

public class Main {

    public static boolean DEBUG = true;

    public static void main(String[] args) {
        init();
    }

    static void init() {
        // form method
        MainFormController controller = new MainFormController();
    }

    public static void log(String message) {
        if (DEBUG)
            System.out.println(message);
    }
}
