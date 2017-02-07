package org.finra.datagenerator.scaffolding.transformer;

/**
 * Created by dkopel on 11/1/16.
 */
public class Worker extends Person {
    private String occupation;

    public String getOccupation() {
        return occupation;
    }

    public Worker setOccupation(String occupation) {
        this.occupation = occupation;
        return this;
    }
}
