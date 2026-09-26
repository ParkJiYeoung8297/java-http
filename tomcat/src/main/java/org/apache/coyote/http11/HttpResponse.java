package org.apache.coyote.http11;

import org.apache.coyote.http11.enums.HttpStatus;

import java.util.*;

public final class HttpResponse {

    private HttpStatus httpStatus;
    private final Map<String, String> headers;
    private byte[] responseBody;

    public HttpResponse(
            final HttpStatus httpStatus,
            final Map<String, String> headers,
            final byte[] responseBody
    ) {
        this.httpStatus = Objects.requireNonNull(
                httpStatus,
                "httpStatus는 null일 수 없습니다."
        );
        this.headers = new LinkedHashMap<>(
                Objects.requireNonNull(headers, "headers는 null일 수 없습니다.")
        );
        this.responseBody = Objects.requireNonNull(
                responseBody,
                "responseBody는 null일 수 없습니다."
        );
    }

    public HttpStatus httpStatus() {
        return httpStatus;
    }

    public Map<String, String> headers() {
        return Collections.unmodifiableMap(headers);
    }

    public byte[] responseBody() {
        return responseBody;
    }

    public void setHttpStatus(final HttpStatus httpStatus) {
        this.httpStatus = Objects.requireNonNull(
                httpStatus,
                "httpStatus는 null일 수 없습니다."
        );
    }

    public void addHeader(final String name, final String value) {
        headers.put(
                Objects.requireNonNull(name, "header name은 null일 수 없습니다."),
                Objects.requireNonNull(value, "header value는 null일 수 없습니다.")
        );
    }

    public void setResponseBody(final byte[] responseBody) {
        this.responseBody = Objects.requireNonNull(
                responseBody,
                "responseBody는 null일 수 없습니다."
        );
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private HttpStatus httpStatus;
        private Map<String, String> headers = new LinkedHashMap<>();
        private byte[] responseBody = new byte[0];

        public Builder httpStatus(final HttpStatus httpStatus) {
            this.httpStatus = httpStatus;
            return this;
        }

        public Builder headers(final Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder responseBody(final byte[] responseBody) {
            this.responseBody = responseBody;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(
                    httpStatus,
                    headers,
                    responseBody
            );
        }
    }
}