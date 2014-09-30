package com.chat14.helpers;

import java.io.IOException;
import java.util.Collection;

import org.apache.log4j.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class JsonUtils {

    public static final String EMPTY_JSON_OBJECT = "{}";
    public static final String EMPTY_JSON_ARRAY = "[]";

    private static final Logger LOGGER = Logger.getLogger(JsonUtils.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private JsonUtils() {
        // NOPMD
    }

    /**
     * Returns with the JSON formatted string representation of the given object. In case of creating JSON fails, it returns with EMPTY_JSON_OBJECT.
     */
    public static String toJsonSilent(final Object object) {
        return toJson(object, EMPTY_JSON_OBJECT);
    }

    /**
     * Returns with the JSON formatted string representation of the given collection. In case of creating JSON fails, it returns with EMPTY_JSON_ARRAY.
     */
    public static <T> String toJsonSilent(final Collection<T> collection) {
        return toJson(collection, EMPTY_JSON_ARRAY);
    }

    /**
     * Returns with the JSON formatted string representation of the given object.
     * 
     * @param object
     *            the object which is to be converted to JSON string
     * @param defaultValue
     *            if creating JSON string fails, it will be the value the function returns with.
     */
    public static String toJson(final Object object, final String defaultValue) {
        String ret;
        try {
            ret = toJson(object);
        } catch (IOException ex) {
            LOGGER.debug(String.format("Could not convert object to JSON format: %s. Using value of %s", object, defaultValue), ex);
            ret = defaultValue;
        }
        return ret;
    }

    /**
     * Returns with the JSON formatted string representation of the given object.
     * 
     * @throws IOException
     *             if the JSON creation fails
     */
    public static String toJson(final Object object) throws IOException {
        return OBJECT_MAPPER.writeValueAsString(object);
    }

    public static <T> T fromJson(final String source, final Class<T> targetClass) throws IOException {
        return OBJECT_MAPPER.readValue(source, targetClass);
    }
}