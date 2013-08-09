package de.ronnyfriedland.android.gps.enums;

/**
 * Available keys to save current state of the map.
 * 
 * @author Ronny Friedland
 */
public enum MapStateKeys {

	/** key to define the current zoom */
	ZOOM("zoom"),
	/** key to define the latitude */
	LATITUDE("latitude"),
	/** key to define the longitude */
	LONGITUDE("longitude");

	private String key;

	/**
	 * Creates a new {@link MapStateKeys} instance.
	 * 
	 * @param key
	 *            the key
	 */
	private MapStateKeys(final String key) {
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
