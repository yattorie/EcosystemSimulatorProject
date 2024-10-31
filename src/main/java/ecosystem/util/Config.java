package ecosystem.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

// Импортируем сообщения об ошибках для удобства обработки исключений
import static ecosystem.util.Messages.RESOURCE_DIRECTORY_IS_NOT_SET;
import static ecosystem.util.Messages.UNABLE_TO_FIND_PROPERTIES;

public class Config {
    // Объект Properties для хранения пар ключ-значение из файла свойств
    private static final Properties properties = new Properties();

    // Статический блок для загрузки свойств при загрузке класса
    static {
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream("application.properties")) {
            // Пытаемся найти файл свойств в класспасе
            if (input == null) {
                // Если файл не найден, выбрасываем IOException с конкретным сообщением
                throw new IOException(UNABLE_TO_FIND_PROPERTIES);
            }
            // Загружаем свойства из входного потока в объект Properties
            properties.load(input);
        } catch (IOException ex) {
            // Выводим стек вызовов для любого IOException, возникшего во время загрузки
            ex.printStackTrace();
        }
    }

    // Метод для получения значения свойства по его ключу
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    // Метод для получения пути к директории из свойств
    public static String getDirectory() {
        // Получаем путь к директории, используя метод getProperty
        String directory = getProperty("directory.path");
        // Если путь к директории равен null, выбрасываем IllegalStateException с конкретным сообщением
        if (directory == null) {
            throw new IllegalStateException(RESOURCE_DIRECTORY_IS_NOT_SET);
        }
        // Возвращаем путь к директории
        return directory;
    }
}
