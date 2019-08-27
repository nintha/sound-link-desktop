package top.nintha.soundlink;

import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Application {
    public static final Vertx VERTX = buildVertx();

    public static void main(String[] args) {
        log.info("=== Sound Link ===");
        VERTX.deployVerticle(new SpeakerServerVerticle(), ar->{
            if(ar.failed()){
                log.error("Failed to deploy SpeakerServerVerticle", ar.cause());
            }
        });
    }

    private static Vertx buildVertx() {
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
        System.setProperty("vertx.disableDnsResolver", "true");
        Vertx vertx = Vertx.vertx();
        vertx.exceptionHandler(t -> log.error("[VERTX] unhandled error", t));
        return vertx;
    }
}
