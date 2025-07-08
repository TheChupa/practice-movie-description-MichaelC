package LaunchCode.AI_Movie_Gen.controllers;

import LaunchCode.AI_Movie_Gen.models.Movie;
import LaunchCode.AI_Movie_Gen.repositories.MovieRepository;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/movies")
public class MoviePageController {


    @Autowired
    private MovieRepository movieRepository;

    //These all return HTML. The names need to match EXACTLY. so movieList is the movieList HTML template, same for the others.
    //It's assigning the command movie.Repository.findAll to ${movies}
    @GetMapping("")
    public String listAllMovies(Model model) {
        model.addAttribute("movies", movieRepository.findAll());
        return "movieList";
    }

//Makes the new Model a movie which maps to the Movie class, the form points to the PostMapping to save it.
    @GetMapping("/new")
    public String showMovieForm(Model model) {
        model.addAttribute("movie", new Movie());
        return "movieForm";

    }
//This is where the movie is saved.
    @PostMapping("save")
    public String saveMovie(@ModelAttribute("movie") Movie movie, Model model) {

        //This is the AI response, if the AI is down it will catch the error and then fill in the default AI overlord is tired
        try {
            Client client = new Client();
            GenerateContentResponse response = client.models.generateContent("gemini-2.0-flash-001", "Get description of: " + movie.getTitle() + ". This will be a movie. Make it under 2048 characters in length. No Spoilers please, if you can.", null);
            movie.setDescription(response.text());
        } catch (Exception e) {
            movie.setDescription("AI overlord is tired, ask later");
        }
        movieRepository.save(movie);
        model.addAttribute("success", true);
        return "movieForm";
    }

    @PostMapping("/delete/{id}")
    public String deleteMovie(@PathVariable int id) {
        movieRepository.deleteById(id);
        return "redirect:/movies"; // this redirects the page back to the movie list after deleting them with the button inside the table.
    }
}
