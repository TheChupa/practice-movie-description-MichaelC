package LaunchCode.AI_Movie_Gen.controllers;


import LaunchCode.AI_Movie_Gen.models.Movie;
import LaunchCode.AI_Movie_Gen.repositories.MovieRepository;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    @Autowired
    MovieRepository movieRepository;


    @GetMapping("") //Get all movies
    public ResponseEntity<?> getAllMovies() {
        List<Movie> allMovies = movieRepository.findAll();
        return new ResponseEntity<>(allMovies, HttpStatus.OK); //200
    }

    @GetMapping(value = "/details/{movieId}", produces = MediaType.APPLICATION_JSON_VALUE) //Get movie by ID
    public ResponseEntity<?> getMoviebyID(@PathVariable(value = "movieId") int movieId) {
        Movie currentMovie = movieRepository.findById(movieId).orElse(null);
        if (currentMovie != null) {
            return new ResponseEntity<>(currentMovie, HttpStatus.OK); //200
        } else {
            String response = "Movie with ID of " + movieId + " not found.";
            return new ResponseEntity<>(Collections.singletonMap("response", response), HttpStatus.NOT_FOUND); //404
        }
    }

    @PostMapping("add")
    public ResponseEntity<?> createnewMovie(@RequestParam(value = "title") String title, @RequestParam(value = "rating") double rating) {
        Movie newMovie = new Movie(title, rating);


        try {
            Client client = new Client();

            GenerateContentResponse response = client.models.generateContent("gemini-2.0-flash-001", "Get description of: " + title + ". Make it under 2048 characters in length. No Spoilers Please, if you can.", null);
            newMovie.setDescription(response.text());
        } catch (Exception e) {
            newMovie.setDescription("Description unavailable due to AI being slow");
        }
        movieRepository.save(newMovie);
        return new ResponseEntity<>(newMovie, HttpStatus.CREATED); //201
    }

    @DeleteMapping(value = "/delete/{movieId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteMovie(@PathVariable(value = "movieId") int movieId) {
        Movie currentMovie = movieRepository.findById(movieId).orElse(null);
        if (currentMovie != null) {
            movieRepository.deleteById(movieId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); //204
        } else {
            String response = "Movie with ID of " + movieId + " not found.";
            return new ResponseEntity<>(Collections.singletonMap("response", response), HttpStatus.NOT_FOUND); //404
        }
    }


}



