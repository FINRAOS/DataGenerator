package org.finra.datagenerator.scaffolding.random;

import java.util.UUID;

/**
 * Created by dkopel on 11/28/16.
 */
public class Food {
    private Blah<Buffet<String>> myGeneric;

    private UUID id;

    private float num;

    public Blah<Buffet<String>> getMyGeneric() {
        return myGeneric;
    }

    public UUID getId() {
        return id;
    }

    public Food setId(UUID id) {
        this.id = id;
        return this;
    }

    public float getNum() {
        return num;
    }

    public Food setNum(float num) {
        this.num = num;
        return this;
    }
}
