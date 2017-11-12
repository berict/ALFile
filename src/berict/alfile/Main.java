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

        debug();
    }

    private static void debug() {
        File file = new File("C:\\Windows\\explorer.exe");
        file.insertAtStart("ParkMyoungChun-");
        file.apply();

        file.toUpperCase();
        file.apply();

        file.toLowerCase();
        file.apply();

        file.replaceAll("parkmyoungchun", "LeeJuhoLee");
        file.apply();

        file.replaceFirst("Lee", "Kim");
        file.apply();

        file.replaceExtension("jpg");
        file.apply();

        file.insertAtEnd("vapor", false);
        file.apply();

        file.insertAtEnd("wave", true);
        file.apply();

        file.insertFileIndex(420, 2); //should make error
        file.apply();

        file.insertFileIndex(4, 3);
        file.apply();

        file.setName("asdf", false);
        file.apply();

        file.setName("internet-explorer.exe", true);
        file.apply();
    }
}
