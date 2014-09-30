package com.chat14.helpers.model;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompressedUpstreamMessage {
    public static enum ControlTypeEnum {
        CONNECTION_DRAINING;
        @JsonCreator
        public static ControlTypeEnum forValue(String value) {
            return StringUtils.isEmpty(value) ? null : ControlTypeEnum.valueOf(value);
        }

        @JsonValue
        final String value() {
            return this.name();
        }
    }

    @JsonProperty("category")
    private String category;

    @JsonProperty("control_type")
    private ControlTypeEnum control_type;

    @JsonProperty("data")
    private CompressedData data;

    @JsonProperty("error")
    private MessageTypeEnum error;

    @JsonProperty("error_description")
    private String error_description;

    @JsonProperty("from")
    private String from;

    @JsonProperty("message_id")
    private String message_id;

    @JsonProperty("message_type")
    private MessageTypeEnum message_type;

    public String getCategory() {
        return category;
    }

    public ControlTypeEnum getControl_type() {
        return control_type;
    }

    public CompressedData getData() {
        return data;
    }

    public MessageTypeEnum getError() {
        return error;
    }

    public String getError_description() {
        return error_description;
    }

    public String getFrom() {
        return from;
    }

    public String getMessage_id() {
        return message_id;
    }

    public MessageTypeEnum getMessage_type() {
        return message_type;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setControl_type(ControlTypeEnum control_type) {
        this.control_type = control_type;
    }

    public void setData(CompressedData data) {
        this.data = data;
    }

    public void setError(MessageTypeEnum error) {
        this.error = error;
    }

    public void setError_description(String error_description) {
        this.error_description = error_description;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public void setMessage_type(MessageTypeEnum message_type) {
        this.message_type = message_type;
    }

    @Override
    public String toString() {
        return "CompressedUpstreamMessage [" + (from != null ? "from=" + from + ", " : "") + (message_id != null ? "message_id=" + message_id + ", " : "")
                + (error != null ? "error=" + error + ", " : "") + (message_type != null ? "message_type=" + message_type + ", " : "")
                + (error_description != null ? "error_description=" + error_description + ", " : "")
                + (control_type != null ? "control_type=" + control_type + ", " : "") + (category != null ? "category=" + category + ", " : "")
                + (data != null ? "data=" + data : "") + "]";
    }
}
