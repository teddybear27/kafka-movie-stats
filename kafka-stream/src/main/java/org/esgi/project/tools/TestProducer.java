package org.esgi.project.tools;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.esgi.project.model.ViewEvent;

import java.util.Properties;

public class TestProducer {
    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        // Exemple d'événement ViewEvent
        ViewEvent event = new ViewEvent();
        event.id = 1;
        event.view_category = "full";
        // Ajoute d'autres champs si besoin

        ObjectMapper mapper = new ObjectMapper();
        String jsonValue = mapper.writeValueAsString(event);

        ProducerRecord<String, String> record = new ProducerRecord<>("views", String.valueOf(event.id), jsonValue);

        producer.send(record);

        producer.close();
        System.out.println("Message envoyé !");
    }
}
