package org.esgi.project.streaming;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.esgi.project.java.model.*;
import org.esgi.project.java.serde.JsonSerde;

import java.time.Duration;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.common.utils.Bytes;

public class StreamProcessing {

    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        String storeViewStats = "movie-stats";
        String storeLikeStats = "like-stats";
        String storeViewStatsWindowed = "movie-stats-5-mn";
        // Serdes
        JsonSerde<ViewEvent> viewEventSerde = new JsonSerde<>(ViewEvent.class);
        JsonSerde<LikeEvent> likeEventSerde = new JsonSerde<>(LikeEvent.class);
        JsonSerde<ViewStats> viewStatsSerde = new JsonSerde<>(ViewStats.class);
        JsonSerde<LikeStats> likeStatsSerde = new JsonSerde<>(LikeStats.class);

        // Source + mapping
        KStream<String, ViewEvent> viewStream = builder.stream("views",
                Consumed.with(Serdes.String(), viewEventSerde));

        KStream<String, LikeEvent> likeStream = builder.stream("likes",
                Consumed.with(Serdes.String(), likeEventSerde));


        // Enregistrement des données
        KTable<String, ViewStats> movieStats = viewStream
                .groupBy(
                        (key, event) -> String.valueOf(event.id), // ou event.id + "|" + event.title
                        Grouped.with(Serdes.String(), viewEventSerde)
                )
                .aggregate(
                        ViewStats :: new, // initialiseur de l'agrégat
                        (key, event, stats) -> {
                            stats.id = Integer.parseInt(key);
                            stats.title = event.title;
                            stats.add(event.view_category);
                            return stats;
                        },
                        Materialized.<String, ViewStats, KeyValueStore<Bytes, byte[]>>as(storeViewStats)
                            .withKeySerde(Serdes.String())
                            .withValueSerde(viewStatsSerde)
                );

        KTable<String, LikeStats> moveLikeStats = likeStream
                .groupBy(
                        (key, event) -> String.valueOf(event.id),
                        Grouped.with(Serdes.String(), likeEventSerde)
                )
                .aggregate(
                        LikeStats :: new,
                        (key, event, stats) -> {
                            stats.id = Integer.parseInt(key);
                            stats.add(event.score);
                            return stats;
                        },
                        Materialized.<String, LikeStats, KeyValueStore<Bytes, byte[]>>as(storeLikeStats)
                            .withKeySerde(Serdes.String())
                            .withValueSerde(likeStatsSerde)
                );


        KTable<Windowed<String>, ViewStats> movieStats5min = viewStream
                .groupBy(
                        (key, event) -> String.valueOf(event.id),
                        Grouped.with(Serdes.String(), viewEventSerde)
                )
                .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(5)))
                .aggregate(
                        ViewStats::new,
                        (key, event, stats) -> {
                            stats.id = Integer.parseInt(key);
                            stats.add(event.view_category);
                            return stats;
                        },
                        Materialized.<String, ViewStats, KeyValueStore<Bytes, byte[]>>as(storeViewStatsWindowed)
                            .withKeySerde(Serdes.String())
                            .withValueSerde(viewStatsSerde)
                );


        return builder.build();
    }
}
