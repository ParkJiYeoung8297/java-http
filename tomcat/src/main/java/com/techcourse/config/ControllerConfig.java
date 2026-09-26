package com.techcourse.config;


import org.apache.coyote.http11.RequestMapping;

import java.util.Map;

public final class ControllerConfig {

    public RequestMapping requestMapping() {
        return new RequestMapping(Map.of(
        ));
    }
}