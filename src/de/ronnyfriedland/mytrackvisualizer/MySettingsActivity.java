package de.ronnyfriedland.mytrackvisualizer;

import java.io.File;
import java.io.FilenameFilter;

import android.os.Bundle;
import android.os.Environment;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

public class MySettingsActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref_general);
		findGpxFilesAndAddToPreferenceSelection();
	}

	/**
	 * Shows the simplified settings UI if the device configuration if the
	 * device configuration dictates that a simplified, single-pane UI should be
	 * shown.
	 */
	private void findGpxFilesAndAddToPreferenceSelection() {

		// In the simplified UI, fragments are not used at all and we instead
		// use the older PreferenceActivity APIs.

		// Add 'general' preferences.

		File sdcard = Environment.getExternalStorageDirectory();
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

		ListPreference trackList = (ListPreference) findPreference("track_list");
		trackList.setEntries(gpxFiles);
		trackList.setEntryValues(gpxFiles);
		trackList.setSummary(trackList.getEntry());
		getPreferenceScreen().addPreference(trackList);
	}

}
