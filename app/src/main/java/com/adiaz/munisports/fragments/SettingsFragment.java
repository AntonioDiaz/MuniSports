package com.adiaz.munisports.fragments;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.adiaz.munisports.R;

/**
 * Created by toni on 03/08/2017.
 */

public class SettingsFragment extends PreferenceFragmentCompat {
	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		addPreferencesFromResource(R.xml.pref_munisports);
	}
}