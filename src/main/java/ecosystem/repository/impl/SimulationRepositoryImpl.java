package ecosystem.repository.impl;

import ecosystem.model.Conditions;
import ecosystem.repository.SimulationRepository;
import ecosystem.service.UIService;
import ecosystem.service.impl.UIServiceImpl;
import ecosystem.util.Config;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static ecosystem.util.Messages.*;

public class SimulationRepositoryImpl implements SimulationRepository {
    // Singleton instance для обеспечения единственного экземпляра репозитория симуляции
    private static SimulationRepositoryImpl instance;
    private final UIService uiService = UIServiceImpl.getInstance();

    // Приватный конструктор для синглтона
    private SimulationRepositoryImpl() {}

    // Метод для получения экземпляра Singleton
    public static synchronized SimulationRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new SimulationRepositoryImpl(); // Создаем новый экземпляр, если он еще не существует
        }
        return instance; // Возвращаем существующий экземпляр
    }

    // Возвращает путь к директории симуляции на основе имени экосистемы
    private String getEcosystemDirectory(String ecosystemName) {
        return Config.getDirectory() + ecosystemName;
    }

    // Создает новую симуляцию с указанным именем
    @Override
    public void createNewSimulation(String ecosystemName) {
        Path ecosystemDirPath = Paths.get(getEcosystemDirectory(ecosystemName));
        createDirectoryIfNotExists(ecosystemDirPath);
        createSimulationFiles(ecosystemDirPath);
    }

    // Создает директорию для симуляции, если она не существует
    private void createDirectoryIfNotExists(Path ecosystemDirPath) {
        try {
            if (!Files.exists(ecosystemDirPath)) {
                Files.createDirectories(ecosystemDirPath);
            }
        } catch (IOException e) {
            uiService.displayMessage(ERROR_CREATING_ECOSYSTEM + ": " + e.getMessage());
        }
    }

    // Создает файлы симуляции (для растений, животных, взаимодействий и условий)
    private void createSimulationFiles(Path dirPath) {
        List<String> files = List.of(
                Config.getProperty("plants.file"),
                Config.getProperty("animals.file"),
                Config.getProperty("interactions.file"),
                Config.getProperty("resource.file")
        );
        for (String file : files) {
            createFileIfNotExists(dirPath.resolve(file));
        }
    }

    // Создает файл, если он не существует
    private void createFileIfNotExists(Path filePath) {
        try {
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            uiService.displayMessage(ERROR_CREATING_FILE + " " + filePath.getFileName() + ": " + e.getMessage());
        }
    }

    // Загружает симуляцию, отображая содержимое файлов для растений и животных
    @Override
    public void loadSimulation(String ecosystemName) {
        displayFileContent(ecosystemName, Config.getProperty("plants.file"), PLANTS_IN_THE_ECOSYSTEM);
        displayFileContent(ecosystemName, Config.getProperty("animals.file"), ANIMALS_IN_THE_ECOSYSTEM);
    }

    // Отображает содержимое заданного файла с заголовком
    private void displayFileContent(String ecosystemName, String fileName, String headerMessage) {
        Path filePath = Paths.get(getEcosystemDirectory(ecosystemName), fileName);
        uiService.displayMessage(headerMessage + ecosystemName + ": ");
        readFileContent(filePath);
    }

    // Читает содержимое файла и выводит его на экран
    private void readFileContent(Path filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                uiService.displayMessage(line);
            }
        } catch (IOException e) {
            uiService.displayMessage(ERROR_WHILE_READING_A_FILE + ": " + e.getMessage());
        }
    }

    // Сохраняет параметры экосистемы (температура, влажность, количество воды) в файл
    @Override
    public void saveEcosystemParameters(String ecosystemName, Conditions conditions) {
        Path resourceFilePath = Paths.get(getEcosystemDirectory(ecosystemName), Config.getProperty("resource.file"));
        writeConditionsToFile(resourceFilePath, conditions);
    }

    // Записывает условия экосистемы в файл
    private void writeConditionsToFile(Path filePath, Conditions conditions) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile()))) {
            writer.write(TEMPERATURE + ": " + conditions.getTemperature() + "\n");
            writer.write(HUMIDITY + ": " + conditions.getHumidity() + "\n");
            writer.write(AVAILABLE_WATER + ": " + conditions.getWaterAmount() + "\n");
        } catch (IOException e) {
            uiService.displayMessage(ERROR_WHEN_RECORDING_PARAMETRS + ": " + e.getMessage());
        }
    }

    // Читает параметры экосистемы из файла и возвращает их как объект Conditions
    @Override
    public Conditions readEcosystemConditions(String ecosystemName) {
        Path resourceFilePath = Paths.get(getEcosystemDirectory(ecosystemName), Config.getProperty("resource.file"));
        return parseConditionsFromFile(resourceFilePath);
    }

    // Проверяет, существует ли директория экосистемы
    @Override
    public boolean ecosystemExists(String ecosystemName) {
        Path ecosystemDirPath = Paths.get(getEcosystemDirectory(ecosystemName));
        return Files.exists(ecosystemDirPath);
    }

    // Парсит условия экосистемы из файла, возвращая значения температуры, влажности и воды
    private Conditions parseConditionsFromFile(Path filePath) {
        double temperature = 0.0;
        double humidity = 0.0;
        double waterAmount = 0.0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(TEMPERATURE)) {
                    temperature = parseParameter(line);
                } else if (line.startsWith(HUMIDITY)) {
                    humidity = parseParameter(line);
                } else if (line.startsWith(AVAILABLE_WATER)) {
                    waterAmount = parseParameter(line);
                }
            }
        } catch (IOException e) {
            uiService.displayMessage(ERROR_WHILE_READING_A_FILE + ": " + e.getMessage());
        }

        return new Conditions(temperature, humidity, waterAmount);
    }

    // Разбирает строку параметра и возвращает числовое значение после двоеточия
    private double parseParameter(String line) {
        return Double.parseDouble(line.split(": ")[1]);
    }
}
