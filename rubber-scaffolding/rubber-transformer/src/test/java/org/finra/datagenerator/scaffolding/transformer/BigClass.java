package org.finra.datagenerator.scaffolding.transformer;

/**
 * Created by dkopel on 9/27/16.
 */
public class BigClass {
    private Integer id;

    private String name;

    private Long num;

    public Integer getId() {
        return id;
    }

    public BigClass setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public BigClass setName(String name) {
        this.name = name;
        return this;
    }

    public Long getNum() {
        return num;
    }

    public BigClass setNum(Long num) {
        this.num = num;
        return this;
    }
}
