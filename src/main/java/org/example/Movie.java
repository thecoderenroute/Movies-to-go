package org.example;

public class Movie {

    String name;
    long year;
    String director;
    String plot;
    String score;

    public Movie(String name, long year, String director, String plot, String score) {
        this.name = name;
        this.year = year;
        this.director = director;
        this.plot = plot;
        this.score = score;
    }

    public String toString() {
        return this.name + " (" + this.year + ") by " + this.director + " : " + this.score + "\n\t" + this.plot;
    }
}
