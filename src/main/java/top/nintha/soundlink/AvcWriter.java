package top.nintha.soundlink;

import lombok.extern.slf4j.Slf4j;

import javax.sound.sampled.*;
import java.io.Closeable;
import java.io.IOException;

@Slf4j
public class AvcWriter implements Closeable {
    private static final AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
    private SourceDataLine vacLine;

    public AvcWriter() throws LineUnavailableException {
        vacLine = AudioSystem.getSourceDataLine(format); //getVacInputSourceDataLine();
        if (vacLine != null) {
            vacLine.open(format);
            vacLine.start();
        }
    }

    private SourceDataLine getVacInputSourceDataLine() throws LineUnavailableException {
        Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
        for (Mixer.Info info : mixerInfo) {
            if (info.getName().contains("CABLE Input") && !info.getName().contains("Port")) {
                return AudioSystem.getSourceDataLine(format, info);
            }
        }
        throw new RuntimeException("not found vac mixer");
    }

    public void write(byte[] data) {
//        log.info("[AvcWriter] write, active={}, running={}, open={} > {}", vacLine.isActive(), vacLine.isRunning(), vacLine.isOpen(), Arrays.toString(data));
        if (vacLine != null && vacLine.isOpen()) {
            vacLine.write(data, 0, data.length);
        }
    }


    @Override
    public void close() throws IOException {
        vacLine.close();
        vacLine.stop();
    }

//    public static void main(String[] args) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
//        File file = new File("sound.wav");
//        try (AudioInputStream in = AudioSystem.getAudioInputStream(file)) {
//            AudioFormat format = in.getFormat();
//            System.out.println(format);
//            SourceDataLine auline = vacLine;
//            auline.open(format);
//            auline.start();
//            byte[] buffer = new byte[64];
//            int len;
//            while ((len = in.read(buffer)) > 0) {
//                auline.write(buffer, 0, len);
//            }
//            auline.stop();
//            auline.close();
//        }
//    }

}
