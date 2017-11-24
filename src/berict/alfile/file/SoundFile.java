package berict.alfile.file;

import berict.alfile.Main;

import java.net.URI;

public class SoundFile extends File {

    public SoundFile(String absolutePath) {
        super(absolutePath);
        initFromAbsolutePath(absolutePath);

        Main.log(toString());
    }

    public SoundFile(java.io.File file) {
        this(file.getAbsolutePath());
    }

    public SoundFile(String parent, String child) {
        super(parent, child);
        initFromAbsolutePath(super.getAbsolutePath());
    }

    public SoundFile(java.io.File parent, String child) {
        super(parent, child);
        initFromAbsolutePath(super.getAbsolutePath());
    }

    public SoundFile(URI uri) {
        super(uri);
        initFromAbsolutePath(super.getAbsolutePath());
    }

    @Override
    protected void initFromAbsolutePath(String absolutePath) {
        super.initFromAbsolutePath(absolutePath);
//        if (!isSound()) {
//            // clear the object if it is not a image file
//            clear();
//        }
    }

    private void clear() {
        fileName = null;
        path = null;
        historyStack = null;
    }
}
