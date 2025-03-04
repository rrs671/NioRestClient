package com.github.rrs671.http.nio.rest.utils;

/**
 * An abstraction for http parameters.
 * Inform a connection and read timeout value
 * Max concurrent requests and delay time (delay time is always the time to wait after the requests is done)
 *
 * @since 1.0.0
 */
public class ClientParams {

    private int connTimeout;
    private int readTimeout;
    private int maxConcurrentRequests;
    private long delayInMilliSeconds;

    private ClientParams() {}

    public int getConnTimeout() {
        return connTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public int getMaxConcurrentRequests() {
        return maxConcurrentRequests;
    }

    public long getDelayInMilliSeconds() {
        return delayInMilliSeconds;
    }

    public static NioRestClientParamsBuilder builder() {
        return new NioRestClientParamsBuilder();
    }

    public static class NioRestClientParamsBuilder {
        private NioRestClientParamsBuilder(){}

        private int connTimeout;
        private int readTimeout;
        private int maxConcurrentRequests;
        private long delayInMilliSeconds;

        public NioRestClientParamsBuilder addConnectionTimeout(int connTimeout) {
            this.connTimeout = connTimeout;
            return this;
        }

        public NioRestClientParamsBuilder addReadTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public NioRestClientParamsBuilder addMaxConcurrentRequest(int maxConcurrentRequests) {
            this.maxConcurrentRequests = maxConcurrentRequests;
            return this;
        }

        public NioRestClientParamsBuilder addRequestDelay(long delayInMilliSeconds) {
            this.delayInMilliSeconds = delayInMilliSeconds;
            return this;
        }

        public ClientParams build() {
            ClientParams clientParams = new ClientParams();

            clientParams.connTimeout = this.connTimeout;
            clientParams.readTimeout = this.readTimeout;
            clientParams.maxConcurrentRequests = this.maxConcurrentRequests;
            clientParams.delayInMilliSeconds = this.delayInMilliSeconds;

            if (this.delayInMilliSeconds > 0L && this.maxConcurrentRequests == 0) {
               throw new IllegalArgumentException("When delay time is > 0, maxConcurrentRequests must be > 0");
            }

            return clientParams;
        }
    }

}