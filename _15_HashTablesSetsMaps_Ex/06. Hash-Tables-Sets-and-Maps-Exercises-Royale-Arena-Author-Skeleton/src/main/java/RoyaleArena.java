import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class RoyaleArena implements IArena {
    //private Set<Battlecard> battlecards;
    private Map<Integer, Battlecard> cardsByIds;
    private Map<CardType, Set<Battlecard>> cardsByType;

    public RoyaleArena() {
        //this.battlecards = new HashSet<>();
        this.cardsByIds = new LinkedHashMap<>();
        this.cardsByType = new LinkedHashMap<>();
    }

    @Override
    public void add(Battlecard card) {
        this.cardsByIds.putIfAbsent(card.getId(), card);
        this.cardsByType.putIfAbsent(card.getType(), new TreeSet<>(Battlecard::compareTo));
        this.cardsByType.get(card.getType()).add(card);
    }

    @Override
    public boolean contains(Battlecard card) {
        return this.cardsByIds.containsKey(card.getId());
    }

    @Override
    public int count() {
        return this.cardsByIds.size();
    }

    @Override
    public void changeCardType(int id, CardType type) {
        Battlecard battlecard = this.cardsByIds.get(id);
        if (battlecard == null) {
            throw new IllegalArgumentException("No card with id: " + id);
        }
        battlecard.setType(type);
    }

    @Override
    public Battlecard getById(int id) {
        Battlecard toGet = this.cardsByIds.get(id);
        if (toGet == null) {
            throw new UnsupportedOperationException();
        }

        return toGet;
    }

    @Override
    public void removeById(int id) {
        Battlecard toRemove = this.cardsByIds.remove(id);
        if (toRemove == null) {
            throw new UnsupportedOperationException();
        }

        this.cardsByType.get(toRemove.getType()).remove(toRemove);
    }

    @Override
    public Iterable<Battlecard> getByCardType(CardType type) {
        return getBattleCardsByType(type);
    }

    @Override
    public Iterable<Battlecard> getByTypeAndDamageRangeOrderedByDamageThenById(CardType type, int lo, int hi) {
        Set<Battlecard> battlecards = getBattleCardsByType(type);

        return battlecards.stream()
                .filter(c -> c.getDamage() >= lo && c.getDamage() < hi)
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Battlecard> getByCardTypeAndMaximumDamage(CardType type, double damage) {
        Set<Battlecard> battlecards = this.cardsByType.get(type);

        List<Battlecard> result = battlecards.stream()
                .filter(c -> c.getDamage() <= damage)
                .collect(Collectors.toList());

        if (result.isEmpty()) {
            throw new UnsupportedOperationException();
        }

        return result;
    }

    @Override
    public Iterable<Battlecard> getByNameOrderedBySwagDescending(String name) {
        List<Battlecard> battlecards = getBattlecardsByPredicate(b -> b.getName().equals(name));

        if (battlecards.isEmpty()) {
            throw new UnsupportedOperationException();
        }
        battlecards.sort(Comparator.comparingDouble(Battlecard::getSwag).reversed());

        return battlecards;
    }

    @Override
    public Iterable<Battlecard> getByNameAndSwagRange(String name, double lo, double hi) {
        List<Battlecard> battlecards = getBattlecardsByPredicate(
                b -> b.getDamage() >= lo && b.getDamage() < hi && b.getName().equals(name));

        if (battlecards.isEmpty()) {
            throw new UnsupportedOperationException();
        }

        battlecards
                .sort(Comparator.comparingDouble(Battlecard::getSwag).reversed()
                .thenComparing(Battlecard::getId));


        return battlecards;
    }

    @Override
    public Iterable<Battlecard> getAllByNameAndSwag() {
        Map<String, Battlecard> battlecards = new HashMap<>();
        for (Battlecard battlecard : cardsByIds.values()) {
            if (!battlecards.containsKey(battlecard.getName())) {
                battlecards.put(battlecard.getName(), battlecard);
            } else {
                double oldSwag = battlecards.get(battlecard.getName()).getSwag();
                double newSwag = battlecard.getSwag();

                if (oldSwag < newSwag) {
                    battlecards.put(battlecard.getName(), battlecard);
                }
            }
        }

        return battlecards.values();
    }

    @Override
    public Iterable<Battlecard> findFirstLeastSwag(int n) {
        if (n > this.count()) {
            throw new UnsupportedOperationException();
        }

        return this.cardsByIds.values()
                .stream()
                .sorted(Comparator.comparingDouble(Battlecard::getSwag)
                        .thenComparingInt(Battlecard::getId))
                        .limit(n)
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Battlecard> getAllInSwagRange(double lo, double hi) {
        return getBattlecardsByPredicate(c -> c.getSwag() >= lo && c.getSwag() <= hi);
    }

    @Override
    public Iterator<Battlecard> iterator() {
        return this.cardsByIds.values().iterator();
    }

    private Set<Battlecard> getBattleCardsByType(CardType type) {
        Set<Battlecard> battlecards = this.cardsByType.get(type);

        if (battlecards == null) {
            throw new UnsupportedOperationException();
        }
        return battlecards;
    }

    private List<Battlecard> getBattlecardsByPredicate(Predicate<Battlecard> predicate) {
        List<Battlecard> battlecards = new ArrayList<>();

        for (Battlecard battlecard : cardsByIds.values()) {
            if (predicate.test(battlecard)) {
                battlecards.add(battlecard);
            }
        }
        return battlecards;
    }
}
