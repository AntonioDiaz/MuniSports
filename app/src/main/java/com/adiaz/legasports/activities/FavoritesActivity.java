package com.adiaz.legasports.activities;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.adiaz.legasports.R;
import com.adiaz.legasports.entities.CompetitionEntity;
import com.adiaz.legasports.entities.TeamFavoriteEntity;
import com.adiaz.legasports.fragments.FavoritesCompetitionsFragment;
import com.adiaz.legasports.fragments.FavoritesTeamsFragment;
import com.adiaz.legasports.utilities.Utils;
import com.adiaz.legasports.utilities.ViewPagerAdapter;

import java.util.List;


public class FavoritesActivity extends AppCompatActivity {

	private static final String TAG = FavoritesActivity.class.getSimpleName();
	public static final String TEAM_NAME = "team_name";

	private TabLayout tabLayout;
	private ViewPager viewPager;

	public static List<CompetitionEntity> competitionsFavorites;
	public static List<TeamFavoriteEntity> teamsFavorites;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favorites);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
		collapsingToolbar.setTitle(getString(R.string.favorites));

		tabLayout = (TabLayout)findViewById(R.id.tabs);
		viewPager = (ViewPager)findViewById(R.id.viewpager);
		setupViewPager(viewPager);
		tabLayout.setupWithViewPager(viewPager);
	}

	@Override
	protected void onResume() {
		super.onResume();
		competitionsFavorites = Utils.getCompetitionsFavorites(this);
		teamsFavorites = Utils.getTeamsFavorites(this);
	}

	private void setupViewPager(ViewPager viewPager) {
		ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
		adapter.addFragment(new FavoritesTeamsFragment(), getString(R.string.teams));
		adapter.addFragment(new FavoritesCompetitionsFragment(), getString(R.string.competitions));
		viewPager.setAdapter(adapter);
	}

	@Override
	public boolean onSupportNavigateUp() {
		onBackPressed();
		return true;
	}
}