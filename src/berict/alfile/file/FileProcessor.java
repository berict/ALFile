package berict.alfile.file;

public class FileProcessor {

    public static void move(File file) {
        // TODO debug
        System.out.println("Move [" + file.getOriginal().getAbsolutePath() + "] to [" + file.getFullPath() + "]");
    }

    public static void rename(File file) {
        // TODO debug
        System.out.println("Rename [" + file.getOriginal().getAbsolutePath() + "] to [" + file.getFullPath() + "]");
    }
}
