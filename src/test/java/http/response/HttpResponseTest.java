package http.response;

import static org.assertj.core.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import http.HttpStatus;

public class HttpResponseTest {
    @Test
    @DisplayName("HttpResponse end 테스트")
    void end() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HttpResponse httpResponse = HttpResponse.from(outputStream, "HTTP/1.1");
        httpResponse.status(HttpStatus.OK).end(null);
        assertThat(outputStream.toString()).isEqualTo("HTTP/1.1 200 OK\r\n\r\n");
    }

    @Test
    @DisplayName("HttpResponse 본문이 존재하는 경우 테스트")
    void end_body() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HttpResponse httpResponse = HttpResponse.from(outputStream, "HTTP/1.1");
        String expected = "HTTP/1.1 200 OK\r\n"
            + "Content-Length: 12\r\n"
            + "\r\n"
            + "Hello World!";
        httpResponse.status(HttpStatus.OK).end("Hello World!".getBytes());
        assertThat(outputStream.toString()).isEqualTo(expected);
    }

    @Test
    @DisplayName("HttpResponse sendRedirect 테스트")
    void sendRedirect() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HttpResponse httpResponse = HttpResponse.from(outputStream, "HTTP/1.1");
        String expected = "HTTP/1.1 302 Found\r\n"
            + "Location: /index.html\r\n"
            + "\r\n";
        httpResponse.sendRedirect("/index.html");
        assertThat(outputStream.toString()).isEqualTo(expected);
    }
}
