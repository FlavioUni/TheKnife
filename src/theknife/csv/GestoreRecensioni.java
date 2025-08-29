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
            String[] campi;
            boolean primaRiga = true;

            while ((campi = reader.readNext()) != null) {
                if (primaRiga) { primaRiga = false; continue; }

                // minimo: Username..Data => 5 colonne (Risposta opzionale)
                if (campi.length < 5) {
                    System.err.println("Riga recensioni ignorata: colonne insufficienti (" + campi.length + ")");
                    continue;
                }
                for (int i = 0; i < campi.length; i++) campi[i] = campi[i].trim();

                String username       = campi[0];
                String nomeRistorante = campi[1];

                int stelle;
                try {
                    stelle = Integer.parseInt(campi[2]);
                } catch (NumberFormatException ex) {
                    System.err.println("Riga recensione ignorata: stelle non numeriche (" + campi[2] + ")");
                    continue;
                }

                String commento = campi[3];
                LocalDate data  = GestoreDate.parseNullable(campi[4]);
                String risposta = campi.length > 5 ? campi[5] : "";

                Recensione r = new Recensione(username, nomeRistorante, stelle, commento, data, risposta);
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
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            writer.writeNext(new String[]{
                "Username","NomeRistorante","Stelle","Commento","Data","Risposta"
            });

            for (Recensione r : elementi) {
                writer.writeNext(new String[]{
                    r.getAutore(),
                    r.getNomeRistorante(),
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