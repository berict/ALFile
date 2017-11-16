package berict.alfile.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static berict.alfile.Main.DEBUG;

public class FileProcessor {

    public static boolean move(File file) {
        if (DEBUG) {
            System.out.println("Move [" + file.getOriginal().getAbsolutePath() + "] to [" + file.getFullPath() + "]");
        }
        try {
            // TODO issues #3
            // new File() -- .exists(); with the changed file path
            Files.move(Paths.get(file.getOriginal().getAbsolutePath()), Paths.get(file.getFullPath()), REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean rename(File file) {
        return move(file);
    }
}
