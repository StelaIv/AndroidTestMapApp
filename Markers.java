package map.mymapretry;

public class Markers {
    private String countryName;
    private String streetAddress;
    double latitude;
    double longitude;

    public Markers(String countryName, double longitude, double latitude, String streetAddress) {
        this.countryName = countryName;
        this.longitude = longitude;
        this.latitude = latitude;
        this.streetAddress = streetAddress;
    }

    public Markers() {
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}
