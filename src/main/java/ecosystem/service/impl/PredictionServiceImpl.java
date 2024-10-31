package ecosystem.service.impl;

import ecosystem.model.Conditions;
import ecosystem.service.PredictionService;

import java.util.HashMap;
import java.util.Map;

// Класс PredictionServiceImpl реализует интерфейс PredictionService
public class PredictionServiceImpl implements PredictionService {
    private static PredictionServiceImpl instance;

    private PredictionServiceImpl() {
    }

    public static synchronized PredictionServiceImpl getInstance() {
        if (instance == null) {
            instance = new PredictionServiceImpl();
        }
        return instance;
    }

    @Override
    public Map<String, String> predictPopulationChanges(Conditions conditions) {
        Map<String, String> predictions = new HashMap<>();

        predictions.put("Plants", predictPlantsChange(conditions));
        predictions.put("Animals", predictAnimalsChange(conditions));

        return predictions;
    }

    private String predictPlantsChange(Conditions conditions) {
        double temperature = conditions.getTemperature();
        double humidity = conditions.getHumidity();
        double waterAmount = conditions.getWaterAmount();

        if (temperature > 35 && humidity < 30) {
            return "Significant Decrease"; // Экстремальные условия приводят к значительному снижению
        } else if (temperature > 30 && waterAmount < 20) {
            return "Decrease"; // Высокая температура и недостаток воды снижают популяцию
        } else if (temperature < 10 || humidity < 15) {
            return "Stable"; // Холод и сухость стабилизируют популяцию
        } else if (temperature > 15 && temperature <= 25 && humidity >= 50) {
            return "Increase"; // Благоприятные условия для роста растений
        } else {
            return "Stable"; // Условия незначительно влияют на популяцию
        }
    }

    private String predictAnimalsChange(Conditions conditions) {
        double temperature = conditions.getTemperature();
        double humidity = conditions.getHumidity();
        double waterAmount = conditions.getWaterAmount();

        if (temperature > 35 && waterAmount < 30) {
            return "Significant Decrease"; // Очень жарко и недостаток воды — значительное снижение
        } else if (temperature > 30 && humidity < 40) {
            return "Decrease"; // Высокая температура и низкая влажность уменьшают популяцию животных
        } else if (temperature < 10 && waterAmount > 40) {
            return "Stable"; // Низкая температура и достаточная вода — стабильная популяция
        } else if (temperature >= 20 && temperature <= 30 && humidity > 60 && waterAmount > 50) {
            return "Increase"; // Идеальные условия для увеличения популяции животных
        } else {
            return "Stable"; // Условия незначительно влияют на популяцию
        }
    }
}
