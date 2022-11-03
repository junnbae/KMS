package hello.kms.logTest;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class LogTest {
    @Test
    void logTest() {
        log.trace("trace test");
        log.debug("debug test");
        log.info("info test");
        log.warn("warn test");
        log.error("error test");
    }
}
