package org.esgi.project.api;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import java.util.List;

import org.esgi.project.model.StreamProcessing;


@RestController
@RequestMapping("/")
public class MovieController {
    KafkaStreams streams;

    public MovieController() {
        StreamProcessing streamProcessing = new StreamProcessing();

        streams = new KafkaStreams(streamProcessing.buildbestology(), buildProperties());
        streams.setUncaughtExceptionHandler(Exception -> {
            streams.close();
            streams.cleanUp();
            return null;
        });
        streams.start();
    }

	private List<LikeStats> scoreExtrema(String kind, int number) {
        ReadOnlyKeyValueStore<String, LikeStats> store = streams.store(
            StoreQueryParameters.fromNameAndType(
                StreamProcessing.storeLikeStats
                , QueryableStoreTypes.<String, LikeStats>keyValueStore()
            )
		);

		PriorityQueue<LikeStats> best = new PriorityQueue<>(Comparator.comparingDouble(s -> s.mean_score));

		store.all().forEachRemaining(entry -> {
            LikeStats stats = entry.value;
            if (best.size() < number) {
                best.offer(stats);
            } else if (
				kind.equals("best") && stats.mean_score > best.peek().mean_score
				|| kind.equals("worst") && stats.mean_score < best.peek().mean_score
			 ) {
                best.poll();
                best.offer(stats);
            }
        });

		List<LikeStats> result = new ArrayList<>(best);
        result.sort((a, b) -> Float.compare(b.mean_score, a.mean_score));
        return result;
    }

	private List<ViewStats> viewExtrema(String kind, int number) {
        ReadOnlyKeyValueStore<String, ViewStats> store = streams.store(
            StoreQueryParameters.fromNameAndType(
                StreamProcessing.storeViewStats
                , QueryableStoreTypes.<String, LikeStats>keyValueStore()
            )
		);

		PriorityQueue<ViewStats> best = new PriorityQueue<>(Comparator.comparingDouble(s -> s.start_only + s.half + s.full));

		store.all().forEachRemaining(entry -> {
            ViewStats stats = entry.value;
            if (best.size() < number) {
                best.offer(stats);
            } else if (
				kind.equals("best") && stats.mean_score > best.peek().mean_score
				|| kind.equals("worst") && stats.mean_score < best.peek().mean_score
			 ) {
                best.poll();
                best.offer(stats);
            }
        });

		List<ViewStats> result = new ArrayList<>(best);
        result.sort((a, b) -> Float.compare(b.mean_score, a.mean_score));
        return result;
    }
	
	/*
    @GetMapping("movies/{id}")
    public Movie getMovieById(@PathVariable String id) {
        ReadOnlyKeyValueStore<String, Movie> store =
            streams.store(StoreQueryParameters.fromNameAndType("movie-store", QueryableStoreTypes.keyValueStore()));
        return store.get(id);
    }
    @GetMapping("movies/{id}")
    public ResponseEntity<Movie> getMovie(@PathVariable String id) {
        return service.getMovieById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
*/

    @GetMapping("stats/ten/best/score")
    public List<LikeStats> bestScore() {
		return scoreExtrema("best", 10);
    }

    @GetMapping("stats/ten/worst/score")
    public List<LikeStats> worstScore() {
		return scoreExtrema("worst", 10);
    }

    @GetMapping("stats/ten/best/views")
    public List<ViewStats> bestViews() {
		return viewExtrema("best", 10);
    }

    @GetMapping("stats/ten/worst/views")
    public List<ViewStats> worstViews() {
		return viewExtrema("worst", 10);
    }
}
