package de.ronnyfriedland.mytrackvisualizer.enums;

/**
 * @author Ronny Friedland
 */
public enum PreferenceKeys {

	MAP("map_list"), TRACK("track_list");

	private String key;

	private PreferenceKeys(final String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
}
