package org.finra.datagenerator.scaffolding.transformer;

/**
 * Created by dkopel on 11/1/16.
 */
public class Mailman extends Worker {
    public Mailman() {
        setOccupation("mailman");
    }

    private String route;

    public String getRoute() {
        return route;
    }

    public Mailman setRoute(String route) {
        this.route = route;
        return this;
    }
}
