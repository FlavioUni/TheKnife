package theknife.test;

import theknife.logica.GeoService;

public class TestGeoService {
	 public static void main(String[] args) {
	        GeoService geoService = new GeoService();
	        
	        // Test geocoding
	        double[] coord = geoService.geocode("Via Roma 1, Milano");
	        if (coord != null) {
	            System.out.println("Lat: " + coord[0] + ", Lon: " + coord[1]);
	        }
	        
	        // Test distanza
	        double distanza = geoService.calcolaDistanza(45.4642, 9.1900, 45.4660, 9.1880);
	        System.out.println("Distanza: " + distanza + " km");
	    }
}
