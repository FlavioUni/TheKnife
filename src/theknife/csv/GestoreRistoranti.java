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
                if (header) { header = false; continue; }

                // minimo fino a "Delivery" => 13 colonne (Descrizione opzionale)
                if (riga.length < 13) {
                    System.err.println("Riga ristoranti ignorata: colonne insufficienti (" + riga.length + ")");
                    continue;
                }
                for (int i = 0; i < riga.length; i++) riga[i] = riga[i].trim();

                String nome       = riga[0];
                String indirizzo  = riga[1];
                String location   = riga[2];
                String prezzo     = riga[3];
                String cucina     = riga[4];

                double longitudine = parseDouble(riga[5]);
                double latitudine  = parseDouble(riga[6]);

                String telefono   = riga[7];
                String websiteUrl = riga[8];
                String premi      = riga[9];
                String servizi    = riga[10];

                boolean prenotazioneOnline = parseBool(riga[11]);
                boolean delivery           = parseBool(riga[12]);

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
                "Nome","Indirizzo","Location","Prezzo","Cucina",
                "Longitudine","Latitudine","Telefono","SitoWeb",
                "Premio","Servizi","PrenotazioneOnline","Delivery","Descrizione"
            });

            for (Ristorante r : elementi) {
                writer.writeNext(new String[]{
                    r.getNome(),
                    r.getIndirizzo(),
                    r.getLocation(),
                    r.getPrezzo(),
                    r.getCucina(),
                    String.valueOf(r.getLongitudine()),
                    String.valueOf(r.getLatitudine()),
                    r.getNumeroTelefono(),
                    r.getWebsiteUrl(),
                    r.getPremi(),
                    r.getServizi(),
                    r.isPrenotazioneOnline() ? "SI" : "NO",
                    r.isDelivery() ? "SI" : "NO",
                    r.getDescrizione() == null ? "" : r.getDescrizione()
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
        return v.equalsIgnoreCase("SI") || v.equalsIgnoreCase("SÌ")
            || v.equalsIgnoreCase("YES") || v.equalsIgnoreCase("TRUE")
            || v.equals("1");
    }
}