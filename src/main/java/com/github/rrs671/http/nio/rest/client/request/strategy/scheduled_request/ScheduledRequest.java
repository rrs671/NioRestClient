package com.github.rrs671.http.nio.rest.client.request.strategy.scheduled_request;

import com.github.rrs671.http.nio.rest.client.enums.VerbsEnum;
import com.github.rrs671.http.nio.rest.client.request.strategy.scheduled_request.strategies.*;

import java.util.EnumMap;
import java.util.Map;

public abstract class ScheduledRequest {

    private static final Map<VerbsEnum, ScheduledRequestStrategy> strategies = new EnumMap<>(VerbsEnum.class);

    static {
        strategies.put(VerbsEnum.GET, new GetScheduledRequestStrategy());
        strategies.put(VerbsEnum.POST, new PostScheduledRequestStrategy());
        strategies.put(VerbsEnum.PUT, new PutScheduledRequestStrategy());
        strategies.put(VerbsEnum.PATCH, new PatchScheduledRequestStrategy());
        strategies.put(VerbsEnum.DELETE, new DeleteScheduledRequestStrategy());
    }

    private ScheduledRequest() {}

    public static ScheduledRequestStrategy getVerbExecutor(VerbsEnum verb) {
        return strategies.get(verb);
    }


}
