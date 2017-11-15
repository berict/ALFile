package berict.alfile.file;

import java.net.URI;
import java.text.NumberFormat;

import static berict.alfile.Main.DEBUG;
import static berict.alfile.file.FileProcessor.rename;

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

    public final static String SEPARATOR = separator;

    // path : "some/dir/"
    private String path;

    // fileName : "file.extension"
    private String fileName;

    // path + fileName returns the whole absolutePath

    // backup for the changed file objects
    private java.io.File original;

    public File(String absolutePath) {
        super(absolutePath);
        original = this;
        initFromAbsolutePath(absolutePath);

        if (DEBUG) {
            System.out.println(toString());
        }
    }

    public File(java.io.File file) {
        this(file.getAbsolutePath());
    }

    public File(String parent, String child) {
        super(parent, child);
        original = this;
        initFromAbsolutePath(super.getAbsolutePath());
    }

    public File(java.io.File parent, String child) {
        super(parent, child);
        original = this;
        initFromAbsolutePath(super.getAbsolutePath());
    }

    public File(URI uri) {
        super(uri);
        original = this;
        initFromAbsolutePath(super.getAbsolutePath());
    }

    private void initFromAbsolutePath(String absolutePath) {
        // initializes the path and fileName values
        int lastIndex = absolutePath.lastIndexOf(SEPARATOR);
        System.out.println(SEPARATOR + "/" + lastIndex);

        path = absolutePath.substring(0, lastIndex + 1);
        fileName = absolutePath.substring(lastIndex + 1);
    }

    public void revert() {
        initFromAbsolutePath(original.getAbsolutePath());
    }

    public void toLowerCase() {
        this.fileName = fileName.toLowerCase();
    }

    public void toUpperCase() {
        String names[] = fileName.split("\\.");
        if (names.length > 0) {
            // only uppercase the actual file 'name', not the extension
            names[0] = names[0].toUpperCase();

            StringBuilder stringBuilder = new StringBuilder();
            for (String name : names) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append(".");
                }
                stringBuilder.append(name);
            }

            this.fileName = stringBuilder.toString();
        } else {
            System.out.println("No filename found");
        }
    }

    public void changeCase(boolean isUpper) {
        if (isUpper) {
            toUpperCase();
        } else {
            toLowerCase();
        }
    }

    public void replaceAll(String regex, String replacement) {
        this.fileName = fileName.replaceAll(regex, replacement);
    }

    public void replaceFirst(String regex, String replacement) {
        this.fileName = fileName.replaceFirst(regex, replacement);
    }

    public void replaceExtension(String replacement) {
        String names[] = fileName.split("\\.");
        if (names.length > 0) {
            // only uppercase the actual file 'name', not the extension
            this.fileName = names[0] + "." + replacement;
        } else {
            System.out.println("No filename found");
        }
    }

    public void insert(String value, boolean isFront, boolean containExtension) {
        if (isFront) {
            insertAtStart(value);
        } else {
            // default to not contain extension
            insertAtEnd(value, containExtension);
        }
    }

    public void insertAtStart(String value) {
        this.fileName = value + fileName;
    }

    public void insertAtEnd(String value, boolean containExtension) {
        if (containExtension) {
            this.fileName = fileName + value;
        } else {
            String names[] = fileName.split("\\.");
            if (names.length > 0) {
                // only uppercase the actual file 'name', not the extension
                names[0] = names[0] + value;

                StringBuilder stringBuilder = new StringBuilder();
                for (String name : names) {
                    if (stringBuilder.length() != 0) {
                        stringBuilder.append(".");
                    }
                    stringBuilder.append(name);
                }

                this.fileName = stringBuilder.toString();
            } else {
                System.out.println("No filename found");
            }
        }
    }

    public void insertFileIndex(int index, int digit) {
        NumberFormat numberFormat = NumberFormat.getInstance();

        if (Math.abs(Math.log10(index)) <= digit) {
            numberFormat.setMinimumIntegerDigits(digit);
            numberFormat.setMaximumIntegerDigits(digit);

            this.fileName = numberFormat.format(index) + fileName;
        } else {
            // TODO this should be a dialog
            System.out.println("Digit should be bigger than the index value");
        }
    }

    public void setName(String name, boolean containExtension) {
        if (containExtension) {
            this.fileName = name;
        } else {
            String names[] = fileName.split("\\.");
            if (names.length > 0) {
                // only uppercase the actual file 'name', not the extension
                names[0] = name;

                StringBuilder stringBuilder = new StringBuilder();
                for (String value : names) {
                    if (stringBuilder.length() != 0) {
                        stringBuilder.append(".");
                    }
                    stringBuilder.append(value);
                }

                this.fileName = stringBuilder.toString();
            } else {
                System.out.println("No filename found");
            }
        }
    }

    public void apply(TableModel tableModel) {
        // TODO add move()
        rename(this);
        original = new java.io.File(getFullPath());
        tableModel.update();
    }

    public java.io.File getOriginal() {
        return original;
    }

    public String getFullPath() {
        return path + fileName;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public String toString() {
        return "path=" + path + ", fileName=" + fileName;
    }
}
