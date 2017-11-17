package berict.alfile.file;

import berict.alfile.Main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class FileProcessor {

    public static boolean move(File file) {
        return move(file.getOriginal().getAbsolutePath(), file.getFullPath());
    }

    public static boolean move(String source, String target) {
        if (new File(source).getOriginal().exists()) {
            Main.log("Move [" + source + "] to [" + target + "]");
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
            Main.log("File doesn't exist");
            return false;
        }
    }

    public static boolean rename(File file) {
        return move(file);
    }

    public static boolean writeToFile(String location, String content) {
        BufferedWriter bw = null;
        FileWriter fw = null;

        try {
            fw = new FileWriter(location);
            bw = new BufferedWriter(fw);
            bw.write(content);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (fw != null)
                    fw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                return false;
            }
        }

        return true;
    }
}
