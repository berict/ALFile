package berict.alfile.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static berict.alfile.Main.DEBUG;

public class FileProcessor {

    public static void move(File file) {
        if (DEBUG) {
            System.out.println("Move [" + file.getOriginal().getAbsolutePath() + "] to [" + file.getFullPath() + "]");
        }
        try {
            // TODO issues #3
            // new File() -- .exists(); with the changed file path
            Files.move(Paths.get(file.getOriginal().getAbsolutePath()), Paths.get(file.getFullPath()), REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void rename(File file) {
        move(file);
    }
}
