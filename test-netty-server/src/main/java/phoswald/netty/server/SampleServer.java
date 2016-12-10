package phoswald.netty.server;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import phoswald.http.server.MyCookie;
import phoswald.http.server.MyHeader;
import phoswald.http.server.MyParam;
import phoswald.http.server.MyRequest;
import phoswald.http.server.MyResponse;
import phoswald.http.server.MyServer;

public final class SampleServer {

    static final boolean SSL = System.getProperty("ssl") != null;
    static final int PORT = Integer.parseInt(System.getProperty("port", SSL ? "8443" : "8080"));

    public static void main(String[] args) throws Exception {
        new SampleServer().run();
    }

    private void run() throws Exception {

        System.err.println("Open your web browser and navigate to " +
                (SSL? "https" : "http") + "://127.0.0.1:" + PORT + '/');

        try(MyServer server = new MyServer()) {
            server.
                port(PORT).
                secure(SSL).
                handler(this::handle).
                start();
        }
    }

    private MyResponse handle(MyRequest request, MyResponse response) {
        response.append("WELCÖME TO THE WILD WILD WEB SERVER\r\n");
        response.append("===================================\r\n");

        response.append("PROTOCOL: ").append(request.protocol()).append("\r\n");
        response.append("HOSTNAME: ").append(request.host()).append("\r\n");
        response.append("PATH:     ").append(request.path()).append("\r\n\r\n");

        for (MyParam param: request.params()) {
            response.append("PARAM ").append(param.name()).append("=").append(param.value()).append("\r\n");
        }

        for (MyHeader header: request.headers()) {
            response.append("HEADER ").append(header.name()).append(": ").append(header.value()).append("\r\n");
        }

        for(MyCookie cookie : request.cookies()) {
            response.append("COOKIE ").append(cookie.name()).append("=").append(cookie.value()).append("\r\n");
        }

        response.contentType("text/plain", StandardCharsets.UTF_8);

        if(request.cookies().isEmpty()) {
            // set some cookies if none are present
            response.cookie("my-server-session", "blubber");
            response.cookie("my-server-other", "foobar");
        } else {
            // send back cookies sent by client
            for(MyCookie cookie : request.cookies()) {
                response.cookie(cookie.name(), cookie.value());
            }
        }

        if(request.contentLength() > 0) {
            Charset charset = request.charset().orElse(StandardCharsets.US_ASCII);
            String content = request.content(charset);
            response.append("CONTENT: ");
            response.append(content);
            response.append("\r\n");
            response.append("END OF CONTENT\r\n");
        }

        return response;
    }
}
