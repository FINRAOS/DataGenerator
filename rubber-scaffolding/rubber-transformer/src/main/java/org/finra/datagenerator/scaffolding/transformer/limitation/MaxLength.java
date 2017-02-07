package org.finra.datagenerator.scaffolding.transformer.limitation;

/**
 * Created by dkopel on 11/22/16.
 */
public class MaxLength implements Limitation<String> {
    private final int maxLength;

    public MaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public boolean isValid(String input) {
        return input.length() <= maxLength;
    }
}
