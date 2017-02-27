package fido.api;

import com.samsung.sds.fido.uaf.server.sdk.http.HttpRequest;

public class ApiRequest implements HttpRequest {

    private final String contentType;

    private final String accept;

    private final String userAgent;

    private final String body;

    public ApiRequest(String contentType, String accept, String userAgent, String body) {
        this.contentType = contentType;
        this.accept = accept;
        this.userAgent = userAgent;
        this.body = body;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public String getAccept() {
        return accept;
    }

    @Override
    public String getUserAgent() {
        return userAgent;
    }

    @Override
    public String getBody() {
        return body;
    }
}

