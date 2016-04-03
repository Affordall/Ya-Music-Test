package com.realjamapps.yamusicapp.models;

import java.io.Serializable;

public class Genres implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean isSelected;

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

    public Genres(int _genresID, String _genresName, boolean isSelected) {
        this.id = _genresID;
        this.name = _genresName;
        this.isSelected = isSelected;
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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
