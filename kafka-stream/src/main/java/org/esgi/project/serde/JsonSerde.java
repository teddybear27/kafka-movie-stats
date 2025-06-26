package org.esgi.project.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serdes.WrapperSerde;

public class JsonSerde<T> extends WrapperSerde<T> {
    public JsonSerde(Class<T> clazz) {
        super(
                (Serializer<T>) (topic, data) -> {
                    try { return new ObjectMapper().writeValueAsBytes(data); }
                    catch (Exception e) { return null; }
                },
                (Deserializer<T>) (topic, data) -> {
                    try { return new ObjectMapper().readValue(data, clazz); }
                    catch (Exception e) { return null; }
                }
        );
    }
}
