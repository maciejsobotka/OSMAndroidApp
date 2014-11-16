package com.pwr.osm.osmandroidapp;

import com.pwr.osm.osmandroidapp.R;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapView.Projection;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.PathOverlay;

/**
 * Główna aktywność aplikacji.
 * Wyświetla mapę z Open Street Maps.
 * Pozwala na stawianie Markerów za pomocą pojedyńczych tapnięć.
 * Posiada przycisk do wysłania punktów na serwer oraz
 * przycisk do usunięcia punktów.
 * 
 * @author Sobot
 *
 */
public class MainActivity extends Activity {

	private MapView mapView;
	private MapController mapController;
	private ArrayList<GeoPoint> pointsUser = new ArrayList<GeoPoint>();
	private ArrayList<Marker> markers = new ArrayList<Marker>(); 
	
    /**
     * Sprawdza czy jest połączenie z internetem.
     * @return tak jeżeli jest internet.
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
    
	/** 
	 * Inicjalizuje mapę i ustawia jej środek na centrum Wrocławia.
	 */
	private void initMap() {
    	mapView = (MapView) findViewById(R.id.mapview);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
  	  	mapView.setBuiltInZoomControls(true);
  	  	mapView.setMultiTouchControls(true);
        mapController = (MapController) mapView.getController();
        mapController.setZoom(15);
  	  	GeoPoint start = new GeoPoint(51.109961, 17.031492);
        mapController.setCenter(start);
    }
    
	/**
	 * Ustawia odpowiednie layouty oraz elementy potrzebne do obsługi mapy.
	 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button findPathButton = (Button) findViewById(R.id.find_path_button);
  	  	Button clearButton = (Button) findViewById(R.id.clear_button);
        initMap();
		Overlay touchOverlay = new Overlay(this){	
	        @Override
	        public boolean onSingleTapConfirmed(final MotionEvent e, final MapView mapView) {
	        	  
	            Projection projection = mapView.getProjection();
	            GeoPoint tapLocation = (GeoPoint) projection.fromPixels((int)e.getX(), (int)e.getY());
	            pointsUser.add(tapLocation);
	              
	        	Marker marker = new Marker(mapView);
	            marker.setPosition(tapLocation);
	            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
	            marker.setTitle("Lat: " + tapLocation.getLatitude() + "\nLong: " + tapLocation.getLongitude());
	            if (markers.size() != 2){
		            markers.add(marker);
		            mapView.getOverlays().add(marker);
	            }
	            else{
	            	Marker tempMarker = markers.get(1);
	            	mapView.getOverlays().removeAll(markers);
	            	for(Marker m : markers)
	            		m.hideInfoWindow();
	            	markers.clear();
	            	markers.add(tempMarker);
	            	markers.add(marker);
		            mapView.getOverlays().addAll(markers);
	            }
	                mapView.invalidate();
	              
	            return true;
	        }
	        
	        @Override
	        protected void draw(Canvas arg0, MapView arg1, boolean arg2) {
	
	        }
		};
		
		final PathOverlay path = new PathOverlay(Color.BLUE, this);
	    Paint paint = path.getPaint();
	    paint.setStrokeWidth(5);
	    path.setPaint(paint);
		
		findPathButton.setOnClickListener(new OnClickListener()
        {
		    /**
		     * Wysyła punkty na serwer.
		     * Odbiera odpowiedź zwrotną i wyświetla ścieżkę.
		     */
            @Override
            public void onClick(View v) {
            	if (markers.size() == 2)
	            	if (isNetworkAvailable()){
		            	UDPHandleTask udpSender = new UDPHandleTask(mapView, path, pointsUser);
		            	udpSender.execute();
	            	}
	            	else{
	            		Context context = getApplicationContext();
	            		CharSequence text = "Brak połączenia z internetem!";
	            		int duration = Toast.LENGTH_SHORT;
	
	            		Toast toast = Toast.makeText(context, text, duration);
	            		toast.show();
	            	}
            	else{
            		Context context = getApplicationContext();
            		CharSequence text = "Potrzebne są dwa punkty.";
            		int duration = Toast.LENGTH_SHORT;

            		Toast toast = Toast.makeText(context, text, duration);
            		toast.show();
            	}
             }           
        });
		
        clearButton.setOnClickListener(new OnClickListener()
        {
		    /**
		     * Usuwa wszystkie markery, punkty z nimi związane oraz  ścieżki.
		     */
            @Override
            public void onClick(View v) {    
            	mapView.getOverlays().removeAll(markers);
            	mapView.getOverlays().remove(path);
            	for(Marker m : markers)
            		m.hideInfoWindow();
            	markers.clear();
            	path.clearPath();
            	pointsUser.clear();
            	mapView.invalidate();
            }           
        });
		mapView.getOverlays().add(touchOverlay);
    }
    
    /**
     * Wyłącza aplikację po wciśnięciu przycisku BACK na telefonie.
     */
    @Override
    public void onBackPressed() {
    	finish();
    	System.exit(0);
    }
}
