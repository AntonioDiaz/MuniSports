package com.adiaz.munisports.activities;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.adiaz.munisports.R;
import com.adiaz.munisports.adapters.FavoriteTeamAdapter;
import com.adiaz.munisports.entities.CourtEntity;
import com.adiaz.munisports.entities.TeamEntity;
import com.adiaz.munisports.entities.TeamMatchEntity;
import com.adiaz.munisports.utilities.MuniSportsConstants;
import com.adiaz.munisports.utilities.Utils;
import com.adiaz.munisports.utilities.harcoPro.HeaderView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static com.adiaz.munisports.database.MuniSportsDbContract.MatchesEntry;



public class FavoriteTeamActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

	@BindView(R.id.app_bar_layout) AppBarLayout appBarLayout;
	@BindView(R.id.toolbar) Toolbar toolbar;
	@BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbar;
	@BindView(R.id.toolbar_header_view)	HeaderView toolbarHeaderView;
	@BindView(R.id.float_header_view) HeaderView floatHeaderView;
	@BindView(R.id.rv_fav_team_jornadas) RecyclerView recyclerView;
	@BindView(R.id.tv_title) TextView tvTitle;
	private String teamName;
	private String idCompetitionServer;
	private String competitionName;
	private String sportTag;
	private String categoryTag;
	private boolean isHideToolbarView = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favorite_team);
		ButterKnife.bind(this);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		teamName = getIntent().getStringExtra(MuniSportsConstants.INTENT_TEAM_NAME);
		competitionName = getIntent().getStringExtra(MuniSportsConstants.INTENT_COMPETITION_NAME);
		idCompetitionServer = getIntent().getStringExtra(MuniSportsConstants.INTENT_ID_COMPETITION_SERVER);
		sportTag = getIntent().getStringExtra(MuniSportsConstants.INTENT_SPORT_TAG);
		categoryTag = getIntent().getStringExtra(MuniSportsConstants.INTENT_CATEGORY_TAG);
		String subTitle = competitionName;
		subTitle += " - " + Utils.getStringResourceByName(this, categoryTag);
		subTitle += " - " + Utils.getStringResourceByName(this, sportTag);
		collapsingToolbar.setTitle(" ");

		toolbarHeaderView.bindTo(teamName, subTitle, 0);
		floatHeaderView.bindTo(teamName, subTitle, 16);
		appBarLayout.addOnOffsetChangedListener(this);
		TeamEntity teamEntity = FavoriteTeamActivity.initTeamCompetition(this, teamName, idCompetitionServer);
		FavoriteTeamAdapter adapter = new FavoriteTeamAdapter(this);
		recyclerView.setHasFixedSize(true);
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setNestedScrollingEnabled(false);
		adapter.setTeamEntity(teamEntity);
		SharedPreferences preferences = getDefaultSharedPreferences(this);
		String townSelect = preferences.getString(MuniSportsConstants.KEY_TOWN_NAME, null);
		tvTitle.setText(townSelect + " - " + getString(R.string.app_name));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_competition, menu);
		String favoriteTeamId = Utils.composeFavoriteTeamId(teamName, idCompetitionServer);
		for(int i = 0; i < menu.size(); i++) {
			if (menu.getItem(i).getItemId()== R.id.action_favorites) {
				String key = getString(R.string.key_favorites_teams);
				if (Utils.checkIfFavoritSelected(this, favoriteTeamId, key)) {
					AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
					Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_favorite_fill);
					menu.getItem(i).setIcon(drawable);
				}
				Drawable icon = menu.getItem(i).getIcon();
				int colorWhite = ContextCompat.getColor(this, R.color.colorWhite);
				final PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(colorWhite, PorterDuff.Mode.SRC_IN);
				icon.setColorFilter(colorFilter);
			}
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_favorites:
				String favoriteTeamId = Utils.composeFavoriteTeamId(teamName, idCompetitionServer);
				String key = getString(R.string.key_favorites_teams);
				AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
				Drawable drawable;
				if (Utils.checkIfFavoritSelected(this, favoriteTeamId, key)) {
					Utils.unMarkFavoriteTeam(this, favoriteTeamId, key);
					drawable = ContextCompat.getDrawable(this, R.drawable.ic_favorite);
				} else {
					Utils.markFavoriteTeam(this, favoriteTeamId, key);
					drawable = ContextCompat.getDrawable(this, R.drawable.ic_favorite_fill);
				}
				int colorWhite = ContextCompat.getColor(this, R.color.colorWhite);
				final PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(colorWhite, PorterDuff.Mode.SRC_IN);
				drawable.setColorFilter(colorFilter);
				item.setIcon(drawable);
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onSupportNavigateUp() {
		onBackPressed();
		return true;
	}
	public void showLocation(View view) {
		TeamMatchEntity teamMatchEntity = (TeamMatchEntity)view.getTag();
		Uri addressUri = Uri.parse("geo:0,0?q=" + teamMatchEntity.getPlaceAddress());
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(addressUri);
		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivity(intent);
		}
	}

	public void addEvent(View view) {
		TeamMatchEntity teamMatchEntity = (TeamMatchEntity)view.getTag();

		Calendar beginTime = Calendar.getInstance();
		beginTime.setTime(teamMatchEntity.getDate());
		Calendar endTime = Calendar.getInstance();
		endTime.setTime(teamMatchEntity.getDate());

		endTime.add(Calendar.HOUR, 2);

		String titleStr = getString(R.string.calendar_title, teamName, teamMatchEntity.getOpponent());
		String descStr = getString(R.string.calendar_description, teamName, teamMatchEntity.getOpponent());

		Intent intent = new Intent(Intent.ACTION_INSERT)
				.setData(CalendarContract.Events.CONTENT_URI)
				.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
				.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
				.putExtra(CalendarContract.Events.TITLE, titleStr)
				.putExtra(CalendarContract.Events.DESCRIPTION, descStr)
				.putExtra(CalendarContract.Events.EVENT_LOCATION, teamMatchEntity.getPlaceName());
		startActivity(intent);
	}

	public void shareMatchDetails(View view) {
		String mimeType = "text/plain";
		TeamMatchEntity teamMatchEntity = (TeamMatchEntity)view.getTag();
		String title = getString(R.string.calendar_title, teamName, teamMatchEntity.getOpponent());
		DateFormat df = new SimpleDateFormat(MuniSportsConstants.DATE_FORMAT);
		String strDate = df.format(teamMatchEntity.getDate());
		String subject = getString(R.string.share_description, teamName, teamMatchEntity.getOpponent(), strDate, teamMatchEntity.getPlaceName());
		ShareCompat.IntentBuilder
				.from(this)
				.setChooserTitle(title)
				.setType(mimeType)
				.setText(subject)
				.startChooser();
	}

	@Override
	public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
		int maxScroll = appBarLayout.getTotalScrollRange();
		float percentage = (float) Math.abs(offset) / (float) maxScroll;
		if (percentage == 1f && isHideToolbarView) {
			toolbarHeaderView.setVisibility(View.VISIBLE);
			isHideToolbarView = !isHideToolbarView;
		} else if (percentage < 1f && !isHideToolbarView) {
			toolbarHeaderView.setVisibility(View.GONE);
			isHideToolbarView = !isHideToolbarView;
		}
	}

	private static TeamEntity initTeamCompetition(Context context, String teamName, String idCompetitionServer) {
		Uri uri = MatchesEntry.buildMatchesUriWithCompetitions(idCompetitionServer);
		Cursor cursorMatches = context.getContentResolver().query(uri, MatchesEntry.PROJECTION, null, null, null);
		cursorMatches.moveToPosition(-1);
		Integer weeksNumber = -1;
		Map<Long, CourtEntity> mapCourts = Utils.initCourts(context);
		while (cursorMatches.moveToNext()) {
			Integer currentWeek = cursorMatches.getInt(MatchesEntry.INDEX_WEEK);
			if (currentWeek>weeksNumber) {
				weeksNumber = currentWeek;
			}
		}
		cursorMatches.moveToPosition(-1);
		TeamEntity teamEntity = new TeamEntity(teamName, weeksNumber);
		while (cursorMatches.moveToNext()) {
			String teamLocal = cursorMatches.getString(MatchesEntry.INDEX_TEAM_LOCAL);
			String teamVisitor = cursorMatches.getString(MatchesEntry.INDEX_TEAM_VISITOR);
			if (teamName.equals(teamLocal) || teamName.equals(teamVisitor)) {
				TeamMatchEntity teamMatchEntity = new TeamMatchEntity();
				Integer week = cursorMatches.getInt(MatchesEntry.INDEX_WEEK);
				Long longDate = cursorMatches.getLong(MatchesEntry.INDEX_DATE);
				Integer scoreLocal = cursorMatches.getInt(MatchesEntry.INDEX_SCORE_LOCAL);
				Integer scoreVisitor = cursorMatches.getInt(MatchesEntry.INDEX_SCORE_VISITOR);
				if (teamName.equals(teamLocal)) {
					teamMatchEntity.setLocal(true);
					teamMatchEntity.setOpponent(teamVisitor);
				} else {
					teamMatchEntity.setLocal(false);
					teamMatchEntity.setOpponent(teamLocal);
				}
				Long sportCourtId = cursorMatches.getLong(MatchesEntry.INDEX_ID_SPORTCENTER);
				CourtEntity courtEntity = mapCourts.get(sportCourtId);
				teamMatchEntity.setPlaceName(courtEntity.getCourtFullName());
				teamMatchEntity.setPlaceAddress(courtEntity.getCenterAddress());
				teamMatchEntity.setDate(new Date(longDate));
				teamMatchEntity.setTeamScore(scoreLocal);
				teamMatchEntity.setOpponentScore(scoreVisitor);
				teamEntity.add(week -1, teamMatchEntity);
			}
		}
		cursorMatches.close();
		return teamEntity;
	}
}