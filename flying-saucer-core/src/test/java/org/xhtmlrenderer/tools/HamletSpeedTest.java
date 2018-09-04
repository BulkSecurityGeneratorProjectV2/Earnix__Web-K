package org.xhtmlrenderer.tools;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.xhtmlrenderer.simple.Graphics2DRenderer;
import org.xhtmlrenderer.util.XRLog;

import java.io.File;
import java.util.Date;

@Slf4j
public class HamletSpeedTest {

    @Test
    public  void test() throws Exception {
        XRLog.setLoggingEnabled(true);
        long total = 0;
        for (int i = 0; i < 10; i++) {
            Date start = new Date();
            Graphics2DRenderer.renderToImage(
                    new File("tests/profiling/xhtml/hamlet.xhtml").toURL().toExternalForm(),
                    700, 
                    700
            );
            Date end = new Date();
            long diff = (end.getTime() - start.getTime());
            log.info("ms = " + diff);
            total += diff;
        }
        long avg = total / 10;
        log.info("Average : " + avg);
        assert avg < 1000;
    }
    
}
