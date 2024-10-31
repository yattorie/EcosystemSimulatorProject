package ecosystem.model;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class Animal extends Species {
    private String dietType;

    public Animal(String name, String dietType) {
        super(name, "animal");
        this.dietType = dietType;
    }
}
