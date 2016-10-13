package com.realjamapps.yamusicapp.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Genres { //extends RealmObject {

    //@PrimaryKey
    private int id;
    private String name;
    private boolean isSelected;

    public Genres() {
    }

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
     * Getter for Genres ID
     * @return String name;
     * */
    public String getName() {
        return name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
