package com.example.mchatclient.others;

import java.io.ByteArrayOutputStream;
        import java.io.File;
        import java.io.FileInputStream;
        import java.io.IOException;

public class ImageEncoder {
    public static byte[] encodeImage(String imagePath) throws IOException {
        File imageFile = new File(imagePath);
        try (FileInputStream fis = new FileInputStream(imageFile);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buf = new byte[1024];
            for (int readNum; (readNum = fis.read(buf)) != -1;) {
                bos.write(buf, 0, readNum);
            }
            return bos.toByteArray();
        }
    }
}
