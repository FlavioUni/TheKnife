/*Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/
package theknife.logica;

import com.byteowls.jopencage.JOpenCageGeocoder;
import com.byteowls.jopencage.model.JOpenCageForwardRequest;
import com.byteowls.jopencage.model.JOpenCageResponse;
import com.byteowls.jopencage.model.JOpenCageResult;

import theknife.ristorante.Ristorante;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Servizio di geolocalizzazione e ricerca ristoranti.
 * Usa JOpenCage per geocoding e caching locale per efficienza.
 */

public class GeoService {
	private final String apiKey;

    public GeoService () {
        // Leggo dal file config.properties
        Properties props = new Properties();
        String key = null;
        try (FileInputStream fis = new FileInputStream("data/config.properties")) {
            props.load(fis);
            key = props.getProperty("JOPENCAGE_API_KEY");
        } catch (IOException e) {
            System.err.println("[GeoService] Nessun config.properties trovato, provo variabile d'ambiente...");
        }

        if (key == null || key.isEmpty()) {
            key = System.getenv("JOPENCAGE_API_KEY");
        }

        if (key == null || key.isEmpty()) {
            System.err.println("[GeoService] API Key mancante. Geolocalizzazione disabilitata.");
        }
        this.apiKey = key;
    }
    
 // Metodo riutilizzabile: converte indirizzo in coordinate
    public double[] geocode (String indirizzo) {
        try {
            JOpenCageGeocoder geocoder = new JOpenCageGeocoder(apiKey);
            JOpenCageForwardRequest request = new JOpenCageForwardRequest(indirizzo);
            request.setLimit(1);
            JOpenCageResponse response = geocoder.forward(request);

            if (!response.getResults().isEmpty()) {
                JOpenCageResult result = response.getResults().get(0);
                return new double[]{ result.getGeometry().getLat(), result.getGeometry().getLng() };
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
 // Metodo riutilizzabile: normalizza la città
    public String normalizzaCitta (String citta) {
        return citta == null ? null : citta.trim().toLowerCase();
    }
    
    // Filtra i ristoranti entro una certa distanza da un indirizzo utente.
    public List<Ristorante> filtraPerVicinoA (String indirizzoUtente, double distanzaMaxKm, List<Ristorante> listaRistoranti) {
        List<Ristorante> filtrati = new ArrayList<>();
        if (apiKey == null) return filtrati; // ritorno vuoto se non ho la chiave

        try {
            JOpenCageGeocoder geocoder = new JOpenCageGeocoder(apiKey);
            JOpenCageForwardRequest request = new JOpenCageForwardRequest(indirizzoUtente);
            request.setLimit(1);

            JOpenCageResponse response = geocoder.forward(request);
            if (response.getResults().isEmpty()) {
                System.err.println("[GeoService] Nessun risultato per " + indirizzoUtente);
                return filtrati;
            }

            JOpenCageResult result = response.getResults().get(0);
            double latUtente = result.getGeometry().getLat();
            double lonUtente = result.getGeometry().getLng();

            for (Ristorante r : listaRistoranti) {
                double distanza = calcolaDistanza(latUtente, lonUtente, r.getLatitudine(), r.getLongitudine());
                if (distanza <= distanzaMaxKm) {
                    filtrati.add(r);
                }
            }

        } catch (Exception e) {
            System.err.println("[GeoService] Errore durante il geocoding: " + e.getMessage());
        }

        return filtrati;
    }
    // Calcola la distanza (in km) tra due coordinate geografiche usando Haversine.
    public double calcolaDistanza (double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // raggio terrestre in km
        double latDist = Math.toRadians(lat2 - lat1);
        double lonDist = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDist / 2) * Math.sin(latDist / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDist / 2) * Math.sin(lonDist / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
