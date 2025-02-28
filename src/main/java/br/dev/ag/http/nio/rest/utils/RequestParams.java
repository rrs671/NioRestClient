package br.dev.ag.http.nio.rest.utils;
import java.util.*;
import java.util.stream.IntStream;

/**
 * An abstraction for request parameters.
 * This class is used to build a request param.
 * Here you must inform baseUrl and has another optional params like paths for path variables,
 * queryParams and headers.
 *
 * @since 0.0.1
 */
public class RequestParams {

    private String baseUrl;
    private List<String> paths;
    private Map<String, String> queryParams;
    private Map<String, String> headers;

    private RequestParams() {}

    public String getBaseUrl() {
        return baseUrl;
    }

    public List<String> getPaths() {
        return paths;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public static RequestParamsBuilder builder() {
        return new RequestParamsBuilder();
    }

    public static class RequestParamsBuilder {
        private RequestParamsBuilder(){}

        private String baseUrl;
        private List<String> paths;
        private Map<String, String> queryParams;
        private Map<String, String> headers;

        public RequestParamsBuilder addUrl(String url) {
            Objects.requireNonNull(url);

            this.baseUrl = url;
            return this;
        }

        public RequestParamsBuilder addPaths(String...paths) {
            Objects.requireNonNull(paths);

            if (Objects.isNull(this.paths)) {
                this.paths = new ArrayList<>();
            }

            List<String> temp = Arrays.stream(paths).toList();

            this.paths.addAll(temp);
            return this;
        }

        public RequestParamsBuilder addQueryParams(String ... querys){
            Objects.requireNonNull(querys);

            if (querys.length % 2 != 0) {
                throw new IllegalArgumentException("Invalid query param: " + Arrays.toString(querys));
            }

            Map<String, String> temp = IntStream.range(0, querys.length / 2)
                    .collect(HashMap::new,
                            (m, i) -> m.put(querys[i * 2], querys[i * 2 + 1]),
                            HashMap::putAll
                    );

            if (Objects.isNull(this.queryParams)) {
                this.queryParams = new HashMap<>();
            }

            this.queryParams.putAll(temp);

            return this;
        }

        public RequestParamsBuilder addHeaders(String ... headers){
            Objects.requireNonNull(headers);

            if (headers.length % 2 != 0) {
                throw new IllegalArgumentException("Invalid header: " + Arrays.toString(headers));
            }

            Map<String, String> temp = IntStream.range(0, headers.length / 2)
                    .collect(HashMap::new,
                            (m, i) -> m.put(headers[i * 2], headers[i * 2 + 1]),
                            HashMap::putAll
                    );

            if (Objects.isNull(this.headers)) {
                this.headers = new HashMap<>();
            }

            this.headers.putAll(temp);

            return this;
        }

        public RequestParams build() {
            RequestParams requestParams = new RequestParams();

            Objects.requireNonNull(this.baseUrl);

            requestParams.baseUrl = this.baseUrl;

            if (Objects.nonNull(this.paths)) {
                requestParams.paths = this.paths;
            }

            if (Objects.nonNull(this.queryParams)) {
                requestParams.queryParams = this.queryParams;
            }

            if (Objects.nonNull(this.headers)) {
                requestParams.headers = this.headers;
            }

            return requestParams;
        }
    }

}
