package com.realjamapps.yamusicapp.listeners;

import com.realjamapps.yamusicapp.models.Genres;

import java.util.ArrayList;

public interface GenresListener {
    void addGenres(Genres genres);
    ArrayList<Genres> getAllGenres();
    int getGenresCount();
}


