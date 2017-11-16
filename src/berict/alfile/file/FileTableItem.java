package berict.alfile.file;

import static berict.alfile.file.File.SEPARATOR;
import static berict.alfile.file.TableModel.dataList;

public class FileTableItem {

    private File file;

    public FileTableItem(File file) {
        this.file = file;
    }

    public FileTableItem(java.io.File file) {
        this.file = new File(file);
    }

    public File getFile() {
        return file;
    }

    public Object[] toObjects() {
        Object items[] = new Object[3];
        // original file name
        items[0] = file.getOriginal().getName();
        // changed file name
        items[1] = file.getFileName();
        // file location
        items[2] = getLocation();

        return items;
    }

    public boolean isModified() {
        return file.isModified();
    }

    public boolean hasDuplicate() {
        boolean duplicate = false;
        for (FileTableItem item : dataList) {
            File itemFile = item.getFile();
            if (!itemFile.getOriginal().getAbsolutePath().equals(file.getOriginal().getAbsolutePath())
                    && itemFile.getFullPath().equals(file.getFullPath())
                    && itemFile.getParent().equals(file.getParent())) {
                // different file but same output name
                duplicate = true;
                break;
            }
        }
        return duplicate;
    }

    private String getLocation() {
        String paths[] = file.getOriginal().getParentFile().getAbsolutePath()
                .replace(SEPARATOR, ">")
                .split(">");
        int subfolderCount = 3;
        if (paths.length > subfolderCount) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("...");
            stringBuilder.append(SEPARATOR);
            for (int i = paths.length - subfolderCount; i < paths.length; i++) {
                stringBuilder.append(paths[i]);
                stringBuilder.append(SEPARATOR);
            }
            return stringBuilder.toString();
        } else {
            return file.getOriginal().getParentFile().getAbsolutePath() + SEPARATOR;
        }
    }

    public boolean exists() {
        return file.getOriginal().exists();
    }
}
