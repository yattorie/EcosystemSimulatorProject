package ecosystem.service;

import ecosystem.model.Conditions;

public interface SimulationService {
    Conditions getCurrentConditions(String ecosystemName);

    String createEcosystem();

    String loadEcosystem();
}
