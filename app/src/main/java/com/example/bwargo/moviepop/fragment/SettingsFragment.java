package com.example.bwargo.moviepop.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.example.bwargo.moviepop.R;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_general);

    }

}