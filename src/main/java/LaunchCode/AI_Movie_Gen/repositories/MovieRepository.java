package LaunchCode.AI_Movie_Gen.repositories;

import LaunchCode.AI_Movie_Gen.models.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer> {
}
