package org.finra.datagenerator.scaffolding.transformer;

/**
 * Created by dkopel on 11/1/16.
 */
public class Movie {
    private String title;

    private String director;

    public String getTitle() {
        return title;
    }

    public Movie setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDirector() {
        return director;
    }

    public Movie setDirector(String director) {
        this.director = director;
        return this;
    }
}
