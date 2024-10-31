package ecosystem.repository;

import ecosystem.model.Conditions;

public interface SimulationRepository {

    void createNewSimulation(String ecosystemName);

    void loadSimulation(String ecosystemName);

    void saveEcosystemParameters(String ecosystemName, Conditions conditions);

    Conditions readEcosystemConditions(String ecosystemName);

    boolean ecosystemExists(String ecosystemName);
}
