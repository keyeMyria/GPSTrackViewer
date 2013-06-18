package de.ronnyfriedland.mytrackvisualizer.to;

import java.util.Date;

/**
 * Transfer object which contains some meta data of a track.
 * 
 * @author Ronny Friedland
 */
public class TrackMetadata {

	private String name;
	private Date date;
	private float length;

	/**
	 * Creates a new (immutable) {@link TrackMetadata} instance.
	 * 
	 * @param name
	 *            the name of the track
	 * @param date
	 *            the start date
	 * @param length
	 *            the length of the track
	 */
	public TrackMetadata(final String name, final Date date, final float length) {
		this.name = name;
		this.date = date;
		this.length = length;
	}

	public String getName() {
		return name;
	}

	public Date getDate() {
		return date;
	}

	public float getLength() {
		return length;
	}
}
