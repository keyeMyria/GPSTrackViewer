package de.ronnyfriedland.mytrackvisualizer.to;

import java.util.Date;

/**
 * @author Ronny Friedland
 */
public class TrackMetadata {

	private String name;
	private Date date;
	private float length;

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
