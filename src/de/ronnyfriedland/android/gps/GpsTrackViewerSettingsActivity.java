package de.ronnyfriedland.android.gps;

import java.io.File;
import java.io.FilenameFilter;

import android.os.Bundle;
import android.os.Environment;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import de.ronnyfriedland.android.gps.enums.PreferenceKeys;

/**
 * Activity to create the settings view.
 * 
 * @author Ronny Friedland
 */
@SuppressWarnings("deprecation")
public class GpsTrackViewerSettingsActivity extends PreferenceActivity {

    public static final File DATA_DIR = new File(Environment.getExternalStorageDirectory(),
            "Android/data/org.mapsforge.android.maps");

    /**
     * {@inheritDoc}
     * 
     * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        configureMapListPreference();
        configureTrackListPreference();
        configureTrackColorListPreference();
    }

    private void configureMapListPreference() {
        File sdcard = DATA_DIR;
        String[] mapFiles = sdcard.list(new FilenameFilter() {
            /**
             * {@inheritDoc}
             * 
             * @see java.io.FilenameFilter#accept(java.io.File,
             *      java.lang.String)
             */
            @Override
            public boolean accept(File dir, String filename) {
                return filename.endsWith(".map");
            }
        });

        ListPreference mapList = (ListPreference) findPreference(PreferenceKeys.MAP.getKey());
        mapList.setEntries(mapFiles);
        mapList.setEntryValues(mapFiles);
        mapList.setSummary(mapList.getEntry());
        mapList.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference pref, Object val) {
                pref.setSummary((String) val);
                return true;
            }
        });
        getPreferenceScreen().addPreference(mapList);
    }

    private void configureTrackListPreference() {
        File sdcard = DATA_DIR;
        String[] gpxFiles = sdcard.list(new FilenameFilter() {
            /**
             * {@inheritDoc}
             * 
             * @see java.io.FilenameFilter#accept(java.io.File,
             *      java.lang.String)
             */
            @Override
            public boolean accept(File dir, String filename) {
                return filename.endsWith(".gpx");
            }
        });

        ListPreference trackList = (ListPreference) findPreference(PreferenceKeys.TRACK.getKey());
        trackList.setEntries(gpxFiles);
        trackList.setEntryValues(gpxFiles);
        trackList.setSummary(trackList.getEntry());
        trackList.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference pref, Object val) {
                pref.setSummary((String) val);
                return true;
            }
        });
        getPreferenceScreen().addPreference(trackList);
    }

    private void configureTrackColorListPreference() {
        ListPreference trackColorList = (ListPreference) findPreference(PreferenceKeys.TRACK_COLOR.getKey());
        trackColorList.setSummary(trackColorList.getEntry());
        trackColorList.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference pref, Object val) {
                ListPreference listPref = ((ListPreference) pref);
                CharSequence[] values = listPref.getEntryValues();
                for (int i = 0; i < values.length; i++) {
                    if (val == values[i]) {
                        listPref.setSummary(listPref.getEntries()[i]);
                    }
                }
                return true;
            }
        });
    }
}
