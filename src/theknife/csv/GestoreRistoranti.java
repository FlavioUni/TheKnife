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
            String[] riga;
            boolean header = true;

            while ((riga = reader.readNext()) != null) {
                if (header) { 
                    header = false; 
                    continue; 
                }

                String nome = riga[0];
                String indirizzo = riga[1];
                String location = riga[2];
                String prezzo = riga[3]; // "€€€€", "$$$", ecc.
                String cucina = riga[4];

                double longitudine = parseDouble(riga[5]);
                double latitudine = parseDouble(riga[6]);

                String telefono = riga[7];
                String websiteUrl = riga[8];
                String premi = riga[9]; // "Selected Restaurants"/"Bib Gourmand"/"1 Star"...
                String servizi = riga[10]; // lista CSV di servizi

                boolean prenotazioneOnline = parseBool(riga[11]); // "SI"/"NO"/"TRUE"/"FALSE"/"1"
                boolean delivery = parseBool(riga[12]);

                String descrizione = riga.length > 13 ? riga[13] : "";

                Ristorante ristorante = new Ristorante(
                        nome, indirizzo, location, prezzo, cucina,
                        longitudine, latitudine, telefono, websiteUrl,
                        premi, servizi, prenotazioneOnline, delivery, descrizione
                );

                elementi.add(ristorante);
            }
        } catch (IOException e) {
            System.err.println("Errore I/O durante la lettura dei ristoranti: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Errore durante il parsing dei ristoranti: " + e.getMessage());
        }
    }

    @Override
    public void salvaSuCSV(String filePath) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            writer.writeNext(new String[]{
                    "Nome", "Indirizzo", "Location", "Prezzo", "Cucina",
                    "Longitudine", "Latitudine", "Telefono", "SitoWeb",
                    "Premio", "Servizi", "PrenotazioneOnline", "Delivery", "Descrizione"
            });

            for (Ristorante ristorante : elementi) {
                writer.writeNext(new String[]{
                        ristorante.getNome(),
                        ristorante.getIndirizzo(),
                        ristorante.getLocation(),
                        ristorante.getPrezzo(),
                        ristorante.getCucina(),
                        String.valueOf(ristorante.getLongitudine()),
                        String.valueOf(ristorante.getLatitudine()),
                        ristorante.getNumeroTelefono(),
                        ristorante.getWebsiteUrl(),
                        ristorante.getPremi(),
                        ristorante.getServizi(),
                        ristorante.isPrenotazioneOnline() ? "SI" : "NO",
                        ristorante.isDelivery() ? "SI" : "NO",
                        ristorante.getDescrizione() == null ? "" : ristorante.getDescrizione()
                });
            }
        } catch (IOException e) {
            System.err.println("Errore durante la scrittura dei ristoranti: " + e.getMessage());
        }
    }

    private static double parseDouble(String valore) {
        if (valore == null) return 0.0;
        valore = valore.trim();
        if (valore.isEmpty()) return 0.0;
        return Double.parseDouble(valore.replace(',', '.'));
    }
    
    private static boolean parseBool(String valore) {
        if (valore == null) return false;
        String valoreNormalizzato = valore.trim();
        return valoreNormalizzato.equalsIgnoreCase("SI") || 
               valoreNormalizzato.equalsIgnoreCase("SÌ") ||
               valoreNormalizzato.equalsIgnoreCase("YES") || 
               valoreNormalizzato.equalsIgnoreCase("TRUE") ||
               valoreNormalizzato.equals("1");
    }
}