/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/

package theknife.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import theknife.utente.Utente;
import theknife.utente.GestoreDate;
import theknife.utente.Ruolo;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class GestoreUtenti extends GestoreCSV<Utente> {

    @Override
    public void caricaDaCSV(String filePath) {
        elementi = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] campi;
            boolean primaRiga = true;

            while ((campi = reader.readNext()) != null) {
                if (primaRiga) {
                    primaRiga = false;
                    continue;
                }

                String nome = campi[0];
                String cognome = campi[1];
                String username = campi[2];
                String password = campi[3];
                String domicilio = campi[4];
                LocalDate data = theknife.utente.GestoreDate.parseNullable(campi[5]);
                Ruolo ruolo = Ruolo.valueOf(campi[6]);

                Utente u = new Utente(nome, cognome, username, password, domicilio, data, ruolo);
                elementi.add(u);
            }

        } catch (IOException e) {
            System.err.println("Errore I/O nella lettura del file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Errore nella lettura del file utenti: " + e.getMessage());
        }
    }

    @Override
    public void salvaSuCSV(String filePath) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            // intestazione
            String[] intestazione = {
                "Nome", "Cognome", "Username", "Password", "Domicilio", "Data", "Ruolo"
            };
            writer.writeNext(intestazione);

            for (Utente u : elementi) {
                String[] riga = {
                    u.getNome(),
                    u.getCognome(),
                    u.getUsername(),
                    u.getPassword(),
                    u.getDomicilio(),
                    GestoreDate.formatOrEmpty(u.getData()),
                    u.getRuolo().name()
                };
                writer.writeNext(riga);
            }

        } catch (IOException e) {
            System.err.println("Errore nella scrittura del file utenti: " + e.getMessage());
        }
    }
}