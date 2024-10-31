package ecosystem.api;

import ecosystem.service.ActionHandlerService;
import ecosystem.service.SimulationService;
import ecosystem.service.UIService;
import ecosystem.service.impl.ActionHandlerServiceImpl;
import ecosystem.service.impl.SimulationServiceImpl;
import ecosystem.service.impl.UIServiceImpl;
import static ecosystem.util.Messages.*;

public class Ecosystem {

    // Создаем экземпляры сервисов, используя паттерн Singleton
    private final SimulationService simulationService = SimulationServiceImpl.getInstance();
    private final UIService uiService = UIServiceImpl.getInstance();
    private final ActionHandlerService actionHandler = ActionHandlerServiceImpl.getInstance();

    // Метод для запуска экосистемы
    public void startEcoSystem() {
        String ecosystemName = "";

        uiService.displayMessage(WELCOME_MESSAGE);

        // Основной цикл для отображения меню и обработки выбора пользователя
        while (true) {
            int choice = uiService.showMainMenu();

            switch (choice) {
                case 1:
                    // Создание новой экосистемы
                    ecosystemName = simulationService.createEcosystem();
                    break;
                case 2:
                    // Загрузка существующей экосистемы
                    ecosystemName = simulationService.loadEcosystem();
                    break;
                case 3:
                    // Выход из программы
                    uiService.displayMessage(EXIT_PROGRAM);
                    return;
                default:
                    // Обработка некорректного выбора
                    uiService.displayMessage(INCORRECT_SELECTION);
            }
            // Проверяем, была ли экосистема успешно создана или загружена
            if (ecosystemName != null && !ecosystemName.isEmpty()) {
                manageEcosystem(ecosystemName);
            } else {
                // Сообщаем о неудаче в создании или загрузке экосистемы
                uiService.displayMessage(FAILED_TO_LOAD_OR_CREATE_ECOSYSTEM);
            }
        }
    }

    // Метод для управления экосистемой после ее создания или загрузки
    private void manageEcosystem(String ecosystemName) {
        boolean continueManaging = true;

        // Цикл для отображения меню управления экосистемой
        while (continueManaging) {
            int actionChoice = uiService.showActionMenu();

            switch (actionChoice) {
                case 1:
                    // Добавление растения
                    actionHandler.addPlant(ecosystemName);
                    break;
                case 2:
                    // Добавление животного
                    actionHandler.addAnimal(ecosystemName);
                    break;
                case 3:
                    // Выход из меню управления
                    continueManaging = false;
                    break;
                case 4:
                    // Обновление диеты животного
                    actionHandler.updateAnimalDiet(ecosystemName);
                    break;
                case 5:
                    // Удаление вида
                    actionHandler.deleteSpecies(ecosystemName);
                    break;
                case 6:
                    // Обработка взаимодействия между видами
                    actionHandler.handleInteraction(ecosystemName);
                    break;
                case 7:
                    // Вывод прогноза популяции
                    actionHandler.displayPopulationPredictions(ecosystemName);
                    break;
                default:
                    // Обработка некорректного выбора
                    uiService.displayMessage(INCORRECT_SELECTION);
            }
        }
    }
}
