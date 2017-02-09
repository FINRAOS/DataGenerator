package org.finra.datagenerator.scaffolding.transformer;

/**
 * Created by dkopel on 11/1/16.
 */
public class Person {
    private String name;

    private Integer age;

    private Gender gender;

    public String getName() {
        return name;
    }

    public Person setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getAge() {
        return age;
    }

    public Person setAge(Integer age) {
        this.age = age;
        return this;
    }

    public Gender getGender() {
        return gender;
    }

    public Person setGender(Gender gender) {
        this.gender = gender;
        return this;
    }
}
