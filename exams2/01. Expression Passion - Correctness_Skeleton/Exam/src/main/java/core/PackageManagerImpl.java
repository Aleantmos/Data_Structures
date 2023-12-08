package core;

import models.Package;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PackageManagerImpl implements PackageManager {

    HashMap<String, Package> allPackages = new HashMap<>();
    HashMap<String, List<Package>> dependencies = new HashMap<>();
    HashMap<String, List<Package>> dependants = new HashMap<>();

    @Override
    public void registerPackage(Package _package) {
        checkUniqueByNameAndVersion(_package);

        allPackages.put(_package.getId(), _package);
    }

    private void checkUniqueByNameAndVersion(Package currPackage) {
        String currName = currPackage.getName();
        String currVersion = currPackage.getVersion();
        for (Package value : allPackages.values()) {
            if (value.getName().equals(currName) &&
                    value.getVersion().equals(currVersion)) {
                throw new IllegalArgumentException();
            }
        }
    }

    @Override
    public void removePackage(String packageId) {
        checkIfExists(packageId);
        dependants.remove(packageId);
        dependencies.remove(packageId);
        allPackages.remove(packageId);

    }

    @Override
    public void addDependency(String packageId, String dependencyId) {
        Package src = checkIfExists(packageId);
        Package extern = checkIfExists(dependencyId);

        dependencies.putIfAbsent(packageId, new ArrayList<>());
        dependencies.get(packageId).add(extern);

        dependants.putIfAbsent(dependencyId, new ArrayList<>());
        dependants.get(dependencyId).add(src);

    }

    private Package checkIfExists(String packageId) {
        Package aPackage = allPackages.get(packageId);
        if (aPackage == null) {
            throw new IllegalArgumentException();
        }
        return aPackage;
    }

    @Override
    public boolean contains(Package _package) {
        return allPackages.containsKey(_package.getId());
    }

    @Override
    public int size() {
        return allPackages.size();
    }

    @Override
    public Iterable<Package> getDependants(Package _package) {
        List<Package> packages = dependants.get(_package.getId());
        if (packages.isEmpty()) {
            return new ArrayList<>();
        }
        return packages;
    }

    @Override
    public Iterable<Package> getIndependentPackages() {
        List<Package> independent = new ArrayList<>();

        for (Map.Entry<String, Package> entry : allPackages.entrySet()) {
            if (dependencies.get(entry.getKey()).isEmpty()) {
                independent.add(entry.getValue());
            }
        }

        return independent.stream()
                .sorted(
                        Comparator.comparing(Package::getReleaseDate)
                        .reversed()
                        .thenComparing(Package::getVersion))
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Package> getOrderedPackagesByReleaseDateThenByVersion() {
        Map<String, Package> groupedPackages = allPackages.values().stream()
                .collect(Collectors
                        .toMap(Package::getName, Function.identity(), BinaryOperator
                                .maxBy(Comparator.comparing(Package::getVersion))));

        return groupedPackages.values().stream()
                .sorted(Comparator.comparing(Package::getReleaseDate).reversed()
                        .thenComparing(Package::getVersion))
                .collect(Collectors.toList());
    }
}
