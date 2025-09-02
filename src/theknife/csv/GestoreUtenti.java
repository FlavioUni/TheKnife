/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/
package theknife.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import theknife.utente.Utente;
import theknife.logica.GestoreDate;
import theknife.utente.Ruolo;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * La classe GestoreUtenti si occupa della gestione della persistenza degli oggetti {@link Utente} da e verso il file CSV.
 * Estende la classe astratta {@link GestoreCSV} adattandola per il tipo Utente.
 * 
 * @author Lorenzo Gasparini
 * @see GestoreCSV
 * @see Utente
 * @see Ruolo
 * @see GestoreDate
 */

public class GestoreUtenti extends GestoreCSV<Utente> {
	
	/**
	 * Carica la lista degli utenti leggendo i dati da un file CSV.
	 * Il metodo ignora la prima riga (intestazione) e costruisce oggetti {@link Utente} a partire dalle righe successive.
	 * Gestisce automaticamente la conversione delle date e l'assegnazione del ruolo.
	 * 
	 * @param filePath Il percorso del file CSV da cui caricare i dati.
	 */
    @Override
    public void caricaDaCSV (String filePath) {
        elementi = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] campi;
            boolean primaRiga = true;

            while ((campi = reader.readNext()) != null) {
                if (primaRiga) { primaRiga = false; continue; }

                // Minimo: Nome..Ruolo => 7 colonne
                if (campi.length < 7) {
                    System.err.println("Riga utenti ignorata: colonne insufficienti (" + campi.length + ")");
                    continue;
                }
                for (int i = 0; i < campi.length; i++) campi[i] = campi[i].trim();

                String nome = campi[0];
                String cognome = campi[1];
                String username = campi[2];
                String password = campi[3];
                String domicilio = campi[4];
                
                LocalDate data = GestoreDate.parseNullable(campi[5]);
                
                Ruolo ruolo = Ruolo.valueOf(campi[6]);

                Utente u = new Utente(nome, cognome, username, password, domicilio, data, ruolo);
                if (campi.length > 7) u.setAssocKeysRaw(campi[7]);
                elementi.add(u);
            }

        } catch (IOException e) {
            System.err.println("Errore I/O nella lettura del file utenti: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Errore nella lettura/parsing utenti: " + e.getMessage());
        }
    }
    
    /**
     * Salva la lista corrente degli utenti nel file CSV di cui &egrave; specificato il percorso.
     * Crea una riga di intestazione e poi una riga per ogni utente, convertendo i suoi campi in stringhe secondo il formato atteso.
     * La lista dei ristoranti associati (preferiti o gestiti) viene serializzata in una stringa compatta.
     * 
     * @param filePath Il percorso del file CSV su cui salvare i dati.
     */
    @Override
    public void salvaSuCSV (String filePath) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            writer.writeNext(new String[]{
                "Nome", "Cognome", "Username", "Password", "Domicilio", "Data", "Ruolo", "Associati"
            });

            for (Utente u : elementi) {
                writer.writeNext(new String[]{
                    u.getNome(),
                    u.getCognome(),
                    u.getUsername(),
                    u.getPassword(),
                    u.getDomicilio(),
                    GestoreDate.formatOrEmpty(u.getData()),
                    u.getRuolo().name(),
                    u.getAssocKeysRaw() // ✅ Salva solo gli ID
                });
            }
        } catch (IOException e) {
            System.err.println("Errore nella scrittura del file utenti: " + e.getMessage());
        }
    }
    
}