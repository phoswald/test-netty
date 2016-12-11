package phoswald.http.client;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import phoswald.http.Cookie;
import phoswald.http.Header;

public class Response {

    private final String status;
    private final String version;
    private final boolean chunked;
    private final List<Header> headers;
    private final List<Cookie> cookies;
    private final byte[] content;

    Response(String status, String version, boolean chunked,
            List<Header> headers,
            List<Cookie> cookies,
            ByteArrayOutputStream content) {
        this.status = status;
        this.version = version;
        this.chunked = chunked;
        this.headers = Collections.unmodifiableList(headers);
        this.cookies = Collections.unmodifiableList(cookies);
        this.content = content.toByteArray();
    }

    public String status() {
        return status;
    }

    public String version() {
        return version;
    }

    public boolean chunked() {
        return chunked;
    }

    public List<Header> headers() {
        return headers;
    }

    public Optional<String> header(String name) {
        return headers.stream().
                filter(h -> h.name().equalsIgnoreCase(name)).
                map(h -> h.value()).
                findFirst();
    }

    public Optional<Charset> charset() {
        return header("content-type").
                filter(s -> s.contains("charset=")).
                map(s -> s.substring(s.indexOf("charset=") + 8)).
                map(Charset::forName);
    }

    public List<Cookie> cookies() {
        return cookies;
    }

    public int contentLength() {
        return content.length;
    }

    public byte[] content() {
        return content;
    }

    public String content(Charset charset) {
        return new String(content, charset);
    }
}