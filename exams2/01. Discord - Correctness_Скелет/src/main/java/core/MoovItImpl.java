package core;

import models.Route;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MoovItImpl implements MoovIt {

    HashMap<String, Route> routes = new HashMap<>();
    @Override
    public void addRoute(Route route) {
        String routeId = route.getId();
        if (routes.containsKey(routeId)) {
            throw new IllegalArgumentException();
        }
        routes.put(routeId, route);
    }

    @Override
    public void removeRoute(String routeId) {
        Route toRemove = routes.remove(routeId);
        if (toRemove == null) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public boolean contains(Route route) {
        return routes.containsKey(route.getId());
    }

    @Override
    public int size() {
        return routes.size();
    }

    @Override
    public Route getRoute(String routeId) {
        Route route = routes.get(routeId);
        if (route == null) {
            throw new IllegalArgumentException();
        }

        return route;
    }

    @Override
    public void chooseRoute(String routeId) {
        Route route = getRoute(routeId);

        route.setPopularity(route.getPopularity() + 1);
    }

    @Override
    public Iterable<Route> searchRoutes(String startPoint, String endPoint) {
        List<Route> isFavouriteUnfiltered = new ArrayList<>();
        List<Route> isNotFavouriteUnfiltered = new ArrayList<>();

        for (Route route : routes.values()) {
            List<String> locationPoints = route.getLocationPoints();
            String currStart = locationPoints.get(0);
            String currEnd = locationPoints.get(locationPoints.size() - 1);

            if (currStart.equals(startPoint) && currEnd.equals(endPoint)) {
                if (route.getIsFavorite()) {
                    isFavouriteUnfiltered.add(route);
                } else {
                    isNotFavouriteUnfiltered.add(route);
                }
            }
        }


        List<Route> filteredFavourite = isFavouriteUnfiltered.stream()
                .sorted(Comparator.comparing((Route r) -> r.getLocationPoints().size())
                        .thenComparing(Route::getDistance)
                        .reversed())
                .collect(Collectors.toList());

        List<Route> filteredNotFavourite = isNotFavouriteUnfiltered.stream()
                .sorted(Comparator.comparing((Route r) -> r.getLocationPoints().size())
                        .thenComparing(Route::getDistance)
                        .reversed())
                .collect(Collectors.toList());

        filteredFavourite.addAll(filteredNotFavourite);

        return filteredFavourite;
    }

    @Override
    public Iterable<Route> getFavoriteRoutes(String destinationPoint) {
        return routes.values().stream()
                .filter(r -> {
                    List<String> locationPoints = r.getLocationPoints();
                    int i = locationPoints.indexOf(destinationPoint);
                    return i != -1 && i != 1;
                }).sorted(Comparator.comparing(Route::getDistance)
                        .thenComparing(Route::getPopularity).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Route> getTop5RoutesByPopularityThenByDistanceThenByCountOfLocationPoints() {
        return routes.values()
                .stream()
                .sorted(Comparator.comparing(Route::getPopularity)
                        .thenComparing(Route::getDistance)
                        .thenComparing(r -> r.getLocationPoints().size())
                        .reversed())
                .collect(Collectors.toList());
    }
}
