/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/

package theknife.ristorante;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GestioneRistorante {

	private List<Ristorante> ristoranti = new ArraytList<>();
	
	
	public void caricaDaCSV(String filePath) {
		try(BufferedReader br = new BufferedReader(new FileReader(filePath))){
			String riga;
			while((riga = br.readLine()) != null) {
				String[] c = riga.split(",");
				String location = c[2];
				String luogo = location.split(",\\s*");
				String citta = luogo.length() > 0 ? luogo[0] : "";
				String nazione = luogo.length() > 1 ? luogo[1] : "";
				if(c.length < 15) continue;
				Ristorante r = new Ristorante(c[0], c[1], citta, nazione,
		                Double.parseDouble(c[4]), Double.parseDouble(c[5]),
		                Double.parseDouble(c[6]),
		                Boolean.parseBoolean(c[7]), Boolean.parseBoolean(c[8]),
		                c[9], c[10], c[11],
		                Integer.parseInt(c[12]), Integer.parseInt(c[13]), c[14]);
				
				listaRistoranti.add(r);
			}
		} catch(IOException e) {
			System.err.println("Errore nella lettura del file CSV: " + e.getMessage());
		} catch (NumberFormatException e) {
            System.err.println("Errore nel formato dei dati numerici: " + e.getMessage());
        }

	}
	
	public List<Ristorante> getRistoranti(){
		return ristoranti;
	}
	
	public void salvaSuCSV(String filePath) {
	    try (PrintWriter writer = new PrintWriter(new FileWriter(percorsoFile))) {
	        for (Ristorante r : ristoranti) {
	            writer.println(r.toCSV()); // Usa il metodo toCSV() della classe Ristorante
	        }
	        System.out.println("Salvataggio completato.");
	    } catch (IOException e) {
	        System.err.println("Errore nel salvataggio del file: " + e.getMessage());
	    }
	}
	
	public void aggiungiRistoranteDaInput(Scanner sc, String filePath) {
	    try {
	        System.out.println("=== Inserimento Nuovo Ristorante ===");
	        System.out.print("Nome: ");
	        String nome = sc.nextLine();

	        System.out.print("Indirizzo: ");
	        String indirizzo = sc.nextLine();

	        System.out.print("Città: ");
	        String citta = sc.nextLine();

	        System.out.print("Nazione: ");
	        String nazione = sc.nextLine();

	        System.out.print("Latitudine: ");
	        double latitudine = Double.parseDouble(sc.nextLine());

	        System.out.print("Longitudine: ");
	        double longitudine = Double.parseDouble(sc.nextLine());

	        System.out.print("Fascia di prezzo (€): ");
	        double fasciaPrezzo = Double.parseDouble(sc.nextLine());

	        System.out.print("Disponibile delivery (true/false): ");
	        boolean delivery = Boolean.parseBoolean(sc.nextLine());

	        System.out.print("Prenotazione online disponibile (true/false): ");
	        boolean prenotazioneOnline = Boolean.parseBoolean(sc.nextLine());

	        System.out.print("Tipo di cucina: ");
	        String tipoCucina = sc.nextLine();

	        System.out.print("Numero di telefono: ");
	        String numeroTelefono = sc.nextLine();

	        System.out.print("Sito web: ");
	        String websiteUrl = sc.nextLine();

	        System.out.print("Numero di premi: ");
	        int premi = Integer.parseInt(sc.nextLine());

	        System.out.print("Valutazione (stelle da 1 a 5): ");
	        int stelle = Integer.parseInt(sc.nextLine());

	        System.out.print("Descrizione: ");
	        String descrizione = sc.nextLine();

	        // Crea oggetto Ristorante
	        Ristorante nuovo = new Ristorante(
	            nome, indirizzo, citta, nazione, latitudine, longitudine, fasciaPrezzo,
	            delivery, prenotazioneOnline, tipoCucina, numeroTelefono,
	            websiteUrl, premi, stelle, descrizione
	        );

	        // Aggiunge e salva
	        ristoranti.add(nuovo);
	        salvaSuCSV(filePath);

	        System.out.println("✅ Ristorante aggiunto e salvato con successo.");

	    } catch (Exception e) {
	        System.err.println("Errore durante l'inserimento: " + e.getMessage());
	    }
	}
	
	public void rimuoviRistoranteDaInput(Scanner sc, String filePath) {
	    System.out.println("=== Rimozione Ristorante ===");
	    System.out.print("Inserisci il nome (anche parziale) del ristorante da cercare: ");
	    String inputNome = sc.nextLine();

	    // Trova tutti i ristoranti che iniziano o contengono il nome inserito
	    List<Ristorante> trovati = new ArrayList<>();
	    for (Ristorante r : ristoranti) {
	        if (r.getNome().toLowerCase().contains(inputNome.toLowerCase())) {
	            trovati.add(r);
	        }
	    }

	    if (trovati.isEmpty()) {
	        System.out.println("❌ Nessun ristorante trovato con quel nome.");
	        return;
	    }

	    System.out.println("\nRistoranti trovati:");
	    for (int i = 0; i < trovati.size(); i++) {
	        System.out.println("[" + i + "] " + trovati.get(i).toString());
	    }

	    System.out.print("\nInserisci il numero del ristorante da eliminare (oppure -1 per annullare): ");
	    int scelta;
	    try {
	        scelta = Integer.parseInt(sc.nextLine());
	    } catch (NumberFormatException e) {
	        System.out.println("❌ Input non valido. Operazione annullata.");
	        return;
	    }

	    if (scelta >= 0 && scelta < trovati.size()) {
	        Ristorante daEliminare = trovati.get(scelta);
	        System.out.print("Confermi l'eliminazione di \"" + daEliminare.getNome() + "\"? (s/n): ");
	        String conferma = sc.nextLine().trim().toLowerCase();

	        if (conferma.equals("s")) {
	            ristoranti.remove(daEliminare);
	            salvaSuCSV(filePath);
	            System.out.println("✅ Ristorante eliminato con successo.");
	        } else {
	            System.out.println("ℹ️ Eliminazione annullata.");
	        }
	    } else {
	        System.out.println("ℹ️ Nessuna azione eseguita.");
	    }
	}


	
	
	
	
}
