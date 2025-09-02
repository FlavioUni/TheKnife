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
            String[] riga;
            boolean header = true;

            while ((riga = reader.readNext()) != null) {
                if (header) {
                    header = false;
                    continue;
                }

                if (riga.length < 14) {
                    System.err.println("Riga ristoranti ignorata: colonne insufficienti (" + riga.length + ")");
                    continue;
                }

                for (int i = 0; i < riga.length; i++) {
                    riga[i] = riga[i].trim();
                }

                String id = riga[0];
                String nome = riga[1];
                String indirizzo = riga[2];
                String location = riga[3];
                String prezzo = riga[4];
                String cucina = riga[5];

                double longitudine = parseDouble(riga[6]);
                double latitudine = parseDouble(riga[7]);

                String telefono = riga[8];
                String websiteUrl = riga[9];
                String premi = riga[10];
                String servizi = riga[11];

                boolean prenotazioneOnline = parseBool(riga[12]);
                boolean delivery = parseBool(riga[13]);

                Ristorante ristorante = new Ristorante(nome, indirizzo, location, prezzo, cucina, longitudine, latitudine, 
                		telefono, websiteUrl, premi, servizi, prenotazioneOnline, delivery);

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
            writer.writeNext(new String[] {
                "ID", "Nome", "Indirizzo", "Location", "Prezzo", "Cucina",
                "Longitudine", "Latitudine", "Telefono", "SitoWeb",
                "Premio", "Servizi", "PrenotazioneOnline", "Delivery"
            });

            for (Ristorante r : elementi) {
                writer.writeNext(new String[] {
                    r.getId(),
                	r.getNome(),
                    r.getIndirizzo(),
                    r.getLocation(),
                    r.getPrezzoMedio(),
                    r.getCucina(),
                    String.valueOf(r.getLongitudine()),
                    String.valueOf(r.getLatitudine()),
                    r.getNumeroTelefono(),
                    r.getWebsiteUrl(),
                    r.getPremi(),
                    r.getServizi(),
                    r.isPrenotazioneOnline() ? "SI" : "NO",
                    r.isDelivery() ? "SI" : "NO"
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
        String v = valore.trim();
        return v.equalsIgnoreCase("si") || v.equalsIgnoreCase("sì") || v.equalsIgnoreCase("true");
    }
}