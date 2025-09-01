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

import java.io.*;
import java.util.*;

/** Servizio di geolocalizzazione (JOpenCage) + cache locale. */
public class GeoService {
    private final String apiKey;
    private Map<String, double[]> geocodeCache = new HashMap<>();
    private static final String CACHE_FILE = "data/geocache.ser";

    public GeoService() {
        this.apiKey = loadApiKey();
        loadCacheFromFile();
    }

    // ===== API KEY =====
    private String loadApiKey() {
        // 1) config.properties
        try (FileInputStream fis = new FileInputStream("data/config.properties")) {
            Properties p = new Properties();
            p.load(fis);
            String k = p.getProperty("JOPENCAGE_API_KEY");
            if (k != null && !k.isBlank()) return k.trim();
        } catch (IOException ignore) {
            System.err.println("[GeoService] Nessun config.properties, provo variabile d'ambiente…");
        }
        // 2) environment
        String env = System.getenv("JOPENCAGE_API_KEY");
        if (env == null || env.isBlank()) {
            System.err.println("[GeoService] API Key mancante. Geolocalizzazione limitata.");
            return null;
        }
        return env.trim();
    }

    // ===== CACHE =====
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

    public void shutdown() { saveCacheToFile(); }

    private void saveCacheToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CACHE_FILE))) {
            oos.writeObject(geocodeCache);
        } catch (Exception e) {
            System.err.println("[GeoService] Errore salvataggio cache: " + e.getMessage());
        }
    }

    // ===== SUGGERIMENTI INDIRIZZI (per scelta utente) =====
    /** Restituisce fino a 'limit' indirizzi formattati suggeriti per l'input. */
    public List<String> suggerisciIndirizzi(String input, int limit) {
        List<String> out = new ArrayList<>();
        if (input == null || input.isBlank() || apiKey == null) return out;
        try {
            JOpenCageGeocoder g = new JOpenCageGeocoder(apiKey);
            JOpenCageForwardRequest req = new JOpenCageForwardRequest(input);
            req.setLimit(Math.max(1, Math.min(limit, 10)));
            req.setNoAnnotations(true);
            req.setLanguage("it");
            JOpenCageResponse resp = g.forward(req);

            // filtra duplicati e risultati troppo “generici”
            List<String> seen = new ArrayList<>();
            for (JOpenCageResult r : resp.getResults()) {
                String formatted = r.getFormatted();
                if (formatted == null || formatted.isBlank()) continue;
                if (!formatted.matches(".*\\d+.*") && formatted.split(",").length <= 3) continue; // solo città/regione → skip

                boolean dup = false;
                for (String s : seen) {
                    if (s.contains(formatted) || formatted.contains(s)) { dup = true; break; }
                }
                if (!dup) {
                    seen.add(formatted);
                    out.add(formatted);
                }
            }
            out.sort(Comparator.comparingInt(String::length)); // più specifici prima
        } catch (Exception e) {
            System.err.println("[GeoService] suggerisciIndirizzi: " + e.getMessage());
        }
        return out;
    }

    // ===== GEOCODING (con cache) =====
    /** Converte un indirizzo in {lat, lon}. Usa cache locale. */
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

    // ===== VICINO A… =====
    /** Filtra ristoranti entro 'distanzaMaxKm' dall'indirizzo dato. */
    public List<Ristorante> filtraPerVicinoA(String indirizzoUtente, double distanzaMaxKm, List<Ristorante> lista) {
        List<Ristorante> out = new ArrayList<>();
        if (lista == null || lista.isEmpty()) return out;
        double[] coord = geocode(indirizzoUtente);
        if (coord == null) {
            System.out.println("[GeoService] Non riesco a geocodificare: " + indirizzoUtente);
            return out;
        }
        double latU = coord[0], lonU = coord[1];
        for (Ristorante r : lista) {
            double latR = r.getLatitudine();
            double lonR = r.getLongitudine();
            if (latR == 0.0 && lonR == 0.0) continue; // ignora ristoranti senza coordinate
            double d = calcolaDistanza(latU, lonU, latR, lonR);
            if (d <= distanzaMaxKm) out.add(r);
        }
        return out;
    }

    /** Distanza Haversine in km. */
    public static double calcolaDistanza(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1), dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2)*Math.sin(dLat/2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon/2)*Math.sin(dLon/2);
        return 2 * R * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    }

    /** Pulisce le cache in RAM. */
    public void clearCache() { geocodeCache.clear(); }

    /** Dimensione cache indirizzi → coordinate. */
    public int getCacheSize() { return geocodeCache.size(); }
}