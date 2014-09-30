package com.chat14.helpers.model;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MessageTypeEnum {
    ack,control,nack;

    @JsonCreator
    public static MessageTypeEnum forValue(String value) {
        return StringUtils.isEmpty(value) ? null : MessageTypeEnum.valueOf(value);
    }

    @JsonValue
    final String value() {
        return this.name();
    }

}
