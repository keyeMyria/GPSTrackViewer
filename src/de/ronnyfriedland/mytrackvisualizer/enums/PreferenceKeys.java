package de.ronnyfriedland.mytrackvisualizer.enums;

/**
 * Available keys for the preferences.
 * 
 * @author Ronny Friedland
 */
public enum PreferenceKeys {

	/** key to define the map */
	MAP("map_list"),
	/** key to define the track(s) */
	TRACK("track_list");

	private String key;

	/**
	 * Creates a new {@link PreferenceKeys} instance.
	 * 
	 * @param key
	 *            the key
	 */
	private PreferenceKeys(final String key) {
		this.key = key;
	}

	/**
	 * Returns the key of the preference.
	 * 
	 * @return the key
	 */
	public String getKey() {
		return key;
	}
}
