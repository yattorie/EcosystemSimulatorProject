package ecosystem.service.impl;

import ecosystem.model.Conditions;
import ecosystem.repository.SimulationRepository;
import ecosystem.repository.impl.SimulationRepositoryImpl;
import ecosystem.service.SimulationService;
import ecosystem.service.UIService;

import static ecosystem.util.Messages.*;

// Класс SimulationServiceImpl реализует интерфейс SimulationService
public class SimulationServiceImpl implements SimulationService {
    // Singleton instance для обеспечения единственного экземпляра репозитория симуляции
    private static SimulationServiceImpl instance;

    // Репозиторий для работы с данными симуляции и пользовательский интерфейс
    private final SimulationRepository simulationRepository = SimulationRepositoryImpl.getInstance();
    private final UIService uiService = UIServiceImpl.getInstance();

    private SimulationServiceImpl() {
    }

    // Метод для получения экземпляра Singleton
    public static synchronized SimulationServiceImpl getInstance() {
        if (instance == null) {
            instance = new SimulationServiceImpl(); // Создаем новый экземпляр, если он еще не существует
        }
        return instance; // Возвращаем существующий экземпляр
    }

    // Метод для получения текущих условий экосистемы по ее имени
    @Override
    public Conditions getCurrentConditions(String ecosystemName) {
        return simulationRepository.readEcosystemConditions(ecosystemName); // Читаем условия экосистемы из репозитория
    }

    // Метод для создания новой экосистемы
    @Override
    public String createEcosystem() {
        String ecosystemName = uiService.askForEcosystemName(true); // Запрашиваем имя экосистемы у пользователя

        // Проверяем, существует ли экосистема с таким именем
        if (simulationRepository.ecosystemExists(ecosystemName)) {
            uiService.displayMessage(THIS_ECOSYSTEM_ALREADY_EXISTS); // Сообщаем, если экосистема уже существует
            return null; // Возвращаем null, если экосистема уже есть
        }

        simulationRepository.createNewSimulation(ecosystemName); // Создаем новую симуляцию с заданным именем

        // Запрашиваем у пользователя параметры экосистемы
        Conditions conditions = new Conditions(
                uiService.askForTemperature(), // Запрашиваем температуру
                uiService.askForHumidity(),    // Запрашиваем влажность
                uiService.askForAvailableWater() // Запрашиваем доступное количество воды
        );

        simulationRepository.saveEcosystemParameters(ecosystemName, conditions); // Сохраняем параметры экосистемы в репозитории
        uiService.displayMessage(ECOSYSTEM_CREATE); // Уведомляем пользователя о создании экосистемы
        return ecosystemName; // Возвращаем имя созданной экосистемы
    }

    // Метод для загрузки существующей экосистемы
    @Override
    public String loadEcosystem() {
        String ecosystemName = uiService.askForEcosystemName(false); // Запрашиваем имя экосистемы у пользователя
        simulationRepository.loadSimulation(ecosystemName); // Загружаем симуляцию из репозитория
        uiService.displayMessage(ECOSYSTEM_LOADED); // Уведомляем пользователя о загрузке экосистемы
        return ecosystemName; // Возвращаем имя загруженной экосистемы
    }

}
