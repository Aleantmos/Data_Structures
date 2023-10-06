package org.softuni.exam.structures;

import org.softuni.exam.entities.Deliverer;
import org.softuni.exam.entities.Package;

import java.util.*;
import java.util.stream.Collectors;

public class DeliveriesManagerImpl implements DeliveriesManager {

    private Map<String, Deliverer> deliverersById = new LinkedHashMap<>();
    private Map<String, Package> packagesById = new LinkedHashMap<>();
    private Map<String, Package> unassignedPackages = new LinkedHashMap<>();
    private Map<String, Integer> packagesByDelivererId = new LinkedHashMap<>();

    @Override
    public void addDeliverer(Deliverer deliverer) {
        deliverersById.put(deliverer.getId(), deliverer);
        packagesByDelivererId.put(deliverer.getId(), 0);

    }

    @Override
    public void addPackage(Package aPackage) {
        packagesById.put(aPackage.getId(), aPackage);
        unassignedPackages.put(aPackage.getId(), aPackage);
    }

    @Override
    public boolean contains(Deliverer deliverer) {
        return deliverersById.get(deliverer.getId()) != null;
    }

    @Override
    public boolean contains(Package aPackage) {
        return packagesById.get(aPackage.getId()) != null;
    }

    @Override
    public Iterable<Deliverer> getDeliverers() {
        return deliverersById.values();
    }

    @Override
    public Iterable<Package> getPackages() {
        return packagesById.values();
    }

    @Override
    public void assignPackage(Deliverer deliverer, Package aPackage) throws IllegalArgumentException {
        if (!contains(deliverer) || !contains(aPackage)) {
            throw new IllegalArgumentException();
        }
        packagesByDelivererId.put(deliverer.getId(), packagesByDelivererId.get(deliverer.getId()) + 1);
        unassignedPackages.remove(aPackage.getId());
    }

    @Override
    public Iterable<Package> getUnassignedPackages() {
        return unassignedPackages.values();
    }

    @Override
    public Iterable<Package> getPackagesOrderedByWeightThenByReceiver() {
        return packagesById.values()
                .stream()
                .sorted(Comparator.comparing(Package::getWeight).reversed()
                        .thenComparing(Package::getReceiver))
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Deliverer> getDeliverersOrderedByCountOfPackagesThenByName() {
        return deliverersById.values().stream()
                .sorted(Comparator.comparing((Deliverer d) -> packagesByDelivererId.get(d.getId()))
                        .reversed()
                        .thenComparing(Deliverer::getName))
                .collect(Collectors.toList());
    }
}
