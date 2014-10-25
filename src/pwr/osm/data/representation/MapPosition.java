package pwr.osm.data.representation;

import java.io.Serializable;


public class MapPosition implements Serializable{

	private static final long serialVersionUID = -2974888392332819510L;
	private double latitude;
	private double longitude;
	

	public MapPosition(){
		this(0,0);
	}
	
	public MapPosition(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	@Override
	public String toString(){
		return "lat:"+latitude+" long:"+longitude;
	}
	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}	
}
