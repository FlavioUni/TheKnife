/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/
package theknife.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import theknife.utente.Utente;
import theknife.utente.Ruolo;
import theknife.utente.GestoreDate;

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
                if (primaRiga) { primaRiga = false; continue; }

                // minimo: Nome..Ruolo => 7 colonne
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

    @Override
    public void salvaSuCSV(String filePath) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            writer.writeNext(new String[]{
                "Nome","Cognome","Username","Password","Domicilio","Data","Ruolo","PreferitiOGestiti"
            });

            for (Utente u : elementi) {
            	String preferitiOGestiti = buildAssocCell(u);
                writer.writeNext(new String[]{
                    u.getNome(),
                    u.getCognome(),
                    u.getUsername(),
                    u.getPassword(),
                    u.getDomicilio(),
                    GestoreDate.formatOrEmpty(u.getData()),
                    u.getRuolo().name(),
                    preferitiOGestiti
                });
            }
        } catch (IOException e) {
            System.err.println("Errore nella scrittura del file utenti: " + e.getMessage());
        }
    }
    
    private static String buildAssocCell(Utente u) {
        java.util.List<theknife.ristorante.Ristorante> src =
            (u.getRuolo() == theknife.utente.Ruolo.CLIENTE)
            ? u.getRistorantiPreferiti()
            : u.getRistorantiGestiti();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < src.size(); i++) {
            theknife.ristorante.Ristorante r = src.get(i);
            if (i > 0) sb.append(';');
            sb.append(r.getNome()).append('|').append(r.getLocation());
        }
        return sb.toString();
    }
}