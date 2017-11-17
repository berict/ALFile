package berict.alfile.file;

import berict.alfile.Main;

import java.net.URI;
import java.text.NumberFormat;

import static berict.alfile.Main.DEBUG;
import static berict.alfile.file.FileProcessor.move;
import static berict.alfile.file.FileProcessor.rename;
import static berict.alfile.main.form.MainForm.makeErrorAlert;

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
    public final static String RESTRICTED_CHARACTERS[] = {"\\", "/", ":", "*", "?", "\"", "<", ">", "|"};
    public final static String RESTRICTED_CHARACTER = "\\ / : * ? \" < > |";

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

        Main.log(toString());
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
        Main.log(SEPARATOR + "/" + lastIndex);
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
            Main.log("No filename found");
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
        if (isAvailableForFileName(replacement)) {
            this.fileName = fileName.replaceAll(regex, replacement);
        } else {
            makeErrorAlert("Following characters are not available for file names: " + RESTRICTED_CHARACTER);
        }
    }

    public void replaceFirst(String regex, String replacement) {
        if (isAvailableForFileName(replacement)) {
            this.fileName = fileName.replaceFirst(regex, replacement);
        } else {
            makeErrorAlert("Following characters are not available for file names: " + RESTRICTED_CHARACTER);
        }
    }

    public void replaceExtension(String replacement) {
        if (isAvailableForFileName(replacement)) {
            String names[] = fileName.split("\\.");
            if (names.length > 0) {
                // only uppercase the actual file 'name', not the extension
                this.fileName = names[0] + "." + replacement;
            } else {
                Main.log("No filename found");
            }
        } else {
            makeErrorAlert("Following characters are not available for file names: " + RESTRICTED_CHARACTER);
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
        if (isAvailableForFileName(value)) {
            this.fileName = value + fileName;
        } else {
            makeErrorAlert("Following characters are not available for file names: " + RESTRICTED_CHARACTER);
        }
    }

    public void insertAtEnd(String value, boolean containExtension) {
        if (isAvailableForFileName(value)) {
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
                    Main.log("No filename found");
                }
            }
        } else {
            makeErrorAlert("Following characters are not available for file names: " + RESTRICTED_CHARACTER);
        }
    }

    public int moveSubfolder(String separator) {
        if (isDirectory()) {
            int process = 0;
            for (java.io.File subfolder : listFiles()) {
                if (subfolder.isDirectory()) {
                    for (java.io.File file : subfolder.listFiles()) {
                        if (move(file.getAbsolutePath(),
                                getFullPath() + SEPARATOR + subfolder.getName() + separator + file.getName())) {
                            process++;
                        }
                    }
                }
            }
            return process;
        } else {
            return -1;
        }
    }

    public String getDirectoryContent() {
        if (isDirectory()) {
            StringBuilder stringBuilder = new StringBuilder();
            for (java.io.File content : listFiles()) {
                File file = new File(content);
                stringBuilder.append("\"");
                stringBuilder.append(file.getType());
                stringBuilder.append("\",");
                stringBuilder.append("\"");
                stringBuilder.append(file.getFileName());
                stringBuilder.append("\",");
                stringBuilder.append("\"");
                stringBuilder.append(file.getFullPath());
                stringBuilder.append("\"");
                stringBuilder.append("\n");
            }
            return stringBuilder.toString();
        } else {
            return null;
        }
    }

    public String getExtension() {
        String names[] = original.getName().split("\\.");
        if (original.getName().contains(".")) {
            if (names.length > 0) {
                return names[names.length - 1];
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public String getType() {
        if (isFile() && getExtension() != null) {
            return "file/" + getExtension();
        } else if (isDirectory()) {
            return "folder";
        } else {
            return "file";
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
            Main.log("Digit should be bigger than the index value");
        }
    }

    public void setName(String name, boolean containExtension) {
        if (isAvailableForFileName(name)) {
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
                    Main.log("No filename found");
                }
            }
        } else {
            makeErrorAlert("Following characters are not available for file names: " + RESTRICTED_CHARACTER);
        }
    }

    public static boolean isAvailableForFileName(String value) {
        if (value != null) {
            boolean available = true;
            for (String s : RESTRICTED_CHARACTERS) {
                if (value.contains(s)) {
                    // not available
                    available = false;
                    break;
                }
            }
            return available;
        } else {
            return false;
        }
    }

    public boolean isModified() {
        return !getFileName().equals(getOriginal().getName());
    }

    public boolean apply(TableModel tableModel) {
        if (isModified()) {
            boolean result;
            if (getFileName().toLowerCase().equals(getOriginal().getName()) ||
                    getFileName().toUpperCase().equals(getOriginal().getName()) ||
                    getOriginal().getName().toLowerCase().equals(getFileName()) ||
                    getOriginal().getName().toUpperCase().equals(getFileName())) {
                // changes the filename twice to change
                result = move(getOriginal().getAbsolutePath(), getFullPath() + ".alfile");
                result = result | move(getFullPath() + ".alfile", getFullPath());
                Main.log("Double switch");
            } else {
                result = rename(this);
            }
            original = new java.io.File(getFullPath());
            tableModel.update();
            return result;
        } else {
            return true;
        }
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
