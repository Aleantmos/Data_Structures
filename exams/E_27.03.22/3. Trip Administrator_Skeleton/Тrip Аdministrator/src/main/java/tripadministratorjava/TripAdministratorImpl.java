package tripadministratorjava;

import java.util.*;
import java.util.stream.Collectors;

public class TripAdministratorImpl implements TripAdministrator {

    private Map<String, Company> companies = new HashMap<>();
    private Map<String, Trip> trips = new HashMap<>();
    private Map<String, List<Trip>> companiesTrips = new HashMap<>();

    @Override
    public void addCompany(Company c) {
        if (exist(c)) {
            throw new IllegalArgumentException();
        }

        companies.put(c.name, c);
        companiesTrips.put(c.name, new ArrayList<>());
    }

    @Override
    public void addTrip(Company c, Trip t) {
        if (!exist(c) || companiesTrips.get(c.name).contains(t)) {
            throw new IllegalArgumentException();
        }

        companiesTrips.get(c.name).add(t);
        //companies.get(c.name).tripOrganizationLimit++;
        trips.put(t.id, t);
    }

    @Override
    public boolean exist(Company c) {
        return companies.containsKey(c.name);
    }

    @Override
    public boolean exist(Trip t) {
        return trips.containsKey(t.id);
    }

    @Override
    public void removeCompany(Company c) {
        if (!exist(c)) {
            throw new IllegalArgumentException();
        }

        companies.remove(c.name);
        List<Trip> tripList = companiesTrips.get(c.name);
        for (Trip trip : tripList) {
            trips.remove(trip.id);
        }
        companiesTrips.remove(c.name);
    }

    @Override
    public Collection<Company> getCompanies() {
        return companies.values();
    }

    @Override
    public Collection<Trip> getTrips() {
        return trips.values();
    }

    @Override
    public void executeTrip(Company c, Trip t) {
        if (!exist(c) || !companiesTrips.get(c.name).contains(t)) {
            throw new IllegalArgumentException();
        }

        companiesTrips.get(c.name).remove(t);

        /*List<Trip> currTrips = companiesTrips.get(c.name);


        for (Trip trip : currTrips) {
            if (trip.id.equals(t.id)) {
                currTrips.remove(t);
            }
        }
*/
        trips.remove(t.id);
    }

    @Override
    public Collection<Company> getCompaniesWithMoreThatNTrips(int n) {
        return companies.values()
                .stream()
                .filter(c -> companiesTrips.get(c.name).size() > n)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Trip> getTripsWithTransportationType(Transportation t) {
        String name = t.name();
        return trips.values()
                .stream()
                .filter(transport -> name.equals(transport.transportation.name()))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Trip> getAllTripsInPriceRange(int lo, int hi) {
        return trips.values()
                .stream()
                .filter(t -> t.price >= lo && t.price <= hi)
                .collect(Collectors.toList());
    }
}
