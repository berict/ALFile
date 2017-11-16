package berict.alfile.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static berict.alfile.Main.DEBUG;

public class FileProcessor {

    public static boolean move(File file) {
        return move(file.getOriginal().getAbsolutePath(), file.getFullPath());
    }

    public static boolean move(String source, String target) {
        if (new File(source).getOriginal().exists()) {
            if (DEBUG) {
                System.out.println("Move [" + source + "] to [" + target + "]");
            }
            try {
                Files.move(Paths.get(source), Paths.get(target), REPLACE_EXISTING);
                return true;
            } catch (IOException e) {
                if (DEBUG) {
                    e.printStackTrace();
                }
                return false;
            }
        } else {
            if (DEBUG) {
                System.out.println("File doesn't exist");
            }
            return false;
        }
    }

    public static boolean rename(File file) {
        return move(file);
    }
}
