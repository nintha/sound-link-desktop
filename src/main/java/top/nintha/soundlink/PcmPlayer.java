package top.nintha.soundlink;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileInputStream;

public class PcmPlayer {
    private static final AudioFormat format = new AudioFormat(44100, 16, 1, true, false);

    public static void main(String[] args) throws Exception {
        File file = new File("out.pcm");
        try (FileInputStream in = new FileInputStream(file)) {
            SourceDataLine auline = AudioSystem.getSourceDataLine(format);
            auline.open(format);
            auline.start();
            byte[] buffer = new byte[512];
            int len;
            while ((len = in.read(buffer)) > 0) {

                auline.write(buffer, 0, len);
            }
        }

    }


}
