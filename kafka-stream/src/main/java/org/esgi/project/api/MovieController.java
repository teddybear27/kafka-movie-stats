package org.esgi.project.api;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import java.util.List;

@RestController
@RequestMapping("/")
public class MovieController {
	@Autowired
	private MovieKafkaConsumer service; // TO CHANGE with the right class name/path

	@GetMapping("movies/{id}")
	public ResponseEntity<Movie> getMovie(@PathVariable String id) {
		return service.getMovieById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("stats/ten/best/score")
	public List<Movie> bestScore() {
		return service.getTopByScore(true);
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
