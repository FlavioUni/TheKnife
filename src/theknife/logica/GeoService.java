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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
	private Map<String, double[]> geocodeCache; // Cache per indirizzi già geocodificati
    private final Map<String, String> cittaNormalizzataCache; // Cache per città normalizzate
    private static final String CACHE_FILE = "data/geocache.ser";

    public GeoService () {
    	this.geocodeCache = new HashMap<>();
        this.cittaNormalizzataCache = new HashMap<>();
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
        loadCacheFromFile(); // Carica cache esistente all'avvio
    }
    
    @SuppressWarnings("unchecked")
	private void loadCacheFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(CACHE_FILE))) {
            geocodeCache = (Map<String, double[]>) ois.readObject();
        } catch (Exception e) {
            geocodeCache = new HashMap<>(); // Cache vuota se il file non esiste
        }
    }
    
    public void shutdown() {
        saveCacheToFile();
    }
    
    private void saveCacheToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CACHE_FILE))) {
            oos.writeObject(geocodeCache);
        } catch (Exception e) {
            System.err.println("Errore nel salvare la cache: " + e.getMessage());
        }
    }
    
    // Metodo riutilizzabile: converte indirizzo in coordinate (con caching)
    public double[] geocode (String indirizzo) {
    	if (indirizzo == null || indirizzo.trim().isEmpty()) {
            return null;
        }
    	String indirizzoNormalizzato = indirizzo.trim().toLowerCase();
    	// Controlla se abbiamo già questo indirizzo in cache
    	if (geocodeCache.containsKey(indirizzoNormalizzato)) {
            System.out.println("[GeoService] Cache hit per: " + indirizzo);
            return geocodeCache.get(indirizzoNormalizzato);
        }
    	if (apiKey == null) {
            return null;
        }
        try {
            JOpenCageGeocoder geocoder = new JOpenCageGeocoder(apiKey);
            JOpenCageForwardRequest request = new JOpenCageForwardRequest(indirizzo);
            request.setLimit(1);
            JOpenCageResponse response = geocoder.forward(request);

            if (!response.getResults().isEmpty()) {
                JOpenCageResult result = response.getResults().get(0);
                double[] coordinates = new double[]{result.getGeometry().getLat(), result.getGeometry().getLng()};
             // Salva in cache per future richieste
                geocodeCache.put(indirizzoNormalizzato, coordinates);
                System.out.println("[GeoService] Geocodificato e cached: " + indirizzo);
                
                return coordinates;
            }
        } catch (Exception e) {
        	System.err.println("[GeoService] Errore durante geocoding: " + e.getMessage());
        }
        return null;
    }
    
 // Metodo riutilizzabile: normalizza la città (con caching)
    public String normalizzaCitta (String citta) {
    	if (citta == null || citta.trim().isEmpty())
            return null;
    	String cittaInput = citta.trim().toLowerCase();
    	// Controlla cache
        if (cittaNormalizzataCache.containsKey(cittaInput))
        	return cittaNormalizzataCache.get(cittaInput);
        // Normalizzazione e salvataggio in cache
        String normalizzata = cittaInput;
        cittaNormalizzataCache.put(cittaInput, normalizzata);
        return normalizzata;
    }
    
    // Filtra i ristoranti entro una certa distanza da un indirizzo utente.
    public List<Ristorante> filtraPerVicinoA (String indirizzoUtente, double distanzaMaxKm, List<Ristorante> listaRistoranti) {
        List<Ristorante> filtrati = new ArrayList<>();
        if (apiKey == null) {
        	System.out.println("[GeoService] API key mancante: geolocalizzazione disabilitata.");
        	return filtrati; // Ritorno vuoto se non ho la chiave
        }

        double[] coordUtente = geocode(indirizzoUtente);
        if (coordUtente == null) {
            System.out.println("[GeoService] Impossibile geocodificare: " + indirizzoUtente);
            return filtrati;
        }

        double latUtente = coordUtente[0];
        double lonUtente = coordUtente[1];

        for (Ristorante r : listaRistoranti) {
            double distanza = calcolaDistanza(latUtente, lonUtente, r.getLatitudine(), r.getLongitudine());
            if (distanza <= distanzaMaxKm) {
                filtrati.add(r);
            }
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
    // Metodo per pulire la cache
    public void clearCache() {
        geocodeCache.clear();
        cittaNormalizzataCache.clear();
    }
    
    // Metodo per vedere le dimensioni della cache
    public int getCacheSize() {
        return geocodeCache.size();
    }
}
