package berict.alfile.file;

import java.net.URI;

public class File extends java.io.File {

    /**
     * The prefix concept is used to handle root directories on UNIX platforms, and drive specifiers, root directories and UNC pathnames on Microsoft Windows platforms, as follows:
     *
     * For UNIX platforms, the prefix of an absolute pathname is always "/". Relative pathnames have no prefix.
     * The abstract pathname denoting the root directory has the prefix "/" and an empty name sequence.
     *
     * For Microsoft Windows platforms, the prefix of a pathname that contains a drive specifier consists of the drive letter followed by ":" and possibly followed by "\\" if the pathname is absolute.
     * The prefix of a UNC pathname is "\\\\"; the hostname and the share name are the first two names in the name sequence.
     * A relative pathname that does not specify a drive has no prefix.
     *
     * @link https://docs.oracle.com/javase/7/docs/api/java/io/File.html
     */

    public final static String SEPARATOR = pathSeparator;

    // path : "some/dir/"
    private String path;

    // fileName : "file.extension"
    private String fileName;

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

    private void init(String absolutePath) {
        // initializes the path and fileName values
        int lastIndex = absolutePath.lastIndexOf(SEPARATOR);
        path = absolutePath.substring(0, lastIndex);
        fileName = absolutePath.substring(lastIndex + 1);
        // TODO debug
        System.out.println(toString());
    }

    public void revert() {
        this.path = original.getAbsolutePath();
        this.fileName = original.getName();
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

    public String getPath() {
        return path;
    }

    public void setName(String name) {

    }

    public void apply() {

    }

    @Override
    public String toString() {
        return "File{" +
                "path='" + path + '\'' +
                ", fileName='" + fileName + '\'' +
                ", original=" + original +
                '}';
    }
}
