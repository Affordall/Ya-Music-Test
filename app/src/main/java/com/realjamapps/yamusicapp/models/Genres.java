package com.realjamapps.yamusicapp.models;

public class Genres {

    private int id;
    private String name;

    /**
     * Create empty constructor
     * */
    public Genres() {
    }

    /**
     * Create normal constructor
     * */
    public Genres(int _genresID, String _genresName) {
        this.id = _genresID;
        this.name = _genresName;
    }

    public Genres(String _genresName) {
        this.name = _genresName;
    }

    /**
     * Getter for Genres ID
     * @return int id;
     * */
    public int getId() {
        return id;
    }

    /**
     * Setter for Genres ID
     * @param id int
     * */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Getter for Genres ID
     * @return String name;
     * */
    public String getName() {
        return name;
    }

    /**
     * Setter for Genres Name
     * @param name String
     * */
    public void setName(String name) {
        this.name = name;
    }
}
