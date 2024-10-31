package ecosystem.service;

public interface ActionHandlerService {
    void addPlant(String ecosystemName);

    void addAnimal(String ecosystemName);

    void updateAnimalDiet(String ecosystemName);

    void deleteSpecies(String ecosystemName);

    void handleInteraction(String ecosystemName);

    void displayPopulationPredictions(String ecosystemName);

}
