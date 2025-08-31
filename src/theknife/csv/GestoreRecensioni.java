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
	            if (header) { header = false; continue; }
	            if (c == null || c.length < 4) continue; // min: user, nome, ..., stelle,...

	            for (int i = 0; i < c.length; i++) c[i] = (c[i] == null) ? "" : c[i].trim();

	            String username = c[0];
	            String nomeRist = c[1];

	            // --- 1) individua DATA con regex gg/MM/aaaa ---
	            int idxData = -1;
	            java.util.regex.Pattern pData = java.util.regex.Pattern.compile("^\\d{1,2}/\\d{1,2}/\\d{4}$");
	            for (int i = c.length - 1; i >= 2; i--) {
	                if (pData.matcher(c[i]).matches()) { idxData = i; break; }
	            }

	            java.time.LocalDate data = null;
	            if (idxData != -1) {
	                try {
	                    data = theknife.utente.GestoreDate.parseNullable(c[idxData]); // usa il tuo GestoreDate
	                } catch (Exception ex) {
	                    System.err.println("Data non valida '" + c[idxData] + "', la ignoro.");
	                    data = null;
	                }
	            }

	            // --- 2) individua STELLE (1..5) prima della data (o fine riga se data assente) ---
	            int idxStelle = -1, stelle = -1;
	            int stop = (idxData == -1 ? c.length - 1 : idxData - 1);
	            for (int i = stop; i >= 2; i--) {
	                try {
	                    int s = Integer.parseInt(c[i]);
	                    if (s >= 1 && s <= 5) { idxStelle = i; stelle = s; break; }
	                } catch (NumberFormatException ignored) { }
	            }
	            if (idxStelle == -1) {
	                System.err.println("Riga recensione ignorata: stelle non trovate");
	                continue;
	            }

	            // --- 3) LOCATION = join colonne [2 .. idxStelle-1] (ricompone "Torino, Italia") ---
	            String location = (idxStelle > 2)
	                    ? String.join(", ", java.util.Arrays.copyOfRange(c, 2, idxStelle))
	                    : "";

	            // --- 4) COMMENTO = join [idxStelle+1 .. idxData-1] oppure fino a fine riga se data assente ---
	            int commentEndExclusive = (idxData == -1 ? c.length : idxData);
	            String commento = (idxStelle + 1 < commentEndExclusive)
	                    ? String.join(", ", java.util.Arrays.copyOfRange(c, idxStelle + 1, commentEndExclusive))
	                    : "";

	            // --- 5) RISPOSTA = tutto dopo la data (se presente) ---
	            String risposta = (idxData != -1 && idxData + 1 < c.length)
	                    ? String.join(", ", java.util.Arrays.copyOfRange(c, idxData + 1, c.length))
	                    : "";

	            elementi.add(new theknife.recensione.Recensione(username, nomeRist, location, stelle, commento, data, risposta));
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