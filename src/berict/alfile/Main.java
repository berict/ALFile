package berict.alfile;

import berict.alfile.file.File;
import berict.alfile.main.MainFormController;
import berict.alfile.main.MainSwingController;

public class Main {

    public static void main(String[] args) {
        init();
    }

    static void init() {
        // full swing method
        MainSwingController controller = new MainSwingController();

        // form method
        //MainFormController controller = new MainFormController();

        File file = new File("C:\\Windows\\explorer.exe");
        file.insertAtStart("parkmyoungchun-");
        file.apply();
    }

}
