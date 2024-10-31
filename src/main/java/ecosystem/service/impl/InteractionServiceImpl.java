package ecosystem.service.impl;

import ecosystem.repository.SpeciesRepository;
import ecosystem.repository.impl.SpeciesRepositoryImpl;
import ecosystem.service.InteractionService;
import ecosystem.service.UIService;

import static ecosystem.util.Messages.*;

// Класс InteractionServiceImpl реализует интерфейс InteractionService
public class InteractionServiceImpl implements InteractionService {
    // Singleton instance для обеспечения единственного экземпляра репозитория симуляции
    private static InteractionServiceImpl instance;

    // Инициализируем сервисы для взаимодействия с пользователем и репозиторием видов
    private final UIService uiService = UIServiceImpl.getInstance();
    private final SpeciesRepository speciesRepository = SpeciesRepositoryImpl.getInstance(); // Репозиторий для работы с видами

    private InteractionServiceImpl() {
    }

    // Метод для получения экземпляра Singleton
    public static synchronized InteractionServiceImpl getInstance() {
        if (instance == null) {
            instance = new InteractionServiceImpl(); // Создаем новый экземпляр, если он еще не существует
        }
        return instance; // Возвращаем существующий экземпляр
    }

    @Override
    public void handleInteraction(String ecosystemName) {
        // Запрашиваем у пользователя имя хищника и жертвы
        String predatorName = uiService.askForPredator();
        String preyName = uiService.askForPrey();

        // Проверяем, может ли травоядное съесть растение
        if (canHerbivoreEatPlant(ecosystemName, predatorName, preyName)) {
            performHerbivoreEatsPlant(ecosystemName, predatorName, preyName);
            // Проверяем, может ли хищник съесть травоядное
        } else if (canCarnivoreEatHerbivore(ecosystemName, predatorName, preyName)) {
            performCarnivoreEatsHerbivore(ecosystemName, predatorName, preyName);
        } else if (canOmnivoreEat(ecosystemName, predatorName, preyName)) {
            performOmnivoreEats(ecosystemName, predatorName, preyName);
        } else {
            // Если взаимодействие невозможно, отображаем сообщение
            uiService.displayMessage(INTERACTION_IS_NOT_POSSIBLE + ": " + predatorName + " " + CANT_EAT + " " + preyName);
        }
    }

    // Метод для проверки, может ли травоядное съесть растение
    private boolean canHerbivoreEatPlant(String ecosystemName, String predator, String prey) {
        // Проверяем, является ли предатор травоядным и жертва растением
        return speciesRepository.checkIfHerbivore(ecosystemName, predator) && speciesRepository.checkIfPlant(ecosystemName, prey);
    }

    // Метод для проверки, может ли хищник съесть травоядное
    private boolean canCarnivoreEatHerbivore(String ecosystemName, String predator, String prey) {
        // Проверяем, является ли предатор хищником и жертва травоядным
        return speciesRepository.checkIfCarnivore(ecosystemName, predator) && speciesRepository.checkIfHerbivore(ecosystemName, prey);
    }

    // Метод для проверки, может ли всеядное съесть жертву (растение или животное)
    private boolean canOmnivoreEat(String ecosystemName, String predator, String prey) {
        // Проверяем, является ли предатор всеядным и жертва растением или животным
        return speciesRepository.checkIfOmnivore(ecosystemName, predator) &&
                (speciesRepository.checkIfPlant(ecosystemName, prey) || speciesRepository.checkIfHerbivore(ecosystemName, prey) || speciesRepository.checkIfCarnivore(ecosystemName, prey));
    }

    // Метод, выполняющий действие "травоядное ест растение"
    private void performHerbivoreEatsPlant(String ecosystemName, String predator, String prey) {
        // Удаляем растение из экосистемы
        speciesRepository.deleteSpecies(ecosystemName, prey, true);
        // Отображаем сообщение об успешном взаимодействии
        uiService.displayMessage(HERBIVORE + " " + predator + " " + ATE_A_PLANT + " " + prey);
        // Регистрируем взаимодействие в репозитории
        speciesRepository.recordInteraction(predator + " " + ATE + " " + prey, ecosystemName);
    }

    // Метод, выполняющий действие "хищник ест травоядное"
    private void performCarnivoreEatsHerbivore(String ecosystemName, String predator, String prey) {
        // Удаляем травоядное из экосистемы
        speciesRepository.deleteSpecies(ecosystemName, prey, false);
        // Отображаем сообщение об успешном взаимодействии
        uiService.displayMessage(PREDATOR + " " + predator + " " + ATE_A_HERBIVORE + " " + prey);
        // Регистрируем взаимодействие в репозитории
        speciesRepository.recordInteraction(predator + " " + ATE + " " + prey, ecosystemName);
    }

    // Метод для выполнения действия "всеядное ест растение или животное"
    private void performOmnivoreEats(String ecosystemName, String predator, String prey) {
        // Удаляем жертву из экосистемы
        boolean isPlant = speciesRepository.checkIfPlant(ecosystemName, prey);
        speciesRepository.deleteSpecies(ecosystemName, prey, isPlant);

        // Отображаем сообщение об успешном взаимодействии
        uiService.displayMessage(OMNIVORE + " " + predator + " " + ATE + " " + prey);

        // Регистрируем взаимодействие в репозитории
        speciesRepository.recordInteraction(predator + " " + ATE + " " + prey, ecosystemName);
    }
}
