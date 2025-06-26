package org.esgi.project;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.esgi.project.streaming.StreamProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Properties;

@SpringBootApplication
public class Main {

    private static final String applicationName = "stream-processing";

    public static void main(String[] args) {


        // Pour garder l'application active
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

        // TODO: write and instantiate some HTTP server with which you're comfortable with
		SpringApplication.run(Main.class, args);
    }

    public static Properties buildProperties() {
        Properties properties = new Properties();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationName);
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        properties.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000);
        return properties;
    }
}
