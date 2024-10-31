package ecosystem.repository.impl;

import ecosystem.model.Animal;
import ecosystem.model.Plant;
import ecosystem.repository.SpeciesRepository;
import ecosystem.service.UIService;
import ecosystem.service.impl.UIServiceImpl;
import ecosystem.util.Config;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static ecosystem.util.Messages.*;

public class SpeciesRepositoryImpl implements SpeciesRepository {
    // Singleton instance для обеспечения единственного экземпляра репозитория видов
    private static SpeciesRepositoryImpl instance;
    private final UIService uiService = UIServiceImpl.getInstance();

    // Приватный конструктор для синглтона
    private SpeciesRepositoryImpl() {
    }

    // Метод для получения экземпляра Singleton
    public static synchronized SpeciesRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new SpeciesRepositoryImpl(); // Создаем новый экземпляр, если он еще не существует
        }
        return instance; // Возвращаем существующий экземпляр
    }

    // Возвращает путь к директории конкретной экосистемы по ее названию
    private String getEcosystemDirectory(String ecosystemName) {
        return Config.getDirectory() + ecosystemName;
    }

    // Добавляет растение в файл растений текущей экосистемы
    @Override
    public void addPlant(String ecosystemName, Plant plant) {
        addSpecies(ecosystemName, plant.getName(), Config.getProperty("plants.file"));
    }

    // Добавляет животное в файл животных текущей экосистемы, включая тип диеты
    @Override
    public void addAnimal(String ecosystemName, Animal animal) {
        addSpecies(ecosystemName, animal.getName() + " (" + animal.getDietType() + ")", Config.getProperty("animals.file"));
    }

    // Добавляет вид (растение или животное) в файл с проверкой на существование файла
    private void addSpecies(String ecosystemName, String speciesName, String fileName) {
        Path filePath = Paths.get(getEcosystemDirectory(ecosystemName), fileName);
        createFileIfNotExists(filePath);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile(), true))) {
            writer.write(speciesName);
            writer.newLine();
            uiService.displayMessage(speciesName + " " + ADDED_TO_ECOSYSTEM + " " + ecosystemName);
        } catch (IOException e) {
            uiService.displayMessage(ERROR_ADDING_SPECIES + ": " + e.getMessage());
        }
    }

    // Удаляет вид из файла, обновляя его, исключая строки с именем вида
    @Override
    public void deleteSpecies(String ecosystemName, String speciesName, boolean isPlant) {
        Path filePath = Paths.get(getEcosystemDirectory(ecosystemName), isPlant ? Config.getProperty("plants.file") : Config.getProperty("animals.file"));

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            List<String> updatedLines = reader.lines()
                    .filter(line -> !(line.equals(speciesName) || line.startsWith(speciesName + " (")))
                    .collect(Collectors.toList());

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile(), false))) {
                for (String line : updatedLines) {
                    writer.write(line);
                    writer.newLine();
                }
                uiService.displayMessage(SPECIE + " " + speciesName + " " + REMOVED_FROM_THE_ECOSYSTEM + " " + ecosystemName);
            }
        } catch (IOException e) {
            uiService.displayMessage(ERROR_WHEN_DELETING_A_SPECIE + ": " + e.getMessage());
        }
    }

    // Обновляет тип диеты для животного в файле животных
    @Override
    public void updateAnimalDiet(String ecosystemName, String animalName, String newDietType) {
        Path animalsFilePath = Paths.get(getEcosystemDirectory(ecosystemName), Config.getProperty("animals.file"));

        try (BufferedReader reader = new BufferedReader(new FileReader(animalsFilePath.toFile()))) {
            List<String> updatedLines = reader.lines()
                    .map(line -> line.startsWith(animalName + " (") ? animalName + " (" + newDietType + ")" : line)
                    .collect(Collectors.toList());

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(animalsFilePath.toFile(), false))) {
                for (String line : updatedLines) {
                    writer.write(line);
                    writer.newLine();
                }
                uiService.displayMessage(ANIMAL_DIET + " " + animalName + " " + UPDATED_TO + " " + newDietType);
            }
        } catch (IOException e) {
            uiService.displayMessage(ERROR_UPDATING_ANIMAL_DIET + ": " + e.getMessage());
        }
    }

    // Проверяет, является ли животное травоядным, по имени и типу диеты
    @Override
    public boolean checkIfHerbivore(String ecosystemName, String animalName) {
        return checkAnimalDiet(ecosystemName, animalName, "herbivore");
    }

    // Проверяет, является ли животное хищником, по имени и типу диеты
    @Override
    public boolean checkIfCarnivore(String ecosystemName, String animalName) {
        return checkAnimalDiet(ecosystemName, animalName, "carnivore");
    }

    @Override
    public boolean checkIfOmnivore(String ecosystemName, String speciesName) {
        return checkAnimalDiet(ecosystemName, speciesName, "omnivore");
    }

    // Универсальный метод для проверки типа диеты животного
    private boolean checkAnimalDiet(String ecosystemName, String animalName, String dietType) {
        Path animalsFilePath = Paths.get(getEcosystemDirectory(ecosystemName), Config.getProperty("animals.file"));

        try (BufferedReader reader = new BufferedReader(new FileReader(animalsFilePath.toFile()))) {
            return reader.lines()
                    .anyMatch(line -> line.startsWith(animalName) && line.contains(dietType));
        } catch (IOException e) {
            uiService.displayMessage(ERROR_CHECKING_ANIMAL_DIET + ": " + e.getMessage());
            return false;
        }
    }

    // Проверяет, является ли вид растением
    @Override
    public boolean checkIfPlant(String ecosystemName, String speciesName) {
        Path plantsFilePath = Paths.get(getEcosystemDirectory(ecosystemName), Config.getProperty("plants.file"));

        try (BufferedReader reader = new BufferedReader(new FileReader(plantsFilePath.toFile()))) {
            return reader.lines().anyMatch(line -> line.contains(speciesName));
        } catch (IOException e) {
            uiService.displayMessage(ERROR_WHEN_CHECKING_A_PLANT + ": " + e.getMessage());
            return false;
        }
    }

    // Создает файл, если он не существует
    private void createFileIfNotExists(Path filePath) {
        try {
            Files.createDirectories(filePath.getParent());
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            uiService.displayMessage(ERROR_CREATING_FILE + ": " + e.getMessage());
        }
    }

    // Записывает взаимодействие (например, взаимодействие между видами) в файл
    @Override
    public void recordInteraction(String interaction, String ecosystemName) {
        Path interactionsFilePath = Paths.get(getEcosystemDirectory(ecosystemName), Config.getProperty("interactions.file"));
        createFileIfNotExists(interactionsFilePath);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(interactionsFilePath.toFile(), true))) {
            writer.write(interaction);
            writer.newLine();
            System.out.println(INTERACTION_RECORDED + ": " + interaction);
        } catch (IOException e) {
            uiService.displayMessage(ERROR_RECORDING_INTERACTION + ": " + e.getMessage());
        }
    }
}
