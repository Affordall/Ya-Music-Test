package com.realjamapps.yamusicapp.models;

import java.util.List;

/**
 * Created by affy on 21.06.16.
 * https://www.javacodegeeks.com/2013/06/builder-pattern-good-for-code-great-for-tests.html
 */
public class PerformerBuilder {

    private int mId;
    private String mName;
    private List<?> mGenres;
    private int mTracks;
    private int mAlbums;
    private String mLink;
    private String mDescription;
    private String mCoverSmall;
    private String mCoverBig;

    public PerformerBuilder() {}

    public PerformerBuilder withId(Integer id) {
        this.mId = id;
        return this;
    }

    public PerformerBuilder withName(String name) {
        this.mName = name;
        return this;
    }

    public PerformerBuilder withGenres(List<?> genres) {
        this.mGenres = genres;
        return this;
    }

    public PerformerBuilder withTracks(int tracks) {
        this.mTracks = tracks;
        return this;
    }

    public PerformerBuilder withAlbums(int albums) {
        this.mAlbums = albums;
        return this;
    }

    public PerformerBuilder withLink(String link) {
        this.mLink = link;
        return this;
    }

    public PerformerBuilder withDescription(String description) {
        this.mDescription = description;
        return this;
    }

    public PerformerBuilder withCoverSmall(String coverSmall) {
        this.mCoverSmall = coverSmall;
        return this;
    }

    public PerformerBuilder withCoverBig(String coverBig) {
        this.mCoverBig = coverBig;
        return this;
    }

    public Performer build() {
        this.validate();
        return new Performer(mId, mName, mGenres, mTracks, mAlbums, mLink, mDescription, mCoverSmall, mCoverBig);
    }

    private void validate() {
        if (mName == null) {
            throw new IllegalStateException("mName is null");
        }
    }

}
