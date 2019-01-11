package avanxo.com.pushnotificationavanxo.data;
import com.google.android.gms.maps.model.LatLng;

public class MCGeofence {

    private LatLng coordenates;
    private int radius;
    private String name;

    public LatLng getCoordenates() {
        return coordenates;
    }

    public void setCoordenates(LatLng coordenates) {
        this.coordenates = coordenates;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}