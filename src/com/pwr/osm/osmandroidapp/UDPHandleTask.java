package com.pwr.osm.osmandroidapp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.PathOverlay;

import pwr.osm.data.representation.MapPosition;

/**
 * Tworzy w¹tek asynchroniczny zajmuj¹cy siê ³¹czeniem z serwerem przez protokó³ UDP.
 * 
 * @author Sobot
 */
public class UDPHandleTask extends AsyncTask<Void, Void ,ArrayList<GeoPoint>>{
	private ArrayList<MapPosition> pointsToServer;
	private ArrayList<GeoPoint> pointsFromServer;
	private PathOverlay path;
	private MapView mapView;
	
	/**
	 * Konstruktor parametryczny.
	 * @param mapView - Wyœwietlacz mapy.
	 * @param path - Wyœwietlacz œcie¿ki.
	 * @param userPoints - Punkty od u¿ytkownika aplikacji.
	 */
	public UDPHandleTask(MapView mapView, PathOverlay path, ArrayList<GeoPoint> userPoints)
	{
		this.mapView = mapView;
		this.path = path;
		
		MapPosition mP;	
		pointsToServer = new ArrayList<MapPosition>();
		
		for (GeoPoint gP : userPoints)
		{
			mP = new MapPosition(gP.getLatitude(),gP.getLongitude());
			pointsToServer.add(mP);
		}
	}
	
	/**
	 * Przepisuje punkty u¿ytkownika aplikacji na odpowiedni¹ strukturê do przes³ania protoko³em.
	 * Wysy³a DatagramPacket na serwer i odbiera odpowiedŸ.
	 * Zwraca odpowiedŸ do onPostExecute.
	 * 
	 * Nale¿y sprawdziæ czy adres ip serwera jest poprawny!
	 */
	@Override
	protected ArrayList<GeoPoint> doInBackground(Void... nothing) {
		try
		{
			String serverAddress = "192.168.0.5";
			DatagramSocket clientSocket = new DatagramSocket();
			InetAddress serverAddr = InetAddress.getByName(serverAddress);
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream(5000);
		  	ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(byteStream));
		  	os.flush();
	        os.writeObject(pointsToServer);
	        os.flush();  
		  	byte[] sendData = byteStream.toByteArray();
		    byte[] receiveData = new byte[5000];
	  
		    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddr, 9876);
		    clientSocket.send(sendPacket);
		    
		    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		    clientSocket.receive(receivePacket);
		    clientSocket.close();
	
		    ByteArrayInputStream inputStream = new ByteArrayInputStream(receiveData);
		    ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(inputStream));
		    @SuppressWarnings("unchecked")
			List<MapPosition> newPoints = (List<MapPosition>) is.readObject();
		    pointsFromServer = new ArrayList<GeoPoint>();
		    
		    for (MapPosition mP : newPoints)
		    {
		    	GeoPoint x = new GeoPoint(mP.getLatitude(),mP.getLongitude());
		    	pointsFromServer.add(x); 
		    }
		
		}
		catch (SocketException e) {
	    e.printStackTrace();
		}catch (UnknownHostException e) {
	    e.printStackTrace();
		} catch (IOException e) {
	    e.printStackTrace();
		} catch (Exception e) {
	    e.printStackTrace();
		}    	
		return pointsFromServer;
	}
	
	/**
	 * Rysuje sie¿kê ³¹cz¹c¹ punkty otrzymane od serwera i uaktualnia mapê.
	 */
	protected void onPostExecute(ArrayList<GeoPoint> pointsFromServer) {
		for (GeoPoint gP : pointsFromServer)
		{
			path.addPoint(gP);
		}
		mapView.getOverlays().add(path);
		mapView.invalidate();
	 }
}
		 
