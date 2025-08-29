package theknife.utente;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import theknife.csv.GestoreRecensioni;
import theknife.csv.GestoreRistoranti;

import theknife.csv.GestoreUtenti;
import theknife.ristorante.Ristorante;
import theknife.logica.UtenteService;

public class MenuHandler {
	private Scanner sc;
	private GestoreUtenti gestoreUtenti;
	private GestoreRistoranti gestoreRistoranti;
	private UtenteService utenteService;
	private GestoreRecensioni gestoreRecensioni;
		
	public MenuHandler () {
		sc = new Scanner(System.in);
		gestoreUtenti = new GestoreUtenti();
		gestoreRistoranti = new GestoreRistoranti();
		utenteService = new UtenteService(gestoreUtenti.getElementi());
		gestoreRecensioni = new GestoreRecensioni();
	}
		
	public void avvia () {
		gestoreUtenti.caricaDaCSV("data/utenti.csv");
        gestoreRistoranti.caricaDaCSV("data/ristoranti.csv");
		boolean continua = true;
		while (continua) {
			System.out.println("\n--------- MENU PRINCIPALE ---------");
			System.out.println("1. Registrazione");
			System.out.println("2. Login");
			System.out.println("3. Esci");
			System.out.print("Scelta (da 1 a 3): ");
			int scelta = Integer.parseInt(sc.nextLine());
		         
			switch (scelta) {
			case 1 -> registrazione();
			case 2 -> login();
			case 3 -> {
				continua = false;
				gestoreUtenti.salvaSuCSV("data/utenti.csv");
                gestoreRistoranti.salvaSuCSV("data/ristoranti.csv");
                gestoreRecensioni.salvaSuCSV("data/recensioni.csv");
				System.out.println("Chiusura programma in corso.");
			}
			default -> System.out.println("Scelta non valida.");
			}
		}
	}
	// REGISTRAZIONE
	private void registrazione () {
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
        System.out.print("Data di nascita (o premere invio per saltare): ");
        String dataInput = sc.nextLine();
        LocalDate dataNascita = null;
        if (!dataInput.isEmpty()) {
            try {
                dataNascita = GestoreDate.parse(dataInput);
            } catch (DateTimeParseException e) {
                System.out.println("Formato della data non valido, verrà ignorata.");
            }
        }
        System.out.print("Ruolo (CLIENTE/RISTORATORE): ");
        String ruoloStr = sc.nextLine();
        Ruolo ruolo = Ruolo.valueOf(ruoloStr.toUpperCase());
        
        Utente nuovo = new Utente(nome, cognome, username, password, domicilio, null, ruolo);
        utenteService.registrazione(nuovo);
	}
	
	// LOGIN
	private void login () {
		System.out.print("Username: ");
        String user = sc.nextLine();
        System.out.print("Password: ");
        String pass = sc.nextLine();
        Utente logged = utenteService.login(user, pass);
        if (logged != null) {
            if (logged.getRuolo() == Ruolo.CLIENTE) {
                menuCliente(logged);
            } else if (logged.getRuolo() == Ruolo.RISTORATORE) {
                menuRistoratore(logged);
            }
        }
	}
	         
	// MENU CLIENTE
	private void menuCliente (Utente utente) {
        boolean continua = true;
        while (continua) {
            System.out.println("\n--------- MENU CLIENTE ---------");
            System.out.println("1. Visualizza preferiti");
            System.out.println("2. Aggiungi preferito");
            System.out.println("3. Rimuovi preferito");
            System.out.println("4. Ricerca ristoranti");
            System.out.println("5. Aggiungi recensione");
            System.out.println("6. Visualizza le recensioni di un ristorante");
            System.out.println("7. Logout");
            System.out.print("Scelta: ");
            int scelta = Integer.parseInt(sc.nextLine());
            
            switch (scelta) {
            case 1: utenteService.visualizzaPreferiti(utente.getUsername()); break;
            case 2:
                System.out.print("Nome ristorante da aggiungere: ");
                String r1 = sc.nextLine();
                Ristorante r = gestoreRistoranti.trovaRistorante(r1);
                if (r != null) utenteService.aggiungiPreferito(utente.getUsername(), r);
                else System.out.println("Ristorante non trovato.");
                break;
            case 3:
            	System.out.print("Nome ristorante da rimuovere: ");
                String r2 = sc.nextLine();
                Ristorante ri = gestoreRistoranti.trovaRistorante(r2);
                if (ri != null) utenteService.rimuoviPreferito(utente.getUsername(), ri);
                else System.out.println("Ristorante non trovato.");
                break;
            case 4:
            	System.out.print("Tipo cucina (invio per nessun filtro): ");
                String tipo = sc.nextLine();
                System.out.print("Città (invio per nessun filtro): ");
                String citta = sc.nextLine();
                System.out.print("Prezzo massimo: ");
                double prezzo = Double.parseDouble(sc.nextLine());
                gestoreRistoranti.ricercaRistoranti
                (
                        tipo.isEmpty() ? null : tipo,
                        citta.isEmpty() ? null : citta,
                        prezzo
        		);
                break;
            case 5:
            	aggiungiRecensione(utente);
            	break;
            case 6:
            	visualizzaRecensioni();
            	break;
            case 7:
            	continua = false;
            default: System.out.println("Scelta non valida.");
            }
        }
	}
	private void menuRistoratore (Utente utente) {
		boolean continua = true;
        while (continua) {
            System.out.println("\n--------- MENU RISTORATORE ---------");
            System.out.println("1. Visualizza ristoranti gestiti");
            System.out.println("2. Aggiungi ristorante gestito");
            System.out.println("3. Rimuovi ristorante gestito");
            System.out.println("4. Visualizza le recensioni sui miei ristoranti");
            System.out.println("5. Logout");
            System.out.print("Scelta: ");
            int scelta = Integer.parseInt(sc.nextLine());
            
            switch (scelta) {
            case 1: utenteService.visualizzaRistorantiGestiti(utente.getUsername()); break;
            case 2:
                System.out.print("Nome ristorante da aggiungere: ");
                String r1 = sc.nextLine();
                Ristorante r = gestoreRistoranti.trovaRistorante(r1);
                if (r != null) {
                    utenteService.aggiungiPreferito(utente.getUsername(), r);
                } else {
                    System.out.println("Ristorante non trovato.");
                }
                break;
            case 3:
                System.out.print("Nome ristorante da rimuovere: ");
                String r2 = sc.nextLine();
                Ristorante ri = gestoreRistoranti.trovaRistorante(r2);
                if (ri != null) utenteService.rimuoviRistoranteGestito(utente.getUsername(), ri);
                else System.out.println("Ristorante non trovato.");
                break;
            case 4: continua = false; break;
            default: System.out.println("Scelta non valida."); 
            }
        }
	}
}
