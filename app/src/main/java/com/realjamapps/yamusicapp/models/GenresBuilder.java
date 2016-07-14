package com.realjamapps.yamusicapp.models;

/**
 * Created by affy on 07.07.16.
 * https://www.javacodegeeks.com/2013/06/builder-pattern-good-for-code-great-for-tests.html
 *
 * Builder class for Performer model.
 */
public class GenresBuilder {

    private int id;
    private String name;
    private boolean isSelected;

    public GenresBuilder() {}

    public GenresBuilder withId(int id) {
        this.id = id;
        return this;
    }

    public GenresBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public GenresBuilder isSelected(boolean isSelected) {
        this.isSelected = isSelected;
        return this;
    }

    public Genres build() {
        this.validate();
        return new Genres(id, name);
    }

    public Genres buildWithSelected() {
        this.validate();
        return new Genres(id, name, isSelected);
    }

    private void validate() {
//        if (name == null) {
//            throw new IllegalStateException("name is null");
//        }
    }

}
