/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/

package theknife.utente;

import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

public class GestoreUtenti {
	private List<Utente> utenti;
	
	public GestoreUtenti () {
		this.utenti = new ArrayList<>();
	}
	
	public void aggiungiUtente (Utente u) {
		utenti.add(u);
	}
	
	public List<Utente> getUtenti () {
		return utenti;
	}
	
	public Utente trovaUtente (String username) {
		for (Utente u: utenti)
			if (u.getUsername().equalsIgnoreCase(username))
				return u;
		return null;
	}
	
	public void salvaSuFile (String nomeFile) throws IOException {
		try (FileWriter fw = new FileWriter(nomeFile)) {
			for (Utente u : utenti) {
				String riga = String.join
						(";",
						u.getNome(), u.getCognome(),
						u.getUsername(), u.getPassword(),
						u.getDomicilio(), u.getData() != null ? GestoreDate.format(u.getData()) : "",
						u.getRuolo().name()
						);
				fw.write(riga + "\n");
			}
		}
	}
	public void caricaDalFile (String nomeFile) throws IOException {
		utenti.clear();
		try (BufferedReader br = new BufferedReader (new FileReader (nomeFile))) {
			String riga;
			while ((riga = br.readLine()) != null) {
				String[] campi = riga.split(";");
				if (campi.length >= 7) {
					Utente u = new Utente (
							campi[0], campi[1], // nome e cognome
							campi[2], campi[3], // username e password
							campi[4], // domicilio
							campi[5].isEmpty() ? null : GestoreDate.parse(campi[5]), // data (se presente)
							Ruolo.valueOf(campi[6]) // ruolo
							);
					utenti.add(u);
				}
			}
		}
	}
	public boolean registrazione (Utente nuovo) {
		if (trovaUtente(nuovo.getUsername()) != null) {
			System.out.println("Username già esistente");
			return false;
		}
		utenti.add(nuovo);
		System.out.println("Registrazione avvenuta con successo");
		return true;
	}
}
