/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/
package theknife.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import theknife.recensione.Recensione;
import theknife.utente.GestoreDate;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class GestoreRecensioni extends GestoreCSV<Recensione> {

    @Override
    public void caricaDaCSV(String filePath) {
        elementi = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] c;
            boolean header = true;

            while ((c = reader.readNext()) != null) {
                if (header) {
                    header = false;
                    continue;
                }

                if (c.length < 7) {
                    System.err.println("Riga recensioni ignorata: servono 7 colonne, trovate " + c.length);
                    continue;
                }
                for (int i = 0; i < c.length; i++) {
                    c[i] = c[i].trim();
                }

                String username = c[0];
                String nomeRistorante = c[1];
                String location = c[2];

                int stelle;
                try {
                    stelle = Integer.parseInt(c[3]);
                } catch (NumberFormatException ex) {
                    System.err.println("Riga recensione ignorata: stelle non numeriche (" + c[3] + ")");
                    continue;
                }

                String commento = c[4];
                LocalDate data = GestoreDate.parseNullable(c[5]);
                String risposta = c[6];

                Recensione r = new Recensione(username, nomeRistorante, location, stelle, commento, data, risposta);
                elementi.add(r);
            }

        } catch (IOException e) {
            System.err.println("Errore di I/O nella lettura del file recensioni: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Errore nella lettura/parsing recensioni: " + e.getMessage());
        }
    }

    @Override
    public void salvaSuCSV(String filePath) {
        try (CSVWriter w = new CSVWriter(new FileWriter(filePath))) {
            w.writeNext(new String[]{
                "Username", "NomeRistorante", "LocationRistorante",
                "Stelle", "Commento", "Data", "Risposta"
            });

            for (Recensione r : elementi) {
                w.writeNext(new String[]{
                    r.getAutore(),
                    r.getNomeRistorante(),
                    r.getLocationRistorante(),
                    String.valueOf(r.getStelle()),
                    r.getDescrizione(),
                    GestoreDate.formatOrEmpty(r.getData()),
                    r.getRisposta()
                });
            }
        } catch (IOException e) {
            System.err.println("Errore nella scrittura del file recensioni: " + e.getMessage());
        }
    }
}