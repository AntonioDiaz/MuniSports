package com.adiaz.deportelocal.fragments;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.adiaz.deportelocal.R;

/**
 * Created by toni on 03/08/2017.
 */

public class SettingsFragment extends PreferenceFragmentCompat {
	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		addPreferencesFromResource(R.xml.pref_localsports);
	}
}
