package se.jsquad.util;

import nl.altindag.log.LogCaptor;
import org.springframework.stereotype.Component;

@Component
public class LogCaptorUtil {
    public void resetLogCaptor(final LogCaptor logCaptor) {
        logCaptor.clearLogs();
        logCaptor.resetLogLevel();
        logCaptor.disableLogs();
    }
}
