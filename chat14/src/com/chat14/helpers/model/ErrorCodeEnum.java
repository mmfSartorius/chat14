package com.chat14.helpers.model;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ErrorCodeEnum {
    BAD_ACK, BAD_REGISTRATION, CONNECTION_DRAINING, DEVICE_UNREGISTERED, INTERNAL_SERVER_ERROR, INVALID_JSON, QUOTA_EXCEEDED, SERVICE_UNAVAILABLE;

    @JsonCreator
    public static ErrorCodeEnum forValue(String value) {
        return StringUtils.isEmpty(value) ? null : ErrorCodeEnum.valueOf(value);
    }

    @JsonValue
    final String value() {
        return this.name();
    }

}
