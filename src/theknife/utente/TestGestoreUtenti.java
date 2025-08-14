package theknife.utente;

import java.time.LocalDate;

public class TestGestoreUtenti {
	public static void main (String[] args) {
		GestoreUtenti gestore = new GestoreUtenti();
		
		gestore.aggiungiUtente(new Utente(
				"Gianfranco", "Milani", "gianmilani", "gnfrnc18", "Monza", LocalDate.of(1989, 11, 02), Ruolo.CLIENTE)
				);
		gestore.aggiungiUtente(new Utente(
				"Ferdinanda", "Saluti", "nandasaluti", "slt3276", "Catanzaro", LocalDate.of(1980, 02, 23), Ruolo.CLIENTE)
				);
		gestore.aggiungiUtente(new Utente(
				"Edoardo", "Esposito", "eesposito", "ee12drd", "Napoli", LocalDate.of(1999, 06, 30), Ruolo.CLIENTE)
				);
		
		try {
			gestore.salvaSuFile("data/utenti.csv");
			System.out.println("File salvato con successo");
			
			GestoreUtenti gestore2 = new GestoreUtenti();
			gestore2.caricaDalFile("data/utenti.csv");
			System.out.println("Utenti caricati dal file:");
			
			for (Utente u : gestore2.getUtenti()) {
				System.out.println(u.getNome() + " " + u.getCognome() + " " + u.getUsername());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
