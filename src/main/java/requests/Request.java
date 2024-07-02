package requests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.io.IOException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class Request {
    private HttpClient client = HttpClient.newHttpClient();
    private HttpRequest.Builder builder = HttpRequest.newBuilder();
    private String method;
    private String URL;
    private Map<String, String> headers;
    private Map<String, String> bodyParams;

    public Request(String URL, String method, Map<String, String> headers, Map<String, String> bodyParams) {
        method = method.toUpperCase();
        assert method.equalsIgnoreCase("POST") || method.equalsIgnoreCase("GET");
        this.URL = URL;
        this.method = "POST";
        this.headers = headers;
        if (method.equals("POST")) this.bodyParams = bodyParams;

        constructRequest();
    }

    public Request(String URL, String method, Map<String, String> headers) {
        this(URL, method, headers, null);
    }
    public Request(String URL, String method) {
        this(URL, method, null, null);
    }

    public void setProxy(String type, String proxy_ip, int proxy_port) {
        type = type.toLowerCase();
//        if (type!="http" && type!="socks4" && type!="socks5") {
//            throw new IllegalArgumentException("Invalid proxy type!");
//        }
        //Proxy.Type proxy_type = Proxy.Type.valueOf(type);
        ProxySelector proxySelector = new ProxySelector() {
            @Override
            public List<Proxy> select(URI uri) {
                return List.of(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy_ip, proxy_port)));
            }

            @Override
            public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
                System.err.println("Proxy connection failed: " + ioe.getMessage());
            }
        };

        client = HttpClient.newBuilder()
                .proxy(proxySelector)
                .build();
    }

    private void constructRequest() {
        builder = HttpRequest.newBuilder()
                .uri(URI.create(URL));
        if (headers!=null) {
            for (String key : headers.keySet()) {
                builder.header(key, headers.get(key));
            }
        }

        if (method.equals("POST") && bodyParams!=null) {
            String body = "";
            Iterator<String> it = bodyParams.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                body += key+"="+URLEncoder.encode(bodyParams.get(key));
                if (it.hasNext()) body+="&";
            }
            //System.out.println(body);
            //device_id=android-Q1X8HA6Oa3gSbhgU&guid=7b9ed8e7-6677-4aa7-baef-bbaa91e95700&enc_password=%23PWD_INSTAGRAM%3A0%3A0%3A32324234&login_attempt_count=0&username=xnce
            builder.POST(HttpRequest.BodyPublishers.ofString(body));
        }

    }

    public HttpResponse<String> send() {
        HttpRequest request = builder.build();
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }

        return response;
    }

    public static ObjectNode convertToJson(String responseBodyString) {
        try {
            JsonNode json = new ObjectMapper().readTree(responseBodyString);
            return (ObjectNode) json;
        } catch (JsonProcessingException | ClassCastException e) {
            return new ObjectMapper().createObjectNode();
        }
    }

    public void setTimeout(int timeoutSeconds) {
        builder.timeout(Duration.ofSeconds(timeoutSeconds));
    }

    public <T> void setHeader(String key, T value) {
        builder.setHeader(key, String.valueOf(value));
    }
}
