package top.nintha.soundlink;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;

public class AudioPlayer {
    public static void main(String[] args) throws Exception {
//        File file = new File("sound.wav");
//        try (AudioInputStream in = AudioSystem.getAudioInputStream(file);) {
//            AudioFormat format = in.getFormat();
//            System.out.println(format);
//            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
//            SourceDataLine auline = (SourceDataLine) AudioSystem.getLine(info);
//            auline.open(format);
//            auline.start();
//            byte[] buffer = new byte[512];
//            int len;
//            while ((len = in.read(buffer)) > 0) {
//
//                auline.write(buffer, 0, len);
//            }
//        }

        Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
        for (Mixer.Info info : mixerInfo) {
            if (info.getName().contains("CABLE") && !info.getName().contains("Port")) {
                System.out.println(info.toString());
                Mixer mixer = AudioSystem.getMixer(info);
                System.out.println("\tsourceLine size: " + mixer.getSourceLineInfo().length);
                for (Line.Info lineInfo : mixer.getSourceLineInfo()) {
                    System.out.println("\tsourceLine: " + lineInfo.getLineClass());
                }

            }
        }
    }


}
