package ecosystem.service.impl;

import ecosystem.model.Animal;
import ecosystem.model.Conditions;
import ecosystem.model.Plant;
import ecosystem.repository.SpeciesRepository;
import ecosystem.repository.impl.SpeciesRepositoryImpl;
import ecosystem.service.*;

import java.util.Map;

import static ecosystem.util.Messages.POPULATION;

// Класс ActionHandlerServiceImpl реализует интерфейс ActionHandlerService
public class ActionHandlerServiceImpl implements ActionHandlerService {
    // Singleton instance для обеспечения единственного экземпляра репозитория симуляции
    private static ActionHandlerServiceImpl instance;

    // Инициализируем зависимости
    private final SpeciesRepository speciesRepository = SpeciesRepositoryImpl.getInstance(); // Репозиторий для работы с видами
    private final SimulationService simulationService = SimulationServiceImpl.getInstance(); // Сервис для симуляций
    private final InteractionService interactionService = InteractionServiceImpl.getInstance(); // Сервис для взаимодействия между видами
    private final PredictionService predictionService = PredictionServiceImpl.getInstance(); // Сервис для предсказания изменений популяции
    private final UIService uiService = UIServiceImpl.getInstance(); // Сервис для взаимодействия с пользователем

    private ActionHandlerServiceImpl() {
    }

    // Метод для получения экземпляра Singleton
    public static synchronized ActionHandlerServiceImpl getInstance() {
        if (instance == null) {
            instance = new ActionHandlerServiceImpl(); // Создаем новый экземпляр, если он еще не существует
        }
        return instance; // Возвращаем существующий экземпляр
    }

    @Override
    public void addPlant(String ecosystemName) {
        // Запрашиваем у пользователя имя растения
        String plantName = uiService.askForPlantName();
        Plant plant = new Plant(plantName); // Создаем объект растения
        // Добавляем растение в экосистему через репозиторий
        speciesRepository.addPlant(ecosystemName, plant);
    }

    @Override
    public void addAnimal(String ecosystemName) {
        // Запрашиваем у пользователя детали о новом животном
        Animal animal = uiService.askForAnimalDetails();
        // Добавляем животное в экосистему через репозиторий
        speciesRepository.addAnimal(ecosystemName, animal);
    }

    @Override
    public void updateAnimalDiet(String ecosystemName) {
        // Запрашиваем у пользователя имя животного для обновления
        String animalName = uiService.askForAnimalNameToUpdate();
        // Запрашиваем новую диету для животного
        String newDiet = uiService.askForNewDiet();
        // Обновляем диету животного в экосистеме через репозиторий
        speciesRepository.updateAnimalDiet(ecosystemName, animalName, newDiet);
    }

    @Override
    public void deleteSpecies(String ecosystemName) {
        // Запрашиваем у пользователя, является ли вид растением
        boolean isPlant = uiService.askIsPlant();
        // Запрашиваем имя вида для удаления
        String speciesName = uiService.askForSpeciesName(isPlant);
        // Удаляем вид из экосистемы через репозиторий
        speciesRepository.deleteSpecies(ecosystemName, speciesName, isPlant);
    }

    @Override
    public void handleInteraction(String ecosystemName) {
        // Обрабатываем взаимодействие в экосистеме
        interactionService.handleInteraction(ecosystemName);
    }

    @Override
    public void displayPopulationPredictions(String ecosystemName) {
        // Получаем текущие условия экосистемы
        Conditions conditions = simulationService.getCurrentConditions(ecosystemName);
        // Получаем прогноз изменений популяции на основе условий
        Map<String, String> predictions = predictionService.predictPopulationChanges(conditions);
        // Отображаем предсказания для каждого вида
        predictions.forEach((species, prediction) -> {
            uiService.displayMessage(species + " " + POPULATION + ": " + prediction);
        });
    }
}
