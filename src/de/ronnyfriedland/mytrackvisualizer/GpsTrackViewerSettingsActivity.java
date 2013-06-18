package de.ronnyfriedland.mytrackvisualizer;

import java.io.File;
import java.io.FilenameFilter;

import android.os.Bundle;
import android.os.Environment;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import de.ronnyfriedland.mytrackvisualizer.enums.PreferenceKeys;

/**
 * @author Ronny Friedland
 * 
 */
@SuppressWarnings("deprecation")
public class GpsTrackViewerSettingsActivity extends PreferenceActivity {

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		findGpxFilesAndAddToPreferenceSelection();
		findMapFilesAndAddToPreferenceSelection();
	}

	private void findGpxFilesAndAddToPreferenceSelection() {
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

		ListPreference trackList = (ListPreference) findPreference(PreferenceKeys.TRACK.getKey());
		trackList.setEntries(gpxFiles);
		trackList.setEntryValues(gpxFiles);
		trackList.setSummary(trackList.getEntry());
		getPreferenceScreen().addPreference(trackList);
	}

	private void findMapFilesAndAddToPreferenceSelection() {
		File sdcard = Environment.getExternalStorageDirectory();
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
		getPreferenceScreen().addPreference(mapList);
	}
}
