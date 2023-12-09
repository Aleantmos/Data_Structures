package core;

import models.Doodle;

import java.util.*;
import java.util.stream.Collectors;

public class DoodleSearchImpl implements DoodleSearch {

    LinkedHashMap<String, Doodle> doodles = new LinkedHashMap<>();

    @Override
    public void addDoodle(Doodle doodle) {
        doodles.put(doodle.getId(), doodle);
    }

    @Override
    public void removeDoodle(String doodleId) {
        Doodle removed = doodles.remove(doodleId);
        if (removed == null) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public int size() {
        return doodles.size();
    }

    @Override
    public boolean contains(Doodle doodle) {
        return doodles.containsKey(doodle.getId());
    }

    @Override
    public Doodle getDoodle(String id) {
        Doodle doodle = doodles.get(id);
        if (doodle == null) {
            throw new IllegalArgumentException();
        }
        return doodle;
    }

    @Override
    public double getTotalRevenueFromDoodleAds() {
        return doodles.values().stream()
                .filter(d -> d.getIsAd())
                .mapToDouble((doodle1) -> {
                    double result = doodle1.getRevenue() * doodle1.getVisits();
                    return result;
                }).sum();
    }

    @Override
    public void visitDoodle(String title) {
        Doodle doodle = doodles.values()
                .stream()
                .filter(d -> d.getTitle().equals(title))
                .findFirst()
                .orElse(null);

        if (doodle == null) {
            throw new IllegalArgumentException();
        }

        doodle.setVisits(doodle.getVisits() + 1);
    }

    @Override
    public Iterable<Doodle> searchDoodles(String searchQuery) {
        List<Doodle> matchingDoodles = new ArrayList<>();

        for (Map.Entry<String, Doodle> entry : doodles.entrySet()) {
            Doodle doodle = entry.getValue();

            if (doodle.getTitle().contains(searchQuery)) {
                matchingDoodles.add(doodle);
            }
        }

        if (matchingDoodles.isEmpty()) {
            return matchingDoodles;
        }
        List<Doodle> adDoodles = new ArrayList<>();
        List<Doodle> regularDoodles = new ArrayList<>();

        for (Doodle doodle : matchingDoodles) {
            if (doodle.getIsAd()) {
                adDoodles.add(doodle);
            } else {
                regularDoodles.add(doodle);
            }
        }
        regularDoodles.sort(
                Comparator.comparingInt((Doodle doodle) ->
                        doodle.getTitle().indexOf(searchQuery) + doodle.getVisits())
                        .reversed());

        adDoodles.sort(Comparator.comparingInt((Doodle doodle) ->
                doodle.getTitle().indexOf(searchQuery) + doodle.getVisits())
                .reversed());

        List<Doodle> sortedDoodles = new ArrayList<>(adDoodles);
        sortedDoodles.addAll(regularDoodles);

        return sortedDoodles;

        /*return doodles.values()
                .stream()
                .filter(d -> d.getTitle().contains(searchQuery))
                .sorted(Comparator
                        .comparing(Doodle::getIsAd)
                        .thenComparing(Doodle::getVisits))
                .collect(Collectors.toList());*/


        //The results should be ordered by relevance  then by visits in descending order. but Ad Doodles should come firs
    }

    @Override
    public Iterable<Doodle> getDoodleAds() {
        return doodles.values().stream()
                .filter(d -> d.getIsAd())
                .sorted(Comparator.comparing(Doodle::getRevenue)
                        .thenComparing(Doodle::getVisits))
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Doodle> getTop3DoodlesByRevenueThenByVisits() {
        return doodles.values()
                .stream()
                .sorted(Comparator.comparing(Doodle::getRevenue)
                        .thenComparing(Doodle::getVisits)
                        .reversed())
                .limit(3)
                .collect(Collectors.toList());
    }
}
