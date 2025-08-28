/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/
package theknife.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import theknife.ristorante.Ristorante;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class GestoreRistoranti extends GestoreCSV<Ristorante> {

    @Override
    public void caricaDaCSV(String filePath) {
        elementi = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] c;
            boolean header = true;

            while ((c = reader.readNext()) != null) {
                if (header) { header = false; continue; }

                String nome           = c[0];
                String indirizzo      = c[1];
                String location       = c[2];
                String prezzo         = c[3];   // "€€€€", "$$$", ecc.
                String cucina         = c[4];

                double longit = parseDouble(c[5]);
                double latit  = parseDouble(c[6]);

                String telefono   = c[7];
                String websiteUrl = c[8];
                String premio     = c[9];      // "Selected Restaurants"/"Bib Gourmand"/"1 Star"...
                String servizi    = c[10];     // lista CSV di servizi

                boolean prenOnline = parseBool(c[11]); // "SI"/"NO"/"TRUE"/"FALSE"/"1"
                boolean delivery   = parseBool(c[12]);

                String descrizione = c.length > 13 ? c[13] : "";

                // Adatta all’ordine del TUO costruttore
                Ristorante r = new Ristorante(
                        nome, indirizzo, location, prezzo, cucina,
                        longit, latit, telefono, websiteUrl,
                        premio, servizi, prenOnline, delivery, descrizione
                );

                elementi.add(r);
            }
        } catch (IOException e) {
            System.err.println("Errore I/O lettura ristoranti: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Errore parsing ristoranti: " + e.getMessage());
        }
    }

    @Override
    public void salvaSuCSV(String filePath) {
        try (CSVWriter w = new CSVWriter(new FileWriter(filePath))) {
            w.writeNext(new String[]{
                    "Nome","Indirizzo","Location","Prezzo","Cucina",
                    "Longitudine","Latitudine","PhoneNumber","WebsiteUrl",
                    "Award","FacilitiesAndServices","PrenotazioneOnline","Delivery","Descrizione"
            });

            for (Ristorante r : elementi) {
                w.writeNext(new String[]{
                        r.getNome(),
                        r.getIndirizzo(),
                        r.getLocation(),
                        r.getPrezzo(),
                        r.getCucina(),
                        String.valueOf(r.getLongitudine()),
                        String.valueOf(r.getLatitudine()),
                        r.getNumeroTelefono(),
                        r.getWebsiteUrl(),
                        r.getPremio(),
                        r.getServizi(),
                        r.isPrenotazioneOnline() ? "SI" : "NO",
                        r.isDelivery() ? "SI" : "NO",
                        r.getDescrizione() == null ? "" : r.getDescrizione()
                });
            }
        } catch (IOException e) {
            System.err.println("Errore scrittura ristoranti: " + e.getMessage());
        }
    }

    // parsing locale, semplice e leggibile
    private static double parseDouble(String s) {
        if (s == null) return 0.0;
        s = s.trim(); if (s.isEmpty()) return 0.0;
        return Double.parseDouble(s.replace(',', '.'));
    }
    private static boolean parseBool(String s) {
        if (s == null) return false;
        String t = s.trim();
        return t.equalsIgnoreCase("SI") || t.equalsIgnoreCase("Sì")
            || t.equalsIgnoreCase("YES") || t.equalsIgnoreCase("TRUE")
            || t.equals("1");
    }
}