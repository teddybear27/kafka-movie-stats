package org.esgi.project.java.tools;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.esgi.project.java.model.ViewStats;

// Cette méthode peut être appelée depuis votre Main après stream.start()
public class StorePrinter {
    public static void printViewStats(KafkaStreams streams) {
        ReadOnlyKeyValueStore<String, ViewStats> store = streams.store(
                "movieStats", // Le nom du state store (doit correspondre à Materialized.as(...) OU au nom auto-généré)
                QueryableStoreTypes.keyValueStore()
        );
        store.all().forEachRemaining(entry ->
                System.out.println("Film id=" + entry.key + " stats=" + entry.value)
        );
    }
}
