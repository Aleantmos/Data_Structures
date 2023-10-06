package barbershopjava;

import java.util.*;
import java.util.stream.Collectors;

public class BarberShopImpl implements BarberShop {

    private Map<String, Integer> barberClientsCnt = new HashMap<>();
    private Map<String, Barber> clientsBarber = new HashMap<>();
    private Map<String, Barber> barbers = new HashMap<>();
    private Map<String, Client> clients = new HashMap<>();
    private Map<String, Client> unassignedClients = new HashMap<>();

    @Override
    public void addBarber(Barber b) {
        if (exist(b)) {
            throw new IllegalArgumentException();
        }
        barbers.put(b.name, b);
        barberClientsCnt.put(b.name, 0);
    }

    @Override
    public void addClient(Client c) {
        if (exist(c)) {
            throw new IllegalArgumentException();
        }

        clients.put(c.name, c);
        unassignedClients.put(c.name, c);
    }

    @Override
    public boolean exist(Barber b) {
        return barbers.containsKey(b.name);
    }

    @Override
    public boolean exist(Client c) {
        return clients.containsKey(c.name);
    }

    @Override
    public Collection<Barber> getBarbers() {
        return barbers.values();
    }

    @Override
    public Collection<Client> getClients() {
        return clients.values();
    }

    @Override
    public void assignClient(Barber b, Client c) {
        if (!exist(b) || !exist(c)) {
            throw new IllegalArgumentException();
        }
        barberClientsCnt.put(b.name, barberClientsCnt.get(b.name) + 1);
        clientsBarber.put(c.name, b);
        unassignedClients.remove(c.name);
    }

    @Override
    public void deleteAllClientsFrom(Barber b) {
        if (!exist(b)) {
            throw new IllegalArgumentException();
        }
        barberClientsCnt.put(b.name, 0);
    }

    @Override
    public Collection<Client> getClientsWithNoBarber() {
        return unassignedClients.values();
    }

    @Override
    public Collection<Barber> getAllBarbersSortedWithClientsCountDesc() {
        return barberClientsCnt.keySet().stream()
                .sorted(Comparator.comparing(b -> barberClientsCnt.get(b))
                        .reversed())
                .map(b -> barbers.get(b))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Barber> getAllBarbersSortedWithStarsDescendingAndHaircutPriceAsc() {
        return barbers.values().stream()
                .sorted(Comparator.comparing((Barber b) -> b.stars)
                        .reversed()
                        .thenComparing(b -> b.haircutPrice))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Client> getClientsSortedByAgeDescAndBarbersStarsDesc() {
        return clients.values()
                .stream()
                .sorted(Comparator.comparing((Client c) -> c.age)
                        .reversed()
                        .thenComparing(c -> clientsBarber.get(c.name).stars)
                        .reversed())
                .collect(Collectors.toList());
    }
}
