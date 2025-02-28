package com.github.rrs671.http.nio.rest.utils;

/**
 * An abstraction for http timeout parameters.
 * This class is used to build a request param.
 * Inform a connection and read timeout value
 * queryParams and headers.
 *
 * @since 1.0.0
 */
public class HttpTimeoutParams {

    private int connTimeout;
    private int readTimeout;

    private HttpTimeoutParams() {}

    public int getConnTimeout() {
        return connTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public static HttpTimeoutParamsBuilder builder() {
        return new HttpTimeoutParamsBuilder();
    }

    public static class HttpTimeoutParamsBuilder {
        private HttpTimeoutParamsBuilder(){}

        private int connTimeout;
        private int readTimeout;

        public HttpTimeoutParamsBuilder addConnectionTimeout(int connTimeout) {
            this.connTimeout = connTimeout;
            return this;
        }

        public HttpTimeoutParamsBuilder addReadTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public HttpTimeoutParams build() {
            HttpTimeoutParams httpTimeoutParams = new HttpTimeoutParams();

            httpTimeoutParams.connTimeout = this.connTimeout;
            httpTimeoutParams.readTimeout = this.readTimeout;

            return httpTimeoutParams;
        }
    }

}