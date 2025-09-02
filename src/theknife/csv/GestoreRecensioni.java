/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/
package theknife.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import theknife.logica.GestoreDate;
import theknife.recensione.Recensione;

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
	            if (header) { header = false; continue; }
	            if (c == null || c.length < 4) continue; // min: user, idRist, stelle,...

	            for (int i = 0; i < c.length; i++) c[i] = (c[i] == null) ? "" : c[i].trim();

	            String username = c[0];
	            String idRistorante = c[1];

	            int stelle;
	            try {
	                stelle = Integer.parseInt(c[2]);
	            } catch (NumberFormatException e) {
	                System.err.println("Stelle non valide nella riga: " + String.join(",", c));
	                continue;
	            }

	            String commento = c[3];
	            LocalDate data = GestoreDate.parseNullable(c[4]);
	            String risposta = (c.length >= 6) ? c[5] : "";

	            elementi.add(new Recensione(username, idRistorante, stelle, commento, data, risposta));
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
                "Username", "IDRistorante",
                "Stelle", "Commento", "Data", "Risposta"
            });

            for (Recensione r : elementi) {
                w.writeNext(new String[]{
                    r.getAutore(),
                    r.getIdRistorante(),
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