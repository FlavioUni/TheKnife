/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/

package theknife.utente;

public class Utente {
	// dichiarazione attributi
	private String nome, cognome, username, password, domicilio;
	private LocalDate data;
	private Ruolo ruolo;
	
	//costruttore
	public Utente (String a1, String a2, String a3, String a4, String a5, LocalDate a6, Ruolo a7) {
		this.nome = a1;
		this.cognome = a2;
		this.username = a3;
		this.password = a4;
		this.domicilio = a5;
		this.data = a6;
		this.ruolo = a7;
	}
}
