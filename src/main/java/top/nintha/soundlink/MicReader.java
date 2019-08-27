package top.nintha.soundlink;

import lombok.extern.slf4j.Slf4j;

import javax.sound.sampled.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

@Slf4j
public class MicReader implements Closeable {
    private static final AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
    private TargetDataLine dataLine;
    private int bufferSize = format.getFrameSize() * 16;
    private Thread thread;

    public synchronized void open(OutputStream outputStream) throws LineUnavailableException {
        this.open(b -> {
            try {
                outputStream.write(b);
            } catch (IOException e) {
                log.error("[MicReader] outputStream.write error", e);
            }
        });
    }

    public synchronized void open(Consumer<byte[]> consumer) throws LineUnavailableException {
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        dataLine = (TargetDataLine) AudioSystem.getLine(info);
        dataLine.open(format);
        dataLine.start();

        thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    if (dataLine.available() < bufferSize) {
                        Thread.sleep(1);
                        continue;
                    }
                    byte[] buf = new byte[bufferSize];
                    int read = dataLine.read(buf, 0, bufferSize);
                    consumer.accept(buf);
                } catch (Exception e) {
                    log.error("[MicReader] error", e);
                    Thread.currentThread().interrupt();
                }
            }
            log.info("[MicReader] end thread, {}", Thread.currentThread().getName());
        }, "MicReader Async Thread");
        thread.start();
    }


    @Override
    public synchronized void close() throws IOException {
        if (dataLine != null) {
            dataLine.stop();
            dataLine.close();
        }
        if (thread != null) {
            thread.interrupt();
        }
        log.info("[MicReader] closed");
    }

    public static void pcm2wav(InputStream inputStream, int length, OutputStream outputStream) throws IOException {
        AudioInputStream ais = new AudioInputStream(inputStream, format, length);
        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, outputStream);
        log.info("pcm2wav, length={}", length);
    }

    public static void main(String[] args) throws Exception {
        log.warn("[MicReader] start...");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (MicReader micReader = new MicReader()) {
            micReader.open(baos);
            Thread.sleep(10_000);
        }

        OutputStream fileOutputStream = Files.newOutputStream(Path.of("out.wav"));
        pcm2wav(new ByteArrayInputStream(baos.toByteArray()), baos.size(), fileOutputStream);
        log.warn("[MicReader] done.");
    }

}
