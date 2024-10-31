package ecosystem.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class Conditions {
    private double temperature;
    private double humidity;
    private double waterAmount;
}
