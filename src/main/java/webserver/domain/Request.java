package webserver.domain;

import org.slf4j.Logger;
import webserver.view.NetworkInput;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

public class Request {
    private static final Logger LOG = getLogger(Request.class);
    private static final String KEY_VALUE_DELIMITER = ":";
    private static final String COOKIE = "cookie";
    private static final String EMPTY = "";
    private static final String CONTENT_LENGTH = "content-length";
    private static final String ZERO_LENGTH = "0";
    private static final String SPACE_DELIMITER = " ";
    private static final String SESSION_ID = "SESSION_ID";
    private static final int ZERO = 0;
    private static final int KEY_INDEX = ZERO;
    private static final int VALUE_INDEX = 1;
    private static final int METHOD_INDEX = ZERO;
    private static final int URL_INDEX = 1;
    private static final int PROTOCOL_INDEX = 2;

    private final HttpMethod httpMethod;
    private final HttpVersion protocol;
    private final Url url;
    private final Map<String, String> header;
    private final Cookies cookies;
    private final HttpSession session;
    private final String body;

    public Request(final NetworkInput networkInput) throws IOException, URISyntaxException {
        final String[] requestLine = networkInput.iterator().next().split(SPACE_DELIMITER);
        url = new Url(requestLine[URL_INDEX]);
        httpMethod = HttpMethod.valueOf(requestLine[METHOD_INDEX].toUpperCase());
        protocol = HttpVersion.of(requestLine[PROTOCOL_INDEX]);
        header = makeHeader(networkInput);
        cookies = new Cookies(header.getOrDefault(COOKIE, EMPTY));
        session = HttpSession.get(cookies.getCookieValue(SESSION_ID));
        cookies.setCookieValue(SESSION_ID, session.getId());
        body = readBody(networkInput);
        printLog();
    }

    private void printLog() {
        LOG.debug("Request - protocol: {}, method: {}, path: {}, parameter: {}\nbody: \n{}",
                protocol,
                httpMethod,
                url.getPath(),
                url.getQueries().toString(),
                body);
    }

    private String readBody(final NetworkInput networkInput) throws IOException {
        final int contentLength = Integer.parseInt(header.getOrDefault(CONTENT_LENGTH, ZERO_LENGTH));
        return (contentLength > ZERO) ? networkInput.readBody(contentLength) : EMPTY;
    }

    private Map<String, String> makeHeader(final NetworkInput networkInput) {
        final Map<String, String> fields = new HashMap<>();
        for (final String input : networkInput) {
            fields.put(makeKey(input), makeValue(input));
        }
        return Collections.unmodifiableMap(fields);
    }

    private String splitAndTrim(final String rawField, final int index) {
        return rawField.split(KEY_VALUE_DELIMITER)[index].trim();
    }

    private String makeKey(final String rawField) {
        return splitAndTrim(rawField, KEY_INDEX).toLowerCase();
    }

    private String makeValue(final String rawField) {
        return splitAndTrim(rawField, VALUE_INDEX);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return url.getPath();
    }

    public Cookies getCookies() {
        return cookies;
    }

    public HttpVersion getProtocol() {
        return protocol;
    }

    public HttpSession getSession() {
        return session;
    }

    public QueryParameter getQueryParameters() {
        final QueryParameter queries = this.url.getQueries();
        if (httpMethod == HttpMethod.POST) {
            queries.putByRawQueries(this.body);
        }
        return queries;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public String getHeaderValue(final String key) {
        return header.getOrDefault(key, EMPTY);
    }
}
