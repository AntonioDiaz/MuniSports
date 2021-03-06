package com.adiaz.deportelocal.utilities.retrofit.callbacks;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.adiaz.deportelocal.database.DeporteLocalDbContract;
import com.adiaz.deportelocal.database.daos.CompetitionsDAO;
import com.adiaz.deportelocal.utilities.retrofit.entities.competitiondetails.Classification;
import com.adiaz.deportelocal.utilities.retrofit.entities.competitiondetails.CompetitionDetails;
import com.adiaz.deportelocal.utilities.retrofit.entities.competitiondetails.Match;
import com.adiaz.deportelocal.utilities.retrofit.entities.competitiondetails.SportCenterCourt;
import com.adiaz.deportelocal.utilities.DeporteLocalConstants;
import com.adiaz.deportelocal.utilities.DeporteLocalCourts;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.adiaz.deportelocal.database.DeporteLocalDbContract.ClassificationEntry;
import static com.adiaz.deportelocal.database.DeporteLocalDbContract.MatchesEntry;

/**
 * Created by toni on 18/08/2017.
 */

public class CompetitionDetailsCallbak implements Callback<CompetitionDetails> {

    private static final String TAG = CompetitionDetailsCallbak.class.getSimpleName();
    private Context mContext;
    private Long idCompetitionServer;
    private OnFinishLoad onFinishLoad;

    public CompetitionDetailsCallbak(Context mContext, Long idCompetitionServer, OnFinishLoad onFinishLoad) {
        this.mContext = mContext;
        this.idCompetitionServer = idCompetitionServer;
        this.onFinishLoad = onFinishLoad;
    }

    @Override
    public void onResponse(Call<CompetitionDetails> call, Response<CompetitionDetails> response) {
        /* Update matches and classification.  */
        CompetitionDetails competitionDetails = response.body();
        loadMatches(competitionDetails, this.idCompetitionServer, this.mContext);
        List<Classification> classification = response.body().getClassification();
        loadClassification(classification, this.idCompetitionServer, this.mContext);
        List<Match> matches = response.body().getMatches();
        loadSportCourts(matches, this.mContext);
		/* Updating local server update date. */
        CompetitionsDAO.markCompetitionsAsDirty(mContext, this.idCompetitionServer, false);
        onFinishLoad.finishLoad();
    }

    private void loadClassification(List<Classification> classificationList, Long idCompetitionServer, Context mContext) {
        List<ContentValues> cvClassificationList = new ArrayList<>();
        for (Classification classification : classificationList) {
            ContentValues cvClassification = new ContentValues();
            cvClassification.put(ClassificationEntry.COLUMN_POSITION, classification.getPosition());
            cvClassification.put(ClassificationEntry.COLUMN_TEAM, classification.getTeam());
            cvClassification.put(ClassificationEntry.COLUMN_POINTS, classification.getPoints());
            cvClassification.put(ClassificationEntry.COLUMN_MATCHES_PLAYED, classification.getMatchesPlayed());
            cvClassification.put(ClassificationEntry.COLUMN_MATCHES_WON, classification.getMatchesWon());
            cvClassification.put(ClassificationEntry.COLUMN_MATCHES_DRAWN, classification.getMatchesDrawn());
            cvClassification.put(ClassificationEntry.COLUMN_MATCHES_LOST, classification.getMatchesLost());
            cvClassification.put(ClassificationEntry.COLUMN_ID_COMPETITION_SERVER, idCompetitionServer);
            cvClassification.put(ClassificationEntry.COLUMN_SANCTIONS, classification.getSanctions());
            cvClassificationList.add(cvClassification);
        }
        ContentValues[] classificationArray = cvClassificationList.toArray(new ContentValues[cvClassificationList.size()]);
        ContentResolver contentResolver = mContext.getContentResolver();
        Uri uri = ClassificationEntry.buildClassificationUriWithCompetitions(idCompetitionServer);
        contentResolver.delete(uri, null, null);
        contentResolver.bulkInsert(ClassificationEntry.CONTENT_URI, classificationArray);
    }

    private void loadSportCourts(List<Match> matchList, Context mContext) {
        List<ContentValues> cvSportCourtsList = new ArrayList<>();
        Set<Long> sportCourtsIdInserted = new HashSet<>();
        for (Match match : matchList) {
            SportCenterCourt court = match.getSportCenterCourt();
            if (court != null && !sportCourtsIdInserted.contains(court.getId())) {
                sportCourtsIdInserted.add(court.getId());
                ContentValues cv = new ContentValues();
                cv.put(DeporteLocalDbContract.SportCourtsEntry.COLUMN_ID_SERVER, court.getId());
                cv.put(DeporteLocalDbContract.SportCourtsEntry.COLUMN_COURT_NAME, court.getName());
                cv.put(DeporteLocalDbContract.SportCourtsEntry.COLUMN_CENTER_NAME, court.getSportCenter().getName());
                cv.put(DeporteLocalDbContract.SportCourtsEntry.COLUMN_CENTER_ADDRESS, court.getSportCenter().getAddress());
                cvSportCourtsList.add(cv);
            }
        }
        ContentValues[] cvArray = cvSportCourtsList.toArray(new ContentValues[cvSportCourtsList.size()]);
        ContentResolver contentResolver = mContext.getContentResolver();
        contentResolver.bulkInsert(DeporteLocalDbContract.SportCourtsEntry.CONTENT_URI, cvArray);
        DeporteLocalCourts.refreshCourts(mContext);
    }

    private void loadMatches(CompetitionDetails competitionDetails, Long idCompetitionServer, Context mContext) {
        List<Match> matches = competitionDetails.getMatches();
        List<String> weeksNames = competitionDetails.getWeeksNames();
        List<ContentValues> cvMatcheList = new ArrayList<>();
        for (Match match : matches) {
            ContentValues cvMatch = new ContentValues();
            String teamLocal = match.getTeamLocalEntity() == null ? DeporteLocalConstants.UNDEFINDED_FIELD : match.getTeamLocalEntity().getName();
            String teamVisitor = match.getTeamVisitorEntity() == null ? DeporteLocalConstants.UNDEFINDED_FIELD : match.getTeamVisitorEntity().getName();
            Long sportCenterCourt = match.getSportCenterCourt() == null ? null : match.getSportCenterCourt().getId();
            String weekName = "";
            if (weeksNames!=null && weeksNames.size()>match.getWeek() - 1) {
                weekName = weeksNames.get(match.getWeek().intValue() - 1);
            }
            cvMatch.put(MatchesEntry.COLUMN_TEAM_LOCAL, teamLocal);
            cvMatch.put(MatchesEntry.COLUMN_TEAM_VISITOR, teamVisitor);
            cvMatch.put(MatchesEntry.COLUMN_SCORE_LOCAL, match.getScoreLocal());
            cvMatch.put(MatchesEntry.COLUMN_SCORE_VISITOR, match.getScoreVisitor());
            cvMatch.put(MatchesEntry.COLUMN_WEEK, match.getWeek());
            cvMatch.put(MatchesEntry.COLUMN_WEEK_NAME, weekName);
            cvMatch.put(MatchesEntry.COLUMN_ID_SPORTCENTER, sportCenterCourt);
            cvMatch.put(MatchesEntry.COLUMN_DATE, match.getDate());
            cvMatch.put(MatchesEntry.COLUMN_ID_SERVER, match.getId());
            cvMatch.put(MatchesEntry.COLUMN_ID_COMPETITION_SERVER, idCompetitionServer);
            cvMatch.put(MatchesEntry.COLUMN_STATE, match.getState());
            cvMatcheList.add(cvMatch);
        }
        ContentValues[] matchesArray = cvMatcheList.toArray(new ContentValues[cvMatcheList.size()]);
        ContentResolver contentResolver = mContext.getContentResolver();
        Uri uri = MatchesEntry.buildMatchesUriWithCompetitions(idCompetitionServer);
        contentResolver.delete(uri, null, null);
        contentResolver.bulkInsert(MatchesEntry.CONTENT_URI, matchesArray);
    }

    @Override
    public void onFailure(Call<CompetitionDetails> call, Throwable t) {
        Log.d(TAG, "onFailure: " + t.getMessage());
    }

    public interface OnFinishLoad {
        public void finishLoad();
    }
}
