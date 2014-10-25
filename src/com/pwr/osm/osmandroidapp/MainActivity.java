package com.pwr.osm.osmandroidapp;

import android.app.Activity;
import android.os.Bundle;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;

import com.pwr.osm.osmandroidapp.R;

public class MainActivity extends Activity {

	private MapView mapView;
	private MapController mapController; 
	
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
        initMap();
    }
}
