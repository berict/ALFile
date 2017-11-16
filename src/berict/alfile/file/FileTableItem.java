package berict.alfile.file;

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
        items[2] = file.getOriginal().getAbsolutePath();

        return items;
    }

    public boolean isModified() {
        return file.isModified();
    }

    public boolean exists() {
        return file.getOriginal().exists();
    }
}
