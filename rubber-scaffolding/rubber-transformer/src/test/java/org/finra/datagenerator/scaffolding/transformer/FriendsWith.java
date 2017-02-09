package org.finra.datagenerator.scaffolding.transformer;

import java.time.LocalDateTime;

/**
 * Created by dkopel on 11/2/16.
 */
public class FriendsWith {
    private String location;

    private LocalDateTime met;

    public String getLocation() {
        return location;
    }

    public FriendsWith setLocation(String location) {
        this.location = location;
        return this;
    }

    public LocalDateTime getMet() {
        return met;
    }

    public FriendsWith setMet(LocalDateTime met) {
        this.met = met;
        return this;
    }
}
