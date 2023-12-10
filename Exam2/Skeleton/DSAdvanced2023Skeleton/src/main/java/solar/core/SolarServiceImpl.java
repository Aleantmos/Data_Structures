package solar.core;

import solar.models.Inverter;
import solar.models.PVModule;

import java.util.*;
import java.util.stream.Collectors;

public class SolarServiceImpl implements SolarService {

    HashMap<String, Inverter> inverters = new HashMap<>();

    HashMap<String, HashMap<String, List<PVModule>>> invertersArrays = new HashMap<>();
    HashMap<String, String> arrayIdToInverterId = new HashMap<>();
    HashMap<String, Integer> invertersCapacity = new HashMap<>();
    LinkedHashMap<PVModule, String> pvModulesInUse = new LinkedHashMap<>();

    @Override
    public void addInverter(Inverter inverter) {
        String inverterId = inverter.id;
        Inverter returned = inverters.putIfAbsent(inverterId, inverter);

        if (returned != null) {
            throw new IllegalArgumentException();
        }

        invertersCapacity.put(inverterId, 0);
    }

    @Override
    public void addArray(Inverter inverter, String arrayId) {

        String inverterId = inverter.id;
        //if the inverter is missing
        if (!inverters.containsKey(inverterId)) {
            throw new IllegalArgumentException();
        }
        //the inverter doesn’t have the capacity for more arrays
        Integer capacityOfInverter = invertersCapacity.get(inverterId);
        if (capacityOfInverter + 1 > inverter.maxPvArraysConnected) {
            throw new IllegalArgumentException();
        }


        //put and check if this arrayId is already in use
        String inverterOfArr = arrayIdToInverterId.putIfAbsent(arrayId, inverterId);
        if (inverterOfArr != null) {
            throw new IllegalArgumentException();
        }

        invertersCapacity.put(inverterId, capacityOfInverter + 1);
        invertersArrays.put(inverterId, new HashMap<>());
        invertersArrays.get(inverterId).put(arrayId, new ArrayList<>());
    }

    @Override
    public void addPanel(Inverter inverter, String arrayId, PVModule pvModule) {

        String inverterId = inverter.id;

        // the Inverter is missing
        HashMap<String, List<PVModule>> arrayModules = invertersArrays.get(inverterId);
        if (arrayModules.isEmpty()) {
            throw new IllegalArgumentException();
        }

        // this array is not associated with this Inverter
        if (!arrayIdToInverterId.get(arrayId).equals(inverterId)) {
            throw new IllegalArgumentException();
        }

        // the PVModule is already in use
        for (PVModule module : pvModulesInUse.keySet()) {
            if (module.equals(pvModule)) {
                throw new IllegalArgumentException();
            }
        }

        pvModulesInUse.put(pvModule, inverterId);
        arrayModules.get(arrayId).add(pvModule);
    }

    @Override
    public boolean containsInverter(String id) {
        return inverters.containsKey(id);
    }


    @Override
    public boolean isPanelConnected(PVModule pvModule) {
        return pvModulesInUse.containsKey(pvModule);
    }

    @Override
    public Inverter getInverterByPanel(PVModule pvModule) {
        String inverterId = pvModulesInUse.get(pvModule);
        return inverters.get(inverterId);
    }

    @Override
    public void replaceModule(PVModule oldModule, PVModule newModule) {
        String oldInverterId = pvModulesInUse.get(oldModule);
        if (oldInverterId == null) {
            throw new IllegalArgumentException();
        }

        String newInverterId = pvModulesInUse.get(newModule);
        if (newInverterId != null) {
            throw new IllegalArgumentException();
        }

        HashMap<String, List<PVModule>> arraysOfInverter = invertersArrays.get(oldInverterId);

        for (List<PVModule> array : arraysOfInverter.values()) {
            for (int i = 0; i < array.size(); i++) {
                if (array.get(i).equals(oldModule)) {
                    array.set(i, newModule);
                    pvModulesInUse.remove(oldModule);
                    pvModulesInUse.put(newModule, oldInverterId);
                    return;
                }
            }
        }
    }

    @Override
    public Collection<Inverter> getByProductionCapacity() {
        return inverters.keySet().stream()
                .sorted(Comparator
                        .comparing(inverterId -> {
                            HashMap<String, List<PVModule>> stringListHashMap = invertersArrays.get(inverterId);
                            return stringListHashMap.values().stream()
                                    .mapToDouble(list -> list
                                            .stream().mapToDouble(pvModule -> pvModule.maxWattProduction)
                                            .sum())
                                    .sum();
                        }))
                .map(key -> inverters.get(key))
                .collect(Collectors.toList());

    }

    @Override
    public Collection<Inverter> getByNumberOfPVModulesConnected() {
        return invertersArrays.keySet()
                .stream()
                .sorted(Comparator
                        .comparing(inverterKey -> {
                            long modulesCnt = 0;
                            for (String currInverterKey : pvModulesInUse.values()) {
                                if (currInverterKey.equals(inverterKey)) {

                                    modulesCnt++;
                                }
                            }
                            return modulesCnt;
                        })
                        .thenComparing(inverterKey -> invertersArrays.get(inverterKey).keySet().size()))
                .map(key -> inverters.get(key))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<PVModule> getByWattProduction() {
        return pvModulesInUse.keySet().stream()
                .sorted(Comparator.comparing(module -> module.maxWattProduction))
                .collect(Collectors.toList());
    }
}
