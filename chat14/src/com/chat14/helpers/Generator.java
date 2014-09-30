package com.chat14.helpers;

import java.util.UUID;

public class Generator {
    private long senderId;
    private static volatile Generator instance;

    public static Generator getInstance() {
        Generator localInstance = instance;
        if (localInstance == null) {
            synchronized (Generator.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new Generator();
                }
            }
        }
        return localInstance;
    }

    public String getRandomUUID(){
        return String.format("%d%d%s", senderId, System.currentTimeMillis(), UUID.randomUUID());
    }
    
    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }
}
