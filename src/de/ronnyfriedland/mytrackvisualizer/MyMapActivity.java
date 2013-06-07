package de.ronnyfriedland.mytrackvisualizer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.divbyzero.gpx.Coordinate;
import net.divbyzero.gpx.GPX;
import net.divbyzero.gpx.Track;
import net.divbyzero.gpx.TrackSegment;
import net.divbyzero.gpx.Waypoint;
import net.divbyzero.gpx.parser.JDOM;
import net.divbyzero.gpx.parser.ParsingException;

import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MapView;
import org.mapsforge.android.maps.overlay.ArrayWayOverlay;
import org.mapsforge.android.maps.overlay.OverlayWay;
import org.mapsforge.core.GeoPoint;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class MyMapActivity extends MapActivity {

	private MapView mapView;
	private TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		mapView = (MapView) findViewById(R.id.mapView);
		textView = (TextView) findViewById(R.id.trackSummary);

		mapView.setClickable(true);
		mapView.setBuiltInZoomControls(true);
		mapView.setMapFile(new File(Environment.getExternalStorageDirectory() + "/sachsen.map"));
		mapView.getController().setZoom(12);

		// create the default paint objects for overlay ways
		Paint wayDefaultPaintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
		wayDefaultPaintFill.setStyle(Paint.Style.STROKE);
		wayDefaultPaintFill.setColor(getResources().getColor(android.R.color.holo_blue_dark));
		wayDefaultPaintFill.setAlpha(160);
		wayDefaultPaintFill.setStrokeWidth(7);
		wayDefaultPaintFill.setStrokeJoin(Paint.Join.ROUND);
		wayDefaultPaintFill.setPathEffect(new DashPathEffect(new float[] { 20, 20 }, 0));

		Paint wayDefaultPaintOutline = new Paint(Paint.ANTI_ALIAS_FLAG);
		wayDefaultPaintOutline.setStyle(Paint.Style.STROKE);
		wayDefaultPaintOutline.setColor(getResources().getColor(android.R.color.holo_blue_dark));
		wayDefaultPaintOutline.setAlpha(128);
		wayDefaultPaintOutline.setStrokeWidth(7);
		wayDefaultPaintOutline.setStrokeJoin(Paint.Join.ROUND);

		ArrayWayOverlay wayOverlay = new ArrayWayOverlay(wayDefaultPaintFill, wayDefaultPaintOutline);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		String gpxFile = prefs.getString("track_list", "");
		try {
			GPX gpx = new JDOM().parse(new File(Environment.getExternalStorageDirectory(), gpxFile));
			ArrayList<Track> tracks = gpx.getTracks();
			for (Track track : tracks) {
				ArrayList<TrackSegment> segments = track.getSegments();
				double length = 0;
				List<GeoPoint> points = new ArrayList<GeoPoint>();
				for (TrackSegment segment : segments) {
					ArrayList<Waypoint> waypoints = segment.getWaypoints();
					length += segment.length();
					for (Waypoint waypoint : waypoints) {
						Coordinate coordinate = waypoint.getCoordinate();
						GeoPoint geoPoint = new GeoPoint(coordinate.getLatitude(), coordinate.getLongitude());
						points.add(geoPoint);
					}
				}
				OverlayWay way1 = new OverlayWay(new GeoPoint[][] { points.toArray(new GeoPoint[points.size()]) });
				wayOverlay.addWay(way1);

				Toast.makeText(getBaseContext(), "lenght: " + length, Toast.LENGTH_LONG);
				// textView.setText(String.format("Geladener Track: %f",
				// length));
			}
			textView.setText(String.format("Geladener Track: %s", gpxFile));
		} catch (ParsingException e) {
			if (!gpxFile.isEmpty()) {
				Log.e(getClass().getCanonicalName(), String.format("Error parsing gpx file: %s", gpxFile));
				Toast.makeText(getBaseContext(), String.format("Error parsing gpx file: %s", gpxFile),
						Toast.LENGTH_LONG).show();
			}
		}

		mapView.getOverlays().add(wayOverlay);

		textView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getBaseContext(), MySettingsActivity.class));
			}

		});
	}
}
