package ecosystem.service.impl;

import ecosystem.model.Animal;
import ecosystem.service.UIService;
import ecosystem.util.EcosystemScanner;

import static ecosystem.util.Messages.*;

// Класс UIServiceImpl реализует интерфейс UIService
public class UIServiceImpl implements UIService {
    // Singleton instance для обеспечения единственного экземпляра репозитория симуляции
    private static UIServiceImpl instance;

    // Сканнер для чтения ввода пользователя
    private final EcosystemScanner ecosystemScanner = EcosystemScanner.getInstance();

    private UIServiceImpl() {
    }

    // Метод для получения экземпляра Singleton
    public static synchronized UIServiceImpl getInstance() {
        if (instance == null) {
            instance = new UIServiceImpl(); // Создаем новый экземпляр, если он еще не существует
        }
        return instance; // Возвращаем существующий экземпляр
    }

    // Метод для отображения главного меню
    @Override
    public int showMainMenu() {
        System.out.println(MENU + ":");
        System.out.println("1. " + CREATE_NEW_ECOSYSTEM);
        System.out.println("2. " + LOAD_EXISTING_ECOSYSTEM);
        System.out.println("3. " + EXIT_PROGRAM);
        System.out.print(CHOOSE_AN_OPERATION + ": ");
        return ecosystemScanner.getScanner().nextInt(); // Возвращаем выбор пользователя
    }

    // Метод для запроса имени экосистемы у пользователя
    @Override
    public String askForEcosystemName(boolean isNew) {
        if (isNew) {
            System.out.print(ENTER_NEW_ECOSYSTEM_NAME + ": ");
        } else {
            System.out.print(ENTER_EXISTING_ECOSYSTEM_NAME + ": ");
        }
        ecosystemScanner.getScanner().nextLine(); // Очищаем буфер
        return ecosystemScanner.getScanner().nextLine(); // Возвращаем введенное имя
    }

    // Метод для отображения меню действий с экосистемой
    @Override
    public int showActionMenu() {
        System.out.println(MANAGING_ECOSYSTEM + ": ");
        System.out.println("1. " + ADD_PLANT);
        System.out.println("2. " + ADD_ANIMAL);
        System.out.println("3. " + EXIT_TO_MAIN_MENU);
        System.out.println("4. " + UPDATE_ANIMAL_DIET);
        System.out.println("5. " + DELETE_SPECIES);
        System.out.println("6. " + INTERACTION_BETWEEN_SPECIES);
        System.out.println("7. " + PREDICTION);
        System.out.print(CHOOSE_AN_OPERATION + ": ");
        return ecosystemScanner.getScanner().nextInt(); // Возвращаем выбор пользователя
    }

    // Метод для запроса имени растения у пользователя
    @Override
    public String askForPlantName() {
        ecosystemScanner.getScanner().nextLine(); // Очищаем буфер
        String plantName;

        // Проверяем, чтобы имя растения соответствовало заданному шаблону
        while (true) {
            System.out.print(ENTER_PLANT_NAME + ": ");
            plantName = ecosystemScanner.getScanner().nextLine();
            if (plantName.matches("[a-zA-Zа-яА-Я]+")) { // Шаблон для проверки имени
                break; // Выход из цикла, если имя корректное
            } else {
                System.out.println(INCORRECT_NAME); // Сообщение об ошибке
            }
        }
        return plantName; // Возвращаем введенное имя растения
    }

    // Метод для запроса деталей о животном у пользователя
    @Override
    public Animal askForAnimalDetails() {
        ecosystemScanner.getScanner().nextLine(); // Очищаем буфер
        String animalName;
        String dietType;

        // Проверяем, чтобы имя животного соответствовало заданному шаблону
        while (true) {
            System.out.print(ENTER_ANIMAL_NAME + ": ");
            animalName = ecosystemScanner.getScanner().nextLine();
            if (animalName.matches("[a-zA-Zа-яА-Я]+")) { // Шаблон для проверки имени
                break; // Выход из цикла, если имя корректное
            } else {
                System.out.println(INCORRECT_NAME); // Сообщение об ошибке
            }
        }
        // Проверяем, чтобы тип диеты соответствовал заданным значениям
        while (true) {
            System.out.print(ENTER_DIET_TYPE + ": ");
            dietType = ecosystemScanner.getScanner().nextLine();
            if (dietType.matches("herbivore|carnivore|omnivore")) { // Разрешенные типы диеты
                break; // Выход из цикла, если тип диеты корректный
            } else {
                System.out.println(INCORECT_TYPE_OF_DIET); // Сообщение об ошибке
            }
        }

        return new Animal(animalName, dietType); // Возвращаем объект Animal с введенными данными
    }

    // Метод для отображения сообщения
    @Override
    public void displayMessage(String message) {
        System.out.println(message); // Печатаем сообщение
    }

    // Метод для запроса имени вида для удаления
    @Override
    public String askForSpeciesName(boolean isPlant) {
        if (isPlant) {
            System.out.print(ENTER_PLANT_TO_REMOVE + ": ");
        } else {
            System.out.print(ENTER_ANIMAL_TO_REMOVE + ": ");
        }
        ecosystemScanner.getScanner().nextLine(); // Очищаем буфер
        return ecosystemScanner.getScanner().nextLine(); // Возвращаем введенное имя
    }

    // Метод для запроса нового типа диеты у пользователя
    @Override
    public String askForNewDiet() {
        System.out.print(ENTER_NEW_DIET + ": ");
        return ecosystemScanner.getScanner().nextLine(); // Возвращаем введенный тип диеты
    }

    // Метод для запроса имени животного для обновления
    @Override
    public String askForAnimalNameToUpdate() {
        System.out.print(ENTER_ANIMAL_NAME_TO_UPDATE + ": ");
        ecosystemScanner.getScanner().nextLine(); // Очищаем буфер
        return ecosystemScanner.getScanner().nextLine(); // Возвращаем введенное имя
    }

    // Метод для запроса типа удаляемого вида (растение или животное)
    @Override
    public boolean askIsPlant() {
        System.out.println(DELETE_WHAT);
        System.out.println("1. " + PLANT);
        System.out.println("2. " + ANIMAL);
        System.out.print(CHOOSE_AN_OPERATION + ": ");
        int choice = ecosystemScanner.getScanner().nextInt(); // Считываем выбор пользователя
        return choice == 1; // Возвращаем true, если выбрано растение
    }

    // Метод для запроса имени хищника у пользователя
    @Override
    public String askForPredator() {
        System.out.print(ENTER_PREDATOR_NAME + ": ");
        ecosystemScanner.getScanner().nextLine(); // Очищаем буфер
        return ecosystemScanner.getScanner().nextLine(); // Возвращаем введенное имя
    }

    // Метод для запроса имени жертвы у пользователя
    @Override
    public String askForPrey() {
        System.out.print(ENTER_PREY_NAME + ": ");
        return ecosystemScanner.getScanner().nextLine(); // Возвращаем введенное имя
    }

    // Метод для запроса температуры у пользователя
    @Override
    public double askForTemperature() {
        System.out.print(ENTER_TEMPERATURE + ": ");
        return ecosystemScanner.getScanner().nextDouble(); // Возвращаем введенное значение температуры
    }

    // Метод для запроса влажности у пользователя
    @Override
    public double askForHumidity() {
        System.out.print(ENTER_HUMIDITY + ": ");
        return ecosystemScanner.getScanner().nextDouble(); // Возвращаем введенное значение влажности
    }

    // Метод для запроса доступного количества воды у пользователя
    @Override
    public double askForAvailableWater() {
        System.out.print(ENTER_AVAILABLE_WATER + ": ");
        return ecosystemScanner.getScanner().nextDouble(); // Возвращаем введенное значение доступной воды
    }
}
