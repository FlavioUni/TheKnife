package theknife.utente;

import java.util.Scanner;

import theknife.csv.GestoreUtenti;

public class PaginaIniziale {
	public static void main (String[] args) {
		Scanner sc = new Scanner(System.in);
		GestoreUtenti gestoreUtenti = new GestoreUtenti();
		// gestoreUtenti.caricaDalFile("data/utenti.csv", gestoreRistoranti);
		boolean continua = true;
		while (continua) {
			 System.out.println("\n--------- MENU PRINCIPALE ---------");
			 System.out.println("1. Registrazione");
	         System.out.println("2. Login");
	         System.out.println("3. Esci");
	         System.out.print("Scelta (da 1 a 3): ");
	         int scelta = sc.nextInt();
	         sc.nextLine();
	         
	         switch (scelta) {
	         	case 1: // Registrazione
	         		System.out.print("Nome: ");
                    String nome = sc.nextLine();
                    System.out.print("Cognome: ");
                    String cognome = sc.nextLine();
                    System.out.print("Username: ");
                    String username = sc.nextLine();
                    System.out.print("Password: ");
                    String password = sc.nextLine();
                    System.out.print("Domicilio: ");
                    String domicilio = sc.nextLine();
                    System.out.print("Data di nascita (facoltativa): ");
                    
                    System.out.print("Ruolo (CLIENTE/RISTORATORE): ");
                    String ruoloStr = sc.nextLine();
                    Ruolo ruolo = Ruolo.valueOf(ruoloStr.toUpperCase());
                    
                    Utente nuovo = new Utente(nome, cognome, username, password, domicilio, null, ruolo);
                    gestoreUtenti.registrazione(nuovo);
                    break;
	         }
		}
	}
}
