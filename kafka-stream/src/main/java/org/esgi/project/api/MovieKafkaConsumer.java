package org.esgi.project.api;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
public class MovieKafkaConsumer {
	private final List<Movie> movies = new CopyOnWriteArrayList<>();

	@KafkaListener(topics = "movies", groupId = "group_id")
	public void consume(Movie movie) {
		movies.add(movie);
	}

	public List<Movie> getAllMovies() {
		return movies;
	}

	public Optional<Movie> getMovieById(String id) {
		return movies.stream().filter(m -> m.getId().equals(id)).findFirst();
	}

	public List<Movie> getTopByScore(boolean best) {
		return movies.stream().sorted(Comparator.comparingDouble(Movie::getScore).reversed()).limit(10)
				.collect(Collectors.toList());
	}

	public List<Movie> getTopByViews(boolean best) {
		Comparator<Movie> comp = Comparator.comparingInt(Movie::getViews);
		if (best)
			comp = comp.reversed();
		return movies.stream().sorted(comp).limit(10).collect(Collectors.toList());
	}
}
