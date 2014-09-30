package com.chat14.helpers.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompressedData implements Comparable<CompressedData> {

    @JsonProperty("t")
    private Integer commandType;

    @JsonProperty("c")
    private Boolean compressed;

    public Boolean getCompressed() {
        return compressed;
    }

    public void setCompressed(Boolean compressed) {
        this.compressed = compressed;
    }

    @JsonProperty("p")
    private String compressedPayload;

    @JsonProperty("msgId")
    private String messageId;

    @JsonProperty("sn")
    private Integer sequenceNumber;

    @JsonProperty("tn")
    private Integer totalNumber;

    @JsonProperty("s")
    private Integer decompressedSize;

    public Integer getCommandType() {
        return commandType;
    }

    public String getCompressedPayload() {
        return compressedPayload;
    }

    public String getMessageId() {
        return messageId;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public Integer getTotalNumber() {
        return totalNumber;
    }

    public Integer getDecompressedSize() {
        return decompressedSize;
    }

    public void setCommandType(Integer commandType) {
        this.commandType = commandType;
    }

    public void setCompressedPayload(String compressedPayload) {
        this.compressedPayload = compressedPayload;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public void setTotalNumber(Integer totalNumber) {
        this.totalNumber = totalNumber;
    }

    public void setDecompressedSize(Integer decompressedSize) {
        this.decompressedSize = decompressedSize;
    }

    @Override
    public String toString() {
        return "CompressedData [" + (commandType != null ? "commandType=" + commandType + ", " : "")
                + (compressed != null ? "compressed=" + compressed + ", " : "")
                + (compressedPayload != null ? "compressedPayload=" + compressedPayload + ", " : "")
                + (messageId != null ? "messageId=" + messageId + ", " : "") + (sequenceNumber != null ? "sequenceNumber=" + sequenceNumber + ", " : "")
                + (totalNumber != null ? "totalNumber=" + totalNumber + ", " : "") + (decompressedSize != null ? "decompressedSize=" + decompressedSize : "")
                + "]";
    }

    @Override
    public int compareTo(CompressedData o) {
        return this.sequenceNumber - o.sequenceNumber;
    }
}
