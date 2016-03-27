package com.realjamapps.yamusicapp.listeners;

import com.realjamapps.yamusicapp.models.Performer;

import java.util.ArrayList;

public interface PerformersListener {
    void addPerformer(Performer perf);

    ArrayList<Performer> getAllPerformers();

    int getPerformersCount();
}
