package org.finra.datagenerator.input;

public enum CollectionPropertiesENUM {

    setPropertyCollection(0), setPropertyCollectionAllCombos(1), setPropertyCollectionPairwise(2);

    private final int value;

    private CollectionPropertiesENUM(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }

}
