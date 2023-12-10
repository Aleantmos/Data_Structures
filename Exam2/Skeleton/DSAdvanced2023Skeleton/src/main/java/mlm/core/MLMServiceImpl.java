package mlm.core;

import mlm.models.Seller;

import java.util.*;
import java.util.stream.Collectors;

public class MLMServiceImpl implements MLMService {

    Seller rootSeller;
    HashMap<String, String> parents = new HashMap<>();
    HashMap<String, List<String>> children = new HashMap<>();

    HashMap<String, Seller> allSellers = new HashMap<>();
    HashMap<String, Integer> sellersHires = new HashMap<>();
    HashMap<String, Integer> sellersSales = new HashMap<>();


    @Override
    public void addSeller(Seller seller) {
        if (rootSeller != null) {
            throw new IllegalArgumentException();
        }
        rootSeller = seller;
        allSellers.put(seller.id, seller);
        children.put(seller.id, new ArrayList<>());
        sellersSales.put(seller.id, 0);
    }

    @Override
    public void hireSeller(Seller parent, Seller newHire) {
        String parentId = parent.id;
        if (!allSellers.containsKey(parentId)) {
            throw new IllegalArgumentException();
        }
        String newHireId = newHire.id;
        if (allSellers.containsKey(newHireId)) {
            throw new IllegalArgumentException();
        }

        allSellers.put(newHireId, newHire);
        parents.put(newHireId, parentId);
        children.putIfAbsent(parentId, new ArrayList<>());
        children.get(parentId).add(newHireId);
        sellersSales.put(newHireId, 0);
    }

    @Override
    public boolean exists(Seller seller) {
        return allSellers.containsKey(seller.id);
    }


    @Override
    public void fire(Seller seller) {
        String toRemoveId = seller.id;
        allSellers.remove(toRemoveId);

        String parentId = parents.remove(toRemoveId);
        List<String> parentContainer = children.get(parentId);

        List<String> children = this.children.remove(toRemoveId);
        if (children != null) {
            for (String childId : children) {
                parents.replace(childId, parentId);
                parentContainer.add(childId);
            }
        }
    }

    @Override
    public void makeSale(Seller seller, int amount) {
        int commission = Math.round((amount * 5) / 100);
        String sellerId = seller.id;

        Seller sellerFromContainer = allSellers.get(sellerId);
        sellerFromContainer.earnings = amount;

        calculateCommission(sellerId, commission, sellerFromContainer);
        sellersSales.put(sellerId, sellersSales.get(sellerId) + 1);
    }

    private void calculateCommission(String sellerId, int commission, Seller seller) {
        String parentId = parents.get(sellerId);

        if (parentId == null) {
            return;
        }

        Seller parent = allSellers.get(parentId);
        parent.earnings += commission;
        seller.earnings -= commission;

        calculateCommission(parentId, commission, seller);
    }


    @Override
    public Collection<Seller> getByProfits() {
        return allSellers.values()
                .stream()
                .sorted(Comparator
                        .comparing((Seller s) -> s.earnings)
                        .reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Seller> getByEmployeeCount() {
        calculateHires(rootSeller.id);

        return allSellers.keySet()
                .stream()
                .map(key -> allSellers.get(key))
                .collect(Collectors.toList());
    }

    private int calculateHires(String sellerId) {
        List<String> children = this.children.get(sellerId);
        if (children == null) {
            return 0;
        }
        int hireCntForSeller = children.size();

        for (int child = 0; child < children.size(); child++) {
            String curr = children.get(child);
            hireCntForSeller += calculateHires(curr);
        }

        sellersHires.put(sellerId, hireCntForSeller);

        return hireCntForSeller;
    }


    @Override
    public Collection<Seller> getByTotalSalesMade() {

        return allSellers.keySet()
                .stream()
                .sorted(Comparator
                        .comparing(key -> sellersSales.get(key))
                        .reversed())
                .map(key -> allSellers.get(key))
                .collect(Collectors.toList());
    }
}
