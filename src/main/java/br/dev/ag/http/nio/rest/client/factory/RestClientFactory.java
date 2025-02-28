package br.dev.ag.http.nio.rest.client.factory;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;

import java.time.Duration;

public abstract class RestClientFactory {

    /**
     * Returns a RestClient instance
     *
     * @param timeoutInSeconds timeout in seconds for connection and read
     * @return a RestClient instance
     */
    public static RestClient create(int timeoutInSeconds) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

        requestFactory.setConnectTimeout((int) Duration.ofSeconds(timeoutInSeconds).toMillis());
        requestFactory.setReadTimeout((int) Duration.ofSeconds(timeoutInSeconds).toMillis());

        return RestClient.builder()
                .requestFactory(requestFactory)
                .messageConverters(converters -> converters.add(new MappingJackson2HttpMessageConverter()))
                .build();
    }

}
