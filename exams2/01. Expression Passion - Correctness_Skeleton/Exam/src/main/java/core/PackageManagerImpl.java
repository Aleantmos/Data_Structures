package core;

import models.Package;

public class PackageManagerImpl implements PackageManager {
    @Override
    public void registerPackage(Package _package) {

    }

    @Override
    public void removePackage(String packageId) {

    }

    @Override
    public void addDependency(String packageId, String dependencyId) {

    }

    @Override
    public boolean contains(Package _package) {
        return false;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Iterable<Package> getDependants(Package _package) {
        return null;
    }

    @Override
    public Iterable<Package> getIndependentPackages() {
        return null;
    }

    @Override
    public Iterable<Package> getOrderedPackagesByReleaseDateThenByVersion() {
        return null;
    }
}
