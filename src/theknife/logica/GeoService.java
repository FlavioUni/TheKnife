package theknife.logica;

import com.byteowls.jopencage.JOpenCageGeocoder;
import com.byteowls.jopencage.model.JOpenCageForwardRequest;
import com.byteowls.jopencage.model.JOpenCageResponse;
import com.byteowls.jopencage.model.JOpenCageResult;

import theknife.ristorante.Ristorante;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Servizio di geolocalizzazione e ricerca ristoranti.
 * Usa JOpenCage per geocoding e caching locale per efficienza.
 */

public class GeoService {
	private final JOpenCageGeocoder geocoder;
    private final Map<String, double[]> cache = new HashMap<>(); // Indirizzo → [lat, lon]
    
    public GeoService(String apiKey) {
        this.geocoder = new JOpenCageGeocoder(apiKey);
    }

    /**
     * Restituisce coordinate [lat, lon] a partire da un indirizzo.
     * Usa cache locale per ridurre chiamate API.
     */
    
    public double[] geocode(String indirizzo) {
        if (indirizzo == null || indirizzo.isBlank()) return null;
        indirizzo = indirizzo.trim().toLowerCase();

        // check cache
        if (cache.containsKey(indirizzo)) {
            return cache.get(indirizzo);
        }

        try {
            JOpenCageForwardRequest request = new JOpenCageForwardRequest(indirizzo);
            request.setLimit(1);
            JOpenCageResponse response = geocoder.forward(request);

            if (response.getResults().isEmpty()) {
                System.err.println("[GeoService] Nessun risultato per: " + indirizzo);
                return null;
            }

            JOpenCageResult result = response.getResults().get(0);
            double lat = result.getGeometry().getLat();
            double lon = result.getGeometry().getLng();

            double[] coords = { lat, lon };
            cache.put(indirizzo, coords); // caching
            return coords;

        } catch (Exception e) {
            System.err.println("[GeoService] Errore geocoding: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Filtra i ristoranti entro una certa distanza da un indirizzo utente.
     */
    public List<Ristorante> filtraPerVicinoA(List<Ristorante> listaRistoranti, String indirizzoUtente, double distanzaMassimaKm) {
        List<Ristorante> filtrati = new ArrayList<>();
        double[] coords = geocode(indirizzoUtente);
        if (coords == null) return filtrati;

        double latUtente = coords[0];
        double lonUtente = coords[1];

        for (Ristorante r : listaRistoranti) {
            double distanza = calcolaDistanza(latUtente, lonUtente, r.getLatitudine(), r.getLongitudine());
            if (distanza <= distanzaMassimaKm) {
                filtrati.add(r);
            }
        }
        return filtrati;
    }
    
    /**
     * Normalizza i nomi delle città (es: "milano", "Milano MI" → "Milano").
     */
    public String normalizeCity(String citta) {
        if (citta == null) return null;
        double[] coords = geocode(citta);
        if (coords == null) return citta;
        // Si può estrarre il "formatted" da OpenCage se servisse
        return citta.substring(0, 1).toUpperCase() + citta.substring(1).toLowerCase();
    }
    
    /**
     * Calcola la distanza (in km) tra due coordinate geografiche usando Haversine.
     */
    private double calcolaDistanza(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // raggio medio terrestre in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
