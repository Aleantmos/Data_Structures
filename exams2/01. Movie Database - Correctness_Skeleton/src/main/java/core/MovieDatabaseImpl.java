package core;

import models.Movie;

import java.util.*;
import java.util.stream.Collectors;

public class MovieDatabaseImpl implements MovieDatabase {
    HashMap<String, Movie> movies = new HashMap<>();

    HashMap<String, Integer> actorsPopularity = new HashMap<>();

    @Override
    public void addMovie(Movie movie) {
        movies.put(movie.getId(), movie);
    }

    @Override
    public void removeMovie(String movieId) {
        Movie movie = movies.remove(movieId);
        if (movie == null) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public int size() {
        return movies.size();
    }

    @Override
    public boolean contains(Movie movie) {
        return movies.containsKey(movie.getId());
    }

    @Override
    public Iterable<Movie> getMoviesByActor(String actorName) {
        List<Movie> moviesByActor = new ArrayList<>();
        for (Movie movie : movies.values()) {
            for (String actor : movie.getActors()) {
                if (actor.equals(actorName)) {
                    moviesByActor.add(movie);
                    break;
                }
            }
        }

        if (moviesByActor.isEmpty()) {
            throw new IllegalArgumentException();
        }
        //ordered by rating in descending order and by release year in descending order.
        return moviesByActor.stream()
                .sorted(
                        Comparator
                                .comparing(Movie::getRating)
                                .thenComparing(Movie::getReleaseYear)
                                .reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Movie> getMoviesByActors(List<String> actors) {
        List<Movie> moviesByActors = new ArrayList<>();
        for (Movie movie : movies.values()) {
            List<String> currMovieActors = movie.getActors();
            boolean addMovie = true;
            for (String actor : actors) {
                if (!currMovieActors.contains(actor)) {
                    addMovie = false;
                    break;
                }
            }
            if (addMovie) {
                moviesByActors.add(movie);
            }
        }

        if (moviesByActors.isEmpty()) {
            throw new IllegalArgumentException();
        }
        //ordered by rating in descending order and by release year in descending order
        return moviesByActors.stream()
                .sorted(Comparator.comparing(Movie::getRating)
                        .thenComparing(Movie::getReleaseYear)
                        .reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Movie> getMoviesByYear(Integer releaseYear) {
        return movies.values().stream()
                .filter(m -> m.getReleaseYear() == releaseYear)
                .sorted(Comparator.comparing(Movie::getRating)
                        .reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Movie> getMoviesInRatingRange(double lowerBound, double upperBound) {
        return movies.values().stream()
                .filter(m -> m.getReleaseYear() >= lowerBound && m.getReleaseYear() <= upperBound)
                .sorted(Comparator.comparing(Movie::getRating).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Movie> getAllMoviesOrderedByActorPopularityThenByRatingThenByYear() {
        getActorsPopularity();
        return movies.values().stream()
                .sorted(Comparator.comparing((Movie m) -> {
                            long total = 0;
                            for (String actor : m.getActors()) {
                                total += actorsPopularity.get(actor);
                            }
                            return total;
                        })
                        .thenComparing(Movie::getRating)
                        .thenComparing(Movie::getReleaseYear)
                        .reversed())
                .collect(Collectors.toList());
    }

    private void getActorsPopularity() {
        for (Movie movie : movies.values()) {
            for (String actor : movie.getActors()) {
                actorsPopularity.putIfAbsent(actor, 0);
                actorsPopularity.put(actor, actorsPopularity.get(actor) + 1);
            }
        }
    }
}
