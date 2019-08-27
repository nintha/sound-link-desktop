package top.nintha.soundlink;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SpeakerServerVerticle extends AbstractVerticle {
//    public static final String EB_SEND_AUDIO = "send_audio_data";

    private static final byte MODE_SPEAKER = 0x00;
    private static final byte MODE_MIC = 0x01;

    private NetSocket singleSocket = null;
    private SocketContext ctx;

    @Override
    public void start() throws Exception {
        final MicReader micReader = new MicReader();
        final AvcWriter avcWriter = new AvcWriter();

        NetServer netServer = vertx.createNetServer();
        netServer.connectHandler(netSocket -> {
            if (this.singleSocket != null) {
                this.singleSocket.close();
            }
            this.singleSocket = netSocket;

            ctx = new SocketContext();
            ctx.socket = netSocket;
            ctx.avcWriter = avcWriter;
            netSocket.handler(buffer -> {
                int pos = 0;
                if (ctx.mode == null) {
                    pos += 1;
                    ctx.mode = buffer.getByte(0);
                    log.info("{}, mode={}", netSocket.remoteAddress(), ctx.mode);
                }
                handleData(ctx, buffer, pos);
            });
            log.info("new connection > {}", netSocket.remoteAddress());
            netSocket.closeHandler(v -> {
                log.info("disconnected > {}", netSocket.remoteAddress());
            });
        });
        netServer.exceptionHandler(t -> log.error("SpeakerServerVerticle error", t));
        netServer.listen(9000);

        micReader.open(bytes -> {
            if (singleSocket != null) {
                try {
                    singleSocket.write(Buffer.buffer(bytes));
                } catch (Exception e) {
                    log.error("[SpeakerServerVerticle] write to {} error", singleSocket.remoteAddress(), e);
                }
            }
        });
    }

    private void handleData(SocketContext ctx, Buffer buffer, int pos) {
        if (ctx.mode == MODE_SPEAKER) {
            // TODO
            log.warn("MODE_SPEAKER not receive data.");
        } else if (ctx.mode == MODE_MIC) {
            byte[] bytes = buffer.getBytes(pos, buffer.length());
            ctx.avcWriter.write(bytes);
        } else {
            log.warn(" unsupported mode={}", ctx.mode);
        }
    }

    @Data
    private class SocketContext {
        private Byte mode;
        private AvcWriter avcWriter;
        private NetSocket socket;
    }

}

