
package com.adiaz.legasports.sync.retrofit.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ClassificationEntity {

    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("position")
    @Expose
    private Long position;
    @SerializedName("team")
    @Expose
    private String team;
    @SerializedName("points")
    @Expose
    private Long points;
    @SerializedName("matchesPlayed")
    @Expose
    private Long matchesPlayed;
    @SerializedName("matchesWon")
    @Expose
    private Long matchesWon;
    @SerializedName("matchesDrawn")
    @Expose
    private Long matchesDrawn;
    @SerializedName("matchesLost")
    @Expose
    private Long matchesLost;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    public Long getMatchesPlayed() {
        return matchesPlayed;
    }

    public void setMatchesPlayed(Long matchesPlayed) {
        this.matchesPlayed = matchesPlayed;
    }

    public Long getMatchesWon() {
        return matchesWon;
    }

    public void setMatchesWon(Long matchesWon) {
        this.matchesWon = matchesWon;
    }

    public Long getMatchesDrawn() {
        return matchesDrawn;
    }

    public void setMatchesDrawn(Long matchesDrawn) {
        this.matchesDrawn = matchesDrawn;
    }

    public Long getMatchesLost() {
        return matchesLost;
    }

    public void setMatchesLost(Long matchesLost) {
        this.matchesLost = matchesLost;
    }

}