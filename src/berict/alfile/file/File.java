package berict.alfile.file;

import java.net.URI;

public class File extends java.io.File {

    String path;

    // backup for the changed file objects
    private File original;

    public File(String pathname) {
        super(pathname);
        original = this;
    }

    public File(String parent, String child) {
        super(parent, child);
        original = this;
    }

    public File(java.io.File parent, String child) {
        super(parent, child);
        original = this;
    }

    public File(URI uri) {
        super(uri);
        original = this;
    }

    public void toLowerCase() {

    }

    public void toUpperCase() {

    }

    public void replace(String regex) {

    }

    public void insertAtStart() {

    }

    public void insertAtEnd() {

    }

    public void insertFileIndex(int index, String numberFormat) {

    }

    public void setName(String name) {

    }

    public void apply() {

    }
}
