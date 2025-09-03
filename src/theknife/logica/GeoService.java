/*
Ciani Flavio Angelo, 761581, VA
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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Properties;

/**
 * La classe GeoService fornisce funzionalità di geocoding e ricerca geografica.
 * Usa l'API JOpenCage per convertire indirizzi in coordinate (latitudine, longitudine)
 * e mantiene una cache locale su file per ridurre le chiamate all'API.
 * 
 * Carica la chiave API da:
 * 1) file "data/config.properties" (chiave: JOPENCAGE_API_KEY)
 * 2) variabile d'ambiente "JOPENCAGE_API_KEY"
 * Se assente, le funzioni di geocoding risultano limitate.
 * 
 * @author Gasparini Lorenzo
 * @author Ciani Flavio Angelo
 * @author Scolaro Gabriele
 */
public class GeoService {

    // CAMPI
    private final String apiKey;
    private Map<String, double[]> geocodeCache = new HashMap<>();
    private static final String CACHE_FILE = "data/geocache.ser";

    /**
     * COSTRUTTORE di GeoService.
     * Inizializza la chiave API e carica la cache da file, se presente.
     */
    public GeoService() {
        this.apiKey = loadApiKey();
        loadCacheFromFile();
    }

    // ==================== API KEY ====================

    /**
     * Carica la chiave API cercando prima nel file config.properties e poi
     * nella variabile d'ambiente.
     * 
     * @return La chiave API trovata, oppure {@code null} se non disponibile
     */
    private String loadApiKey() {
        try (FileInputStream fis = new FileInputStream("data/config.properties")) {
            Properties p = new Properties();
            p.load(fis);
            String k = p.getProperty("JOPENCAGE_API_KEY");
            if (k != null && !k.isBlank()) 
            	return k.trim();
        } catch (IOException ignore) {
            System.err.println("[GeoService] Nessun config.properties, provo variabile d'ambiente…");
        }

        String env = System.getenv("JOPENCAGE_API_KEY");
        if (env == null || env.isBlank()) {
            System.err.println("[GeoService] API Key mancante. Geolocalizzazione limitata.");
            return null;
        }
        return env.trim();
    }

    // ==================== CACHE ====================

    /**
     * Carica la cache indirizzo→coordinate da file, se presente.
     * In caso di errore la cache viene inizializzata vuota.
     */
    @SuppressWarnings("unchecked")
    private void loadCacheFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(CACHE_FILE))) {
            Object obj = ois.readObject();
            if (obj instanceof Map<?, ?> map) {
                this.geocodeCache = (Map<String, double[]>) map;
            }
        } catch (Exception ignore) {
            this.geocodeCache = new HashMap<>();
        }
    }

    /**
     * Salva la cache su file. Da chiamare tipicamente in chiusura applicazione.
     */
    public void shutdown() {saveCacheToFile();}

    /**
     * Serializza la cache su disco.
     */
    private void saveCacheToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CACHE_FILE))) {
            oos.writeObject(geocodeCache);
        } catch (Exception e) {
            System.err.println("[GeoService] Errore salvataggio cache: " + e.getMessage());
        }
    }

    // ==================== GEOCODING ====================

    /**
     * Converte un indirizzo testuale in coordinate geografiche.
     * Usa una cache locale per evitare chiamate ripetute all’API.
     * 
     * @param indirizzo Testo completo dell’indirizzo
     * @return Array {@code double[2]} nel formato {latitudine, longitudine}, oppure {@code null} se non disponibile
     */
    public double[] geocode(String indirizzo) {
        if (indirizzo == null || indirizzo.isBlank()) return null;

        String key = indirizzo.trim().toLowerCase();
        double[] cached = geocodeCache.get(key);
        if (cached != null) return cached;
        if (apiKey == null) return null;

        try {
            JOpenCageGeocoder g = new JOpenCageGeocoder(apiKey);
            JOpenCageForwardRequest req = new JOpenCageForwardRequest(indirizzo);
            req.setLimit(1);
            req.setNoAnnotations(true);
            req.setLanguage("it");

            JOpenCageResponse resp = g.forward(req);
            if (!resp.getResults().isEmpty()) {
                JOpenCageResult r = resp.getResults().get(0);
                double[] coords = new double[]{ r.getGeometry().getLat(), r.getGeometry().getLng() };
                geocodeCache.put(key, coords);
                return coords;
            }
        } catch (Exception e) {
            System.err.println("[GeoService] geocode error: " + e.getMessage());
        }
        return null;
    }

    // ==================== RICERCA “VICINO A” ====================

    /**
     * Filtra e restituisce i ristoranti entro una certa distanza (raggio) da un indirizzo fornito.
     * 
     * @param indirizzoUtente Indirizzo di partenza
     * @param distanzaMaxKm Distanza massima in chilometri
     * @param lista Lista completa dei ristoranti su cui effettuare il filtro
     * @return Lista di ristoranti entro il raggio richiesto (può essere vuota)
     */
    public List<Ristorante> filtraPerVicinoA(String indirizzoUtente, double distanzaMaxKm, List<Ristorante> lista) {
        List<Ristorante> out = new ArrayList<>();
        if (lista == null || lista.isEmpty()) 
        	return out;

        double[] coord = geocode(indirizzoUtente);
        if (coord == null) {
            System.out.println("[GeoService] Non riesco a geocodificare: " + indirizzoUtente);
            return out;
        }

        double latU = coord[0], lonU = coord[1];
        for (Ristorante r : lista) {
            double latR = r.getLatitudine();
            double lonR = r.getLongitudine();
            if (latR == 0.0 && lonR == 0.0) 
            	continue; // ignora i ristoranti senza coordinate

            double d = calcolaDistanza(latU, lonU, latR, lonR);
            if (d <= distanzaMaxKm) out.add(r);
        }
        return out;
    }

    /**
     * Calcola la distanza fra due punti (lat/lon) sulla superficie terrestre, in km.
     * Formula di Haversine.
     * 
     * @param lat1 Latitudine punto 1
     * @param lon1 Longitudine punto 1
     * @param lat2 Latitudine punto 2
     * @param lon2 Longitudine punto 2
     * @return Distanza in chilometri
     */
    public static double calcolaDistanza(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0; // raggio terrestre in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double sinDLat = Math.sin(dLat / 2);
        double sinDLon = Math.sin(dLon / 2);

        double a = sinDLat * sinDLat
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * sinDLon * sinDLon;

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    // ===== Metodi di utilità potenzialmente futuri =====
    /**
     * Svuota la cache (in RAM) degli indirizzi geocodificati.
     */
    public void clearCache() { geocodeCache.clear(); }

    /**
     * Restituisce la dimensione attuale della cache in RAM.
     * 
     * @return Numero di voci nella cache
     */
    public int getCacheSize() { return geocodeCache.size(); }
}