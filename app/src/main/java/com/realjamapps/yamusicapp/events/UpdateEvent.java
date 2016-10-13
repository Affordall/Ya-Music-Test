package com.realjamapps.yamusicapp.events;

import com.realjamapps.yamusicapp.models.Performer;

import java.util.ArrayList;

public class UpdateEvent {

    public final ArrayList<Performer> list;

    public UpdateEvent(ArrayList<Performer> list) {
        this.list = list;
    }

}
