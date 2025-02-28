package com.github.rrs671.http.nio.rest.client.factory;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;

import java.time.Duration;

public abstract class RestClientFactory {

    /**
     * Returns a RestClient instance
     *
     * @param connectionTimeOutInSeconds connection timeout in seconds
     * @param readTimeOutInSeconds read timeout in seconds
     * @return a RestClient instance
     */
    public static RestClient create(int connectionTimeOutInSeconds, int readTimeOutInSeconds) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

        requestFactory.setConnectTimeout((int) Duration.ofSeconds(connectionTimeOutInSeconds).toMillis());
        requestFactory.setReadTimeout((int) Duration.ofSeconds(readTimeOutInSeconds).toMillis());

        return RestClient.builder()
                .requestFactory(requestFactory)
                .messageConverters(converters -> converters.add(new MappingJackson2HttpMessageConverter()))
                .build();
    }

}
