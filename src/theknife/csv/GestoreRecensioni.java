/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/

package theknife.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import theknife.recensione.Recensione;

import java.util.ArrayList;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class GestoreRecensioni extends GestoreCSV<Recensione> {
	
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	
    @Override
    public void caricaDaCSV(String filePath) {
        elementi = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] campi;
            boolean primaRiga = true;

            while ((campi = reader.readNext()) != null) {
                if (primaRiga) {
                    primaRiga = false;
                    continue; // Salta intestazione
                }

                String username = campi[0];
                String nomeRistorante = campi[1];
                int stelle = Integer.parseInt(campi[2]);
                String commento = campi[3];
                LocalDate data = LocalDate.parse(campi[4], formatter);
                String risposta = campi.length > 5 ? campi[5] : "";

                Recensione r = new Recensione(username, nomeRistorante, stelle, commento, data, risposta);
                elementi.add(r);
            }

        } catch (IOException e) {
            System.err.println("Errore di I/O nella lettura del file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Errore nella lettura del file: " + e.getMessage());
        }
    }

    @Override
    public void salvaSuCSV(String filePath) {

        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            // Scrive intestazione
            String[] intestazione = { "Username", "NomeRistorante", "Stelle", "Commento", "Data", "Risposta" };
            writer.writeNext(intestazione);

            // Scrive ogni recensione
            for (Recensione r : elementi) {
                String[] riga = {
                    r.getAutore(),
                    r.getNomeRistorante(),
                    String.valueOf(r.getStelle()),
                    r.getDescrizione(),
                    r.getData().format(formatter),
                    r.getRisposta()
                };
                writer.writeNext(riga);
            }

        } catch (IOException e) {
            System.err.println("Errore nella scrittura del file: " + e.getMessage());
        }
    }
}