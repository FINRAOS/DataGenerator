package org.finra.datagenerator.scaffolding.transformer;

/**
 * Created by dkopel on 11/1/16.
 */
public class Food {
    private String label;

    private Integer calories;

    public String getLabel() {
        return label;
    }

    public Food setLabel(String label) {
        this.label = label;
        return this;
    }

    public Integer getCalories() {
        return calories;
    }

    public Food setCalories(Integer calories) {
        this.calories = calories;
        return this;
    }
}
