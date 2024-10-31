package ecosystem.service;

import ecosystem.model.Conditions;

import java.util.Map;

public interface PredictionService {
    Map<String, String> predictPopulationChanges(Conditions conditions);
}
