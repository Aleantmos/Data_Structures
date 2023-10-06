package org.softuni.exam.structures;

import org.softuni.exam.entities.Actor;
import org.softuni.exam.entities.Movie;

import java.util.*;
import java.util.stream.Collectors;

public class MovieDatabaseImpl implements MovieDatabase {

    private List<Movie> movies;

    private Map<Actor, List<Movie>> actorsMovies;

    public MovieDatabaseImpl() {
        this.movies = new ArrayList<>();
        this.actorsMovies = new HashMap<>();
    }

    @Override
    public void addActor(Actor actor) {
        if (actor != null) {
            actorsMovies.putIfAbsent(actor, new ArrayList<>());
        }
    }

    @Override
    public void addMovie(Actor actor, Movie movie) throws IllegalArgumentException {
        actorsMovies.get(actor).add(movie);
        movies.add(movie);
    }

    @Override
    public boolean contains(Actor actor) {
        return actorsMovies.containsKey(actor);
    }

    @Override
    public boolean contains(Movie movie) {
        return movies.contains(movie);
    }

    @Override
    public Iterable<Movie> getAllMovies() {
        return movies;
    }

    @Override
    public Iterable<Actor> getNewbieActors() {
        return actorsMovies.keySet().stream()
                .filter(a -> actorsMovies.get(a).isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Movie> getMoviesOrderedByBudgetThenByRating() {
        return movies.stream()
                .sorted(Comparator
                        .comparingDouble(Movie::getBudget)
                        .reversed()
                        .thenComparingDouble(Movie::getRating)
                        .reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Actor> getActorsOrderedByMaxMovieBudgetThenByMoviesCount() {
        return actorsMovies.keySet().stream()
                .sorted(Comparator.comparing((Actor a) -> actorsMovies.get(a).stream()
                        .mapToDouble(Movie::getBudget)
                                .max()
                                .getAsDouble())
                        .thenComparing(a -> actorsMovies.get(a).size()))
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Movie> getMoviesInRangeOfBudget(double lower, double upper) {
        return movies.stream()
                .sorted(Comparator.comparing(Movie::getRating)
                        .reversed())
                .filter(m -> m.getBudget() >= lower && m.getBudget() <= upper)
                .collect(Collectors.toList());
    }
}
