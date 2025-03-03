package com.github.rrs671.http.nio.rest.client.request.strategy.request;

import com.github.rrs671.http.nio.rest.client.enums.VerbsEnum;
import com.github.rrs671.http.nio.rest.client.request.strategy.request.strategies.*;

import java.util.EnumMap;
import java.util.Map;

public abstract class Request {

    private static final Map<VerbsEnum, RequestStrategy> strategies = new EnumMap<>(VerbsEnum.class);

    static {
        strategies.put(VerbsEnum.GET, new GetRequestStrategy());
        strategies.put(VerbsEnum.POST, new PostRequestStrategy());
        strategies.put(VerbsEnum.PUT, new PutRequestStrategy());
        strategies.put(VerbsEnum.PATCH, new PatchRequestStrategy());
        strategies.put(VerbsEnum.DELETE, new DeleteRequestStrategy());
    }

    private Request() {}

    public static RequestStrategy getVerbStrategy(VerbsEnum verb) {
        return strategies.get(verb);
    }


}
