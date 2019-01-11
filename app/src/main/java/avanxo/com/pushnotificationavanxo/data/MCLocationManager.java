package avanxo.com.pushnotificationavanxo.data;

import java.util.ArrayList;

public class MCLocationManager {

    private static MCLocationManager ourInstance = null;

    /**
     *  Geolocations retrieved from Marketing cloud's SDK
     */
    private ArrayList<MCGeofence> geofences;
    /**
     * Beacons retrieved from Marketing cloud's SDK
     */
    private ArrayList<MCBeacon> beacons;

    public static MCLocationManager getInstance() {
        if (ourInstance == null){
            ourInstance = new MCLocationManager();
            ourInstance.setGeofences(new ArrayList<MCGeofence>());
            ourInstance.setBeacons(new ArrayList<MCBeacon>());
        }
        return ourInstance;
    }

    public ArrayList<MCGeofence> getGeofences() {
        return geofences;
    }

    public void setGeofences(ArrayList<MCGeofence> geofences) {
        this.geofences = geofences;
    }

    public ArrayList<MCBeacon> getBeacons() {
        return beacons;
    }

    public void setBeacons(ArrayList<MCBeacon> beacons) {
        this.beacons = beacons;
    }
}