package utils;

import static org.assertj.core.api.Assertions.*;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IOUtilsTest {
    private static final Logger logger = LoggerFactory.getLogger(IOUtilsTest.class);

    @Test
    public void readData() throws Exception {
        String data = "abcd123";
        StringReader sr = new StringReader(data);
        BufferedReader br = new BufferedReader(sr);

        logger.debug("parse body : {}", IOUtils.readData(br, data.length()));
    }

    @Test
    @DisplayName("readHeaders 테스트")
    public void readHeaders() throws Exception {
        String data = "Accept: text/html\r\nContent-Length: 100\r\n";
        StringReader sr = new StringReader(data);
        BufferedReader br = new BufferedReader(sr);
        List<String> headers = IOUtils.readHeaders(br);
        logger.debug("parse headers : {}", headers);
        assertThat(headers.size()).isEqualTo(2);
    }
}
