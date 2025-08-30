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

                if (c.length < 5) {
                    System.err.println("Riga recensioni ignorata: colonne insufficienti (" + c.length + ")");
                    continue;
                }
                for (int i = 0; i < c.length; i++) {
                    c[i] = c[i].trim();
                }

                String username;
                String nomeRistorante;
                String location;
                int stelle;
                String commento;
                LocalDate data;
                String risposta;

                boolean formatoNuovo = c.length >= 7;
                if (formatoNuovo) {
                    username = c[0];
                    nomeRistorante = c[1];
                    location = c[2];
                    try {
                        stelle = Integer.parseInt(c[3]);
                    } catch (NumberFormatException ex) {
                        System.err.println("Riga recensione ignorata: stelle non numeriche (" + c[3] + ")");
                        continue;
                    }
                    commento = c[4];
                    data = GestoreDate.parseNullable(c[5]);
                    risposta = c.length > 6 ? c[6] : "";
                } else {
                    username = c[0];
                    nomeRistorante = c[1];
                    location = "";
                    try {
                        stelle = Integer.parseInt(c[2]);
                    } catch (NumberFormatException ex) {
                        System.err.println("Riga recensione ignorata: stelle non numeriche (" + c[2] + ")");
                        continue;
                    }
                    commento = c[3];
                    data = c.length > 4 ? GestoreDate.parseNullable(c[4]) : null;
                    risposta = c.length > 5 ? c[5] : "";
                }

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