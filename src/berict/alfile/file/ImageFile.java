package berict.alfile.file;

import berict.alfile.Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;

public class ImageFile extends File {

    public ImageFile(String absolutePath) {
        super(absolutePath);
        initFromAbsolutePath(absolutePath);

        Main.log(toString());
    }

    public ImageFile(java.io.File file) {
        this(file.getAbsolutePath());
    }

    public ImageFile(String parent, String child) {
        super(parent, child);
        initFromAbsolutePath(super.getAbsolutePath());
    }

    public ImageFile(java.io.File parent, String child) {
        super(parent, child);
        initFromAbsolutePath(super.getAbsolutePath());
    }

    public ImageFile(URI uri) {
        super(uri);
        initFromAbsolutePath(super.getAbsolutePath());
    }

    @Override
    protected void initFromAbsolutePath(String absolutePath) {
        super.initFromAbsolutePath(absolutePath);
        if (!isImage()) {
            clear();
        }
    }

    private void clear() {
        fileName = null;
        path = null;
        historyStack = null;
    }

    public ImageIcon getResizedImageIcon(int size) {
        return new ImageIcon(getResizedImage(size));
    }

    public Image getResizedImage(int size) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(getAbsolutePath()));
        } catch (IOException e) {
            Main.log(e.getMessage());
        }

        float width;
        float height;

        Main.log(image.getWidth() + " X " + image.getHeight());

        if (image.getWidth() > image.getHeight()) {
            // width is the main
            width = size;
            height = size * ((float) image.getHeight() / image.getWidth());
        } else {
            width = size * ((float) image.getWidth() / image.getHeight());
            height = size;
        }

        Main.log(width + " X " + height);

        return image.getScaledInstance((int) width, (int) height, Image.SCALE_FAST);
    }
}
