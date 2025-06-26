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

        streams = new KafkaStreams(streamProcessing.buildTopology(), buildProperties());
        streams.setUncaughtExceptionHandler(Exception -> {
            streams.close();
            streams.cleanUp();
            return null;
        });
        streams.start();
	}

	@GetMapping("movies/{id}")
	public ResponseEntity<Movie> getMovie(@PathVariable String id) {
		return service.getMovieById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("stats/ten/best/score")
	public List<Movie> bestScore() {
		streams.store("name").get().toJson();
		// récupérer le store
		// le requêter
		// le convertir

		// créer une classe Movie avec juste les attributs attendus en sortie d'API
		// return une liste de ça suffit.


		return list(Movie);
	}

	@GetMapping("stats/ten/worst/score")
	public List<Movie> worstScore() {
		return service.getTopByScore(false);
	}

	@GetMapping("stats/ten/best/views")
	public List<Movie> bestViews() {
		return service.getTopByViews(true);
	}

	@GetMapping("stats/ten/worst/views")
	public List<Movie> worstViews() {
		return service.getTopByViews(false);
	}
}
