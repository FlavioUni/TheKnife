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
 * La classe GestoreUtenti gestisce la persistenza degli utenti sul file Utenti.csv.
 * Si occupa di caricare gli utenti dal file e di salvarli nuovamente su Utenti.csv.
 * 
 * L'implementazione della libreria OpenCSV ha semplficato e reso più robuste le operazione di 
 * lettura e scrittura dati (persistenza dati). Essa infatti offre metodi pronti per leggere 
 * e scrivere file CSV, rispettando lo standard (gestione di virgolette, caratteri speciali, separatori) 
 * evitando inoltre la gestione manuale riga per riga dei CSV (es. uso di split(",").
 * A ogni operazione di scrittura viene sovrascritto completamente il file CSV.
 * 
 * 
 * 
 * @author Gasparini Lorenzo
 * @author Ciani Flavio Angelo
 * @author Scolaro Gabriele
 */
public class GestoreUtenti extends GestoreCSV<Utente> {
	
    /**
     * Carica la lista degli utenti leggendo i dati da un file CSV.
     * Ignora la prima riga (intestazione) e costruisce gli oggetti Utente
     * dalle righe successive. Converte le date e assegna il ruolo.
     * 
     * @param filePath Percorso del file CSV da cui caricare i dati
     */
    @Override
    public void caricaDaCSV(String filePath) {
        elementi = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] campi;
            boolean primaRiga = true;

            while ((campi = reader.readNext()) != null) {
                if (primaRiga) { primaRiga = false; continue; }

                // Minimo 7 colonne
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
     * Salva la lista corrente degli utenti nel file CSV indicato.
     * Scrive una riga di intestazione e poi una riga per ogni utente,
     * serializzando la lista dei ristoranti associati in formato compatto (vedi Utente).
     * 
     * @param filePath Percorso del file CSV su cui salvare i dati
     */
    @Override
    public void salvaSuCSV(String filePath) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {        //new FileWriter(filePath, true) per append
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
                    u.getAssocKeysRaw()
                });
            }
        } catch (IOException e) {
            System.err.println("Errore nella scrittura del file utenti: " + e.getMessage());
        }
    }
}