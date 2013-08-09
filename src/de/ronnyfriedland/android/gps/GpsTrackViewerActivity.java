package de.ronnyfriedland.android.gps;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import net.divbyzero.gpx.Coordinate;
import net.divbyzero.gpx.GPX;
import net.divbyzero.gpx.Track;
import net.divbyzero.gpx.TrackSegment;
import net.divbyzero.gpx.Waypoint;
import net.divbyzero.gpx.parser.JDOM;
import net.divbyzero.gpx.parser.ParsingException;

import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MapView;
import org.mapsforge.android.maps.MapViewPosition;
import org.mapsforge.android.maps.overlay.ArrayWayOverlay;
import org.mapsforge.android.maps.overlay.OverlayWay;
import org.mapsforge.core.GeoPoint;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import de.ronnyfriedland.android.gps.enums.MapStateKeys;
import de.ronnyfriedland.android.gps.enums.PreferenceKeys;
import de.ronnyfriedland.android.gps.to.TrackMetadata;

/**
 * Main activity to start the app.
 * 
 * @author Ronny Friedland
 */
public class GpsTrackViewerActivity extends MapActivity {

    private MapView mapView;
    private TextView textView;

    private List<TrackMetadata> trackList = new ArrayList<TrackMetadata>();

    /**
     * {@inheritDoc}
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapView = (MapView) findViewById(R.id.mapView);
        textView = (TextView) findViewById(R.id.trackSummary);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(v.getContext());
                dialog.setTitle(R.string.trk_title);
                // build dialog
                ScrollView sv = new ScrollView(v.getContext());
                LinearLayout ll = new LinearLayout(v.getContext());
                ll.setOrientation(LinearLayout.VERTICAL);
                ll.setPadding(10, 10, 10, 10);
                sv.addView(ll);
                dialog.setContentView(sv);
                for (TrackMetadata track : trackList) {
                    // add name
                    addElem(ll, getResources().getString(R.string.trk_name), track.getName());
                    // add date
                    SimpleDateFormat sdfDestination = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
                    addElem(ll, getResources().getString(R.string.trk_date), track.getDate() == null ? ""
                            : sdfDestination.format(track.getDate()));
                    // add length
                    addElem(ll, getResources().getString(R.string.trk_length),
                            String.format("%.2f m", track.getLength()));
                }
                dialog.show();
            }

            private void addElem(LinearLayout ll, final String key, final String value) {
                TextView tvKey = new TextView(ll.getContext());
                tvKey.setText(key);
                tvKey.setTextAppearance(ll.getContext(), android.R.style.TextAppearance_Small);
                ll.addView(tvKey);

                TextView tvValue = new TextView(ll.getContext());
                tvValue.setText(value);
                tvValue.setTextAppearance(ll.getContext(), android.R.style.TextAppearance_Medium);
                ll.addView(tvValue);
            }
        });
    }

    /**
     * {@inheritDoc}
     * 
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map_menu, menu);
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_settings:
            startActivity(new Intent(getBaseContext(), GpsTrackViewerSettingsActivity.class));
            return true;
        case R.id.action_exit:
            finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.mapsforge.android.maps.MapActivity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadContent();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        MapViewPosition mapPos = mapView.getMapPosition();
        outState.putByte(MapStateKeys.ZOOM.getKey(), mapPos.getZoomLevel());
        outState.putDouble(MapStateKeys.LATITUDE.getKey(), mapPos.getMapCenter().getLatitude());
        outState.putDouble(MapStateKeys.LONGITUDE.getKey(), mapPos.getMapCenter().getLongitude());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.containsKey(MapStateKeys.ZOOM.getKey())) {
            mapView.setCenter(new GeoPoint(savedInstanceState.getDouble(MapStateKeys.LATITUDE.getKey()),
                    savedInstanceState.getDouble(MapStateKeys.LONGITUDE.getKey())));
            mapView.getController().setZoom((int) savedInstanceState.getByte(MapStateKeys.ZOOM.getKey()));
        } else {
            LocationManager locationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            boolean gpsEnabled = locationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (gpsEnabled) {
                Location location = locationMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    mapView.setCenter(new GeoPoint(location.getLatitude(), location.getLongitude()));
                }
            }
            mapView.getController().setZoom(12);
        }
    }

    private void loadContent() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        loadMap(prefs.getString(PreferenceKeys.MAP.getKey(), ""));
        loadTrack(
                prefs.getStringSet(PreferenceKeys.TRACK.getKey(), Collections.<String> emptySet()),
                Integer.valueOf(prefs.getString(PreferenceKeys.TRACK_COLOR.getKey(),
                        String.valueOf(android.R.color.holo_green_dark))));
    }

    /**
     * Loads the the map which is defined in the preferences.
     */
    private void loadMap(final String mapFileName) {
        File mapFile = new File(GpsTrackViewerSettingsActivity.DATA_DIR, mapFileName);
        if (mapFile.exists() && mapFile.isFile()) {
            mapView.setMapFile(mapFile);
            mapView.setClickable(true);
            mapView.setBuiltInZoomControls(true);
        } else {
            Toast.makeText(getBaseContext(), getResources().getString(R.string.msg_no_map), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Loads the tracks which from the file which filename is defined in the
     * preferences and given as parameter. The tracks are displayed as overlay.
     */
    private void loadTrack(final Set<String> gpxFileNames, final Integer color) {
        Paint wayDefaultPaintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        wayDefaultPaintFill.setStyle(Paint.Style.STROKE);
        wayDefaultPaintFill.setColor(getResources().getColor(color));
        wayDefaultPaintFill.setAlpha(160);
        wayDefaultPaintFill.setStrokeWidth(7);
        wayDefaultPaintFill.setStrokeJoin(Paint.Join.ROUND);
        wayDefaultPaintFill.setPathEffect(new DashPathEffect(new float[] { 20, 20 }, 0));

        Paint wayDefaultPaintOutline = new Paint(Paint.ANTI_ALIAS_FLAG);
        wayDefaultPaintOutline.setStyle(Paint.Style.STROKE);
        wayDefaultPaintOutline.setColor(getResources().getColor(color));
        wayDefaultPaintOutline.setAlpha(128);
        wayDefaultPaintOutline.setStrokeWidth(7);
        wayDefaultPaintOutline.setStrokeJoin(Paint.Join.ROUND);

        trackList.clear();
        mapView.getOverlays().clear();

        ArrayWayOverlay wayOverlay = new ArrayWayOverlay(wayDefaultPaintFill, wayDefaultPaintOutline);
        float length = 0;
        for (String gpxFileName : gpxFileNames) {
            try {
                File gpxFile = new File(GpsTrackViewerSettingsActivity.DATA_DIR, gpxFileName);
                if (gpxFile.exists() && gpxFile.isFile()) {
                    GPX gpx = new JDOM().parse(gpxFile);
                    ArrayList<Track> tracks = gpx.getTracks();
                    for (Track track : tracks) {
                        ArrayList<TrackSegment> segments = track.getSegments();
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
                        OverlayWay way1 = new OverlayWay(
                                new GeoPoint[][] { points.toArray(new GeoPoint[points.size()]) });
                        wayOverlay.addWay(way1);
                        // add track meta data to list
                        trackList.add(new TrackMetadata(gpxFileName, track.startingTime(), length));
                    }
                }
            } catch (ParsingException e) {
                if (!gpxFileName.isEmpty()) {
                    Log.e(getClass().getCanonicalName(), String.format("Error parsing gpx file: %s", gpxFileName));
                    Toast.makeText(getBaseContext(), String.format("Error parsing gpx file: %s", gpxFileName),
                            Toast.LENGTH_LONG).show();
                }
            }
        }
        // add track overlay
        mapView.getOverlays().add(wayOverlay);
        // update summary text view
        textView.setText(String.format(getResources().getString(R.string.msg_track), length / 1000));
    }
}
