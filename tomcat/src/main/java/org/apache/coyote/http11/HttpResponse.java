package org.apache.coyote.http11;

import org.apache.coyote.http11.enums.HttpStatus;

import java.util.Map;
import java.util.Objects;

public record HttpResponse(
        HttpStatus httpStatus,
        Map<String, String> headers,
        String responseBody
) {
    public HttpResponse {
        Objects.requireNonNull(httpStatus, "httpStatus는 null일 수 없습니다.");
        Objects.requireNonNull(headers, "headers는 null일 수 없습니다.");
        Objects.requireNonNull(responseBody, "responseBody는 null일 수 없습니다.");

        headers = Map.copyOf(headers);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private HttpStatus httpStatus;
        private Map<String, String> headers = Map.of();
        private String responseBody = "";

        public Builder httpStatus(final HttpStatus httpStatus) {
            this.httpStatus = httpStatus;
            return this;
        }

        public Builder headers(final Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder responseBody(final String responseBody) {
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