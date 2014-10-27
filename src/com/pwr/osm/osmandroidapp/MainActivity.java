package com.pwr.osm.osmandroidapp;

import com.pwr.osm.osmandroidapp.R;
import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapView.Projection;
import org.osmdroid.views.overlay.Overlay;


public class MainActivity extends Activity {

	private MapView mapView;
	private MapController mapController;
	private ArrayList<GeoPoint> pointsUser = new ArrayList<GeoPoint>();
	private ArrayList<Marker> markers = new ArrayList<Marker>(); 
	
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
	            markers.add(marker);
	            mapView.getOverlays().add(marker);
                mapView.invalidate();
	              
	            return true;
	        }
	        
	        @Override
	        protected void draw(Canvas arg0, MapView arg1, boolean arg2) {
	
	        }
		};
		
		findPathButton.setOnClickListener(new OnClickListener()
        {     	
            @Override
            public void onClick(View v) {
            	UDPHandleTask udpSender = new UDPHandleTask(pointsUser);
            	udpSender.execute();
             }           
        });
		
        clearButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v) {    
            	mapView.getOverlays().removeAll(markers);
            	markers.clear();
            	pointsUser.clear();
            	mapView.invalidate();
            }           
        });
		mapView.getOverlays().add(touchOverlay);
    }
   
    @Override
    public void onBackPressed() {
    	finish();
    	System.exit(0);
    }
}
