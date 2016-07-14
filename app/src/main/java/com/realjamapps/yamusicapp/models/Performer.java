package com.realjamapps.yamusicapp.models;

import java.util.List;

public class Performer  {

    private int mId;
    private String mName;
    private List<?> mGenres;
    private int mTracks;
    private int mAlbums;
    private String mLink;
    private String mDescription;
    private String mCoverSmall;
    private String mCoverBig;

    /**
     * Empty constructor
     * */
    public Performer() {
    }

    /**
     * Normal constructor for Builder
     * */
    public Performer(int _id, String _perfName, List _perfGenres, int _perfTracks,
                int _perfAlbums, String _perfUrl, String _perfDescription, String _smallCover,
                String _bigCover) {
        this.mId = _id;
        this.mName = _perfName;
        this.mGenres = _perfGenres;
        this.mTracks = _perfTracks;
        this.mAlbums = _perfAlbums;
        this.mLink = _perfUrl;
        this.mDescription = _perfDescription;
        this.mCoverSmall = _smallCover;
        this.mCoverBig = _bigCover;
    }

    /**
     * Getter for Performer ID
     * @return int mId;
     * */
    public int getmId() {
        return mId;
    }

    /**
     * Getter for Performer Name
     * @return String mName
     * */
    public String getmName() {
        return mName;
    }

    /**
     * Getter for Performer Genres
     * @return List mGenres
     * */
    public List getmGenres() {
        return mGenres;
    }

    /**
     * Getter for count Performer Tracks
     * @return int mTracks
     * */
    public int getmTracks() {
        return mTracks;
    }

    /**
     * Getter for count Performer Albums
     * @return int mAlbums
     * */
    public int getmAlbums() {
        return mAlbums;
    }

    /**
     * Getter for link on Performer
     * @return int mAlbums
     * */
    public String getmLink() {
        return mLink;
    }

    /**
     * Getter for Performer Description
     * @return String mDescription
     * */
    public String getmDescription() {
        return mDescription;
    }

    /**
     * Getter for Performer Small Cover
     * @return String mCoverSmall
     * */
    public String getmCoverSmall() {
        return mCoverSmall;
    }

    /**
     * Getter for Performer Big Cover
     * @return String mCoverBig
     * */
    public String getmCoverBig() {
        return mCoverBig;
    }

}
