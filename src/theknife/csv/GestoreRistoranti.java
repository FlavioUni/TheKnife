/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/
package theknife.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;

import theknife.ristorante.Ristorante;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * La classe GestoreRistoranti gestisce la persistenza dei ristoranti sul file Ristoranti.csv.
 * Si occupa di caricare i ristoranti dal file e di salvarli nuovamente su Ristoranti.csv.
 * 
 * L’utilizzo della libreria OpenCSV semplifica e rende più robuste le operazioni di
 * lettura e scrittura dei dati, grazie alla gestione automatica di virgolette,
 * caratteri speciali e separatori, evitando l’uso di split manuali.
 * Ad ogni operazione di scrittura viene sovrascritto completamente il file CSV.
 * 
 * Autori:
 * @author Gasparini Lorenzo
 * @author Ciani Flavio Angelo
 * @author Scolaro Gabriele
 */
public class GestoreRistoranti extends GestoreCSV<Ristorante> {

    /**
     * Carica la lista dei ristoranti leggendo i dati da un file CSV.
     * Ignora la prima riga (intestazione) e costruisce gli oggetti Ristorante
     * dalle righe successive. Converte i campi numerici e booleani.
     * 
     * @param filePath Percorso del file CSV da cui caricare i dati
     */
    @Override
    public void caricaDaCSV(String filePath) {
        elementi = new ArrayList<>();

        try {
            CSVParser parser = new CSVParserBuilder()
                    .withSeparator(',')
                    .withQuoteChar('"')
                    .withIgnoreQuotations(false)
                    .build();

            CSVReader reader = new CSVReaderBuilder(new FileReader(filePath))
                    .withCSVParser(parser)
                    .build();

            String[] riga;
            boolean header = true;

            while ((riga = reader.readNext()) != null) {
                if (header) {
                    header = false;
                    continue;
                }

                if (riga.length < 14) {
                    System.err.println("Riga ristoranti ignorata: colonne insufficienti (" + riga.length + ")");
                    continue;
                }

                for (int i = 0; i < riga.length; i++) {
                    riga[i] = riga[i] != null ? riga[i].trim() : "";
                }

                String id = riga[0];
                if (id == null || id.isBlank()) {
                    System.err.println("Ristorante senza ID: " + riga[1]);
                    continue;
                }

                String nome = riga[1];
                String indirizzo = riga[2];
                String location = riga[3];
                String prezzo = riga[4];
                String cucina = riga[5];

                double longitudine = parseDouble(riga[6]);
                double latitudine = parseDouble(riga[7]);

                String telefono = riga[8];
                String websiteUrl = riga[9];
                String premi = riga[10];
                String servizi = riga[11];

                boolean prenotazioneOnline = parseBool(riga[12]);
                boolean delivery = parseBool(riga[13]);

                Ristorante ristorante = new Ristorante(nome, indirizzo, location, prezzo, cucina,
                        longitudine, latitudine, telefono, websiteUrl,
                        premi, servizi, prenotazioneOnline, delivery);

                ristorante.setId(id);
                elementi.add(ristorante);
            }

        } catch (IOException e) {
            System.err.println("Errore I/O durante la lettura dei ristoranti: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Errore durante il parsing dei ristoranti: " + e.getMessage());
        }
    }

    /**
     * Salva la lista corrente dei ristoranti nel file CSV indicato.
     * Scrive una riga di intestazione e poi una riga per ogni ristorante,
     * convertendo i campi nei formati corretti.
     * 
     * @param filePath Percorso del file CSV su cui salvare i dati
     */
    @Override
    public void salvaSuCSV(String filePath) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            writer.writeNext(new String[]{
                "ID", "Nome", "Indirizzo", "Location", "Prezzo", "Cucina",
                "Longitudine", "Latitudine", "Telefono", "SitoWeb",
                "Premio", "Servizi", "PrenotazioneOnline", "Delivery"
            });

            for (Ristorante r : elementi) {
                writer.writeNext(new String[]{
                    r.getId(),
                    r.getNome(),
                    r.getIndirizzo(),
                    r.getLocation(),
                    r.getPrezzoMedio(),
                    r.getCucina(),
                    String.valueOf(r.getLongitudine()),
                    String.valueOf(r.getLatitudine()),
                    r.getNumeroTelefono(),
                    r.getWebsiteUrl(),
                    r.getPremi(),
                    r.getServizi(),
                    r.isPrenotazioneOnline() ? "SI" : "NO",
                    r.isDelivery() ? "SI" : "NO"
                });
            }
        } catch (IOException e) {
            System.err.println("Errore durante la scrittura dei ristoranti: " + e.getMessage());
        }
    }

    // === METODI DI SUPPORTO ===

    /**
     * Converte una stringa in un numero decimale (double).
     * Se la stringa è nulla o vuota, restituisce 0.0.
     * Accetta sia virgola che punto come separatore decimale.
     * 
     * @param valore Stringa da convertire
     * @return Numero decimale corrispondente
     */
    private static double parseDouble(String valore) {
        if (valore == null) return 0.0;
        valore = valore.trim();
        if (valore.isEmpty()) return 0.0;
        return Double.parseDouble(valore.replace(',', '.'));
    }

    /**
     * Converte una stringa in un valore booleano.
     * Restituisce true se la stringa è "si", "sì" o "true" (non fa distinzione tra maiuscole/minuscole).
     * Altrimenti restituisce false.
     * 
     * @param valore Stringa da controllare
     * @return true o false
     */
    private static boolean parseBool(String valore) {
        if (valore == null) return false;
        String v = valore.trim();
        return v.equalsIgnoreCase("si") || v.equalsIgnoreCase("sì") || v.equalsIgnoreCase("true");
    }
}