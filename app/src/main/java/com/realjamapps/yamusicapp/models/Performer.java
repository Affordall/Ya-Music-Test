package com.realjamapps.yamusicapp.models;

import java.util.List;

public class Performer {

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
     * Create empty constructor
     * */
    public Performer() {
    }

    /**
     * Create normal constructor
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
     * Setter for Performer ID
     * @param mId int
     * */
    public void setmId(int mId) {
        this.mId = mId;
    }

    /**
     * Getter for Performer Name
     * @return String mName
     * */
    public String getmName() {
        return mName;
    }

    /**
     * Setter for Performer Name
     * @param mName String
     * */
    public void setmName(String mName) {
        this.mName = mName;
    }

    /**
     * Getter for Performer Genres
     * @return List mGenres
     * */
    public List getmGenres() {
        return mGenres;
    }

    /**
     * Setter for Performer Genres
     * @param mGenres List
     * */
    public void setmGenres(List<?> mGenres) {
        this.mGenres = mGenres;
    }

    /**
     * Getter for count Performer Tracks
     * @return int mTracks
     * */
    public int getmTracks() {
        return mTracks;
    }

    /**
     * Setter for count Performer Tracks
     * @param mTracks int
     * */
    public void setmTracks(int mTracks) {
        this.mTracks = mTracks;
    }

    /**
     * Getter for count Performer Albums
     * @return int mAlbums
     * */
    public int getmAlbums() {
        return mAlbums;
    }

    /**
     * Setter for count Performer Albums
     * @param mAlbums int
     * */
    public void setmAlbums(int mAlbums) {
        this.mAlbums = mAlbums;
    }

    /**
     * Getter for link on Performer
     * @return int mAlbums
     * */
    public String getmLink() {
        return mLink;
    }

    /**
     * Setter for link on Performer
     * @param mLink String
     * */
    public void setmLink(String mLink) {
        this.mLink = mLink;
    }

    /**
     * Getter for Performer Description
     * @return String mDescription
     * */
    public String getmDescription() {
        return mDescription;
    }

    /**
     * Setter for Performer Description
     * @param mDescription String
     * */
    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    /**
     * Getter for Performer Small Cover
     * @return String mCoverSmall
     * */
    public String getmCoverSmall() {
        return mCoverSmall;
    }

    /**
     * Setter for Performer Small Cover
     * @param mCoverSmall String
     * */
    public void setmCoverSmall(String mCoverSmall) {
        this.mCoverSmall = mCoverSmall;
    }

    /**
     * Getter for Performer Big Cover
     * @return String mCoverBig
     * */
    public String getmCoverBig() {
        return mCoverBig;
    }

    /**
     * Setter for Performer Big Cover
     * @param mCoverBig String
     * */
    public void setmCoverBig(String mCoverBig) {
        this.mCoverBig = mCoverBig;
    }
}
