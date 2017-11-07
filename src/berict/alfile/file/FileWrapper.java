package berict.alfile.file;

import java.io.File;

public class FileWrapper {

    private String originalName;
    private String processedName;

    private String originalLocation;
    private String processedLocation;

    private File original;

    public FileWrapper(File original) {
        this.original = original;
        this.originalName = original.getName();
        this.processedName = originalName;
        this.originalLocation = original.getAbsolutePath();
        this.processedLocation = originalLocation;
    }

    public FileWrapper changeFileExtension(String extension) {
        // replace the extension
        originalName = originalName.substring(0, originalName.lastIndexOf(".")) + extension;
        originalLocation = originalLocation.substring(0, originalLocation.lastIndexOf(".")) + extension;
        return this;
    }
}
