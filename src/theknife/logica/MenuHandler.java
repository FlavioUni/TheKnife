/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/
package theknife.logica;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import theknife.ristorante.Ristorante;
import theknife.utente.*;
import theknife.eccezioni.InputAnnullatoException;
import theknife.recensione.Recensione;

public class MenuHandler {

    // ====== HARD-CODE ======
    private static final String UTENTI_CSV     = "data/Utenti.csv";
    private static final String RISTORANTI_CSV = "data/Ristoranti.csv";
    private static final String RECENSIONI_CSV = "data/Recensioni.csv";

    private Scanner sc = new Scanner(System.in);
	private DataContext data = new DataContext();
	private UtenteService utenteService;
	private RistoranteService ristoranteService;
    private RecensioneService recensioneService;
    private GeoService geoService = new GeoService();

    public MenuHandler () {
        // Carica tutto in RAM
        data.loadAll(UTENTI_CSV, RISTORANTI_CSV, RECENSIONI_CSV);

        // Inizializza i service
        utenteService = new UtenteService(data);
        ristoranteService = new RistoranteService(data, geoService);
        recensioneService = new RecensioneService(data);
    }

    public void avvia () {
    	try {
	        boolean continua = true;
	        while (continua) {
	            System.out.println("\n--------- MENU PRINCIPALE ---------");
	            System.out.println("1) Registrazione");
	            System.out.println("2) Login");
	            System.out.println("3) Continua come ospite");
	            System.out.println("4) Esci");
	            System.out.print("Scelta: ");
	            int scelta = leggiInt();
	
	            switch (scelta) {
				case 1 -> registrazione();
				case 2 -> login();
				case 3 -> menuOspite();
				case 4 -> {
					continua = false;
					System.out.println("Chiusura programma in corso.");
				}
				default -> System.out.println("Scelta non valida.");
				}
	        }
    	} // Gestione annullamento operazioni
        catch (InputAnnullatoException e) {
            System.out.println("Operazione annullata, ritorno al menu principale.");
        } // Gestione altri errori
        catch (Exception e) {
            System.err.println("Errore imprevisto: " + e.getMessage());
            e.printStackTrace();
    	} finally {
    		try {
                data.saveAll(UTENTI_CSV, RISTORANTI_CSV, RECENSIONI_CSV);
                geoService.shutdown();
                System.out.println("Dati salvati e risorse rilasciate.");
            } catch (Exception e) {
                System.err.println("Errore durante il salvataggio: " + e.getMessage());
            }
    	}
    }

    // OSPITE
    private void menuOspite () {
        boolean continua = true;
        while (continua) {
            System.out.println("\n--------- MENU OSPITE ---------");
            System.out.println("1) Elenco ristoranti (rapido)");
            System.out.println("2) Cerca ristoranti");
            System.out.println("3) Visualizza le recensioni di un ristorante");
            System.out.println("4) Cerca ristoranti vicino a un indirizzo");
            System.out.println("5) Torna indietro");
            System.out.print("Scelta: ");
            int scelta = leggiInt();

            switch (scelta) {
                case 1 -> stampaElencoRistoranti(data.getRistoranti());
                case 2 -> cercaRistorantiConFiltri();
                case 3 -> visualizzaRecensioniRistorante();
                case 4 -> cercaVicinoAMe();              // <--- QUI LA CHIAMATA
                case 5 -> continua = false;
                default -> System.out.println("Scelta non valida.");
            }
        }
    }

 // REGISTRAZIONE
    private void registrazione () {
        System.out.println("\n--- REGISTRAZIONE ---");
        System.out.print("Nome: ");
        String nome = sc.nextLine().trim();

        System.out.print("Cognome: ");
        String cognome = sc.nextLine().trim();

        System.out.print("Username: ");
        String username = leggiUsernameDisponibile();

        System.out.print("Password: ");
        String password = leggiPasswordValida();

        System.out.print("Domicilio: ");
        String domicilio = sc.nextLine().trim();

        System.out.print("Data di nascita DD/MM/YYYY (o premere invio per saltare): ");
        String dataInput = sc.nextLine().trim();
        LocalDate dataNascita = null;
        if (!dataInput.isEmpty()) {
            try {
                dataNascita = GestoreDate.parse(dataInput);
            } catch (IllegalArgumentException e) {
                System.out.println("Formato della data non valido, ignoro la data.");
            }
        }

        System.out.print("Ruolo (CLIENTE/RISTORATORE): ");
        Ruolo ruolo = leggiRuolo();

        Utente nuovo = new Utente(nome, cognome, username, password, domicilio, dataNascita, ruolo);
        boolean ok = utenteService.registrazione(nuovo);

        // <-- qui la riga che volevi -->
        System.out.println(ok ? "Registrazione completata." : "Registrazione NON riuscita.");
    }
    // LOGIN
    private void login () {
        System.out.println("\n--- Login ---");
        System.out.print("Username: ");
        String u = sc.nextLine().trim();
        System.out.print("Password: ");
        String p = sc.nextLine();

        Utente loggato = utenteService.login(u, p);
        if (loggato == null) return;

        if (loggato.getRuolo() == Ruolo.CLIENTE) {
            menuCliente(loggato);
        } else if (loggato.getRuolo() == Ruolo.RISTORATORE) {
            menuRistoratore(loggato);
        } else {
            System.out.println("Ruolo non gestito.");
        }
    }

    // MENU CLIENTE
    private void menuCliente (Utente utente) {
        boolean continua = true;
        while (continua) {
            System.out.println("\n--------- MENU CLIENTE ---------");
            System.out.println("1) Visualizza preferiti");
            System.out.println("2) Aggiungi preferito");
            System.out.println("3) Rimuovi preferito");
            System.out.println("4) Cerca ristoranti");
            System.out.println("5) Aggiungi recensione");
            System.out.println("6) Visualizza le recensioni di un ristorante");
            System.out.println("7) Cerca ristoranti vicino a un indirizzo"); // <--- NUOVA
            System.out.println("8) Logout");
            System.out.print("Scelta: ");
            int scelta = leggiInt();

            switch (scelta) {
                case 1 -> utenteService.visualizzaPreferiti(utente.getUsername());
                case 2 -> {
                    Ristorante r = chiediRistorante();
                    if (r != null) {
                        boolean ok = ristoranteService.aggiungiPreferito(utente, r);
                        System.out.println(ok ? "Aggiunto ai preferiti." : "Non aggiunto.");
                    }
                }
                case 3 -> {
                    Ristorante r = chiediRistorante();
                    if (r != null) {
                        boolean ok = ristoranteService.rimuoviPreferito(utente, r);
                        System.out.println(ok ? "Rimosso dai preferiti." : "Non rimosso.");
                    }
                }
                case 4 -> cercaRistorantiConFiltri();
                case 5 -> {
                    Ristorante r = chiediRistorante();
                    if (r == null) break;
                    System.out.print("Stelle (1-5): ");
                    int stelle = leggiInt();
                    System.out.print("Commento: ");
                    String testo = sc.nextLine();
                    try {
                        Recensione rec = recensioneService.aggiungiRecensione(utente, r, stelle, testo);
                        System.out.println(rec != null ? "Recensione aggiunta." : "Impossibile aggiungere.");
                    } catch (Exception e) {
                        System.out.println("Errore: " + e.getMessage());
                    }
                }
                case 6 -> visualizzaRecensioniRistorante();
                case 7 -> cercaVicinoAMe();              // <--- QUI LA CHIAMATA
                case 8 -> continua = false;
                default -> System.out.println("Scelta non valida.");
            }
        }
    }

    // MENU RISTORATORE
    private void menuRistoratore (Utente ristoratore) {
        boolean continua = true;
        while (continua) {
        	System.out.println("\n------ MENU RISTORATORE ------");
        	System.out.println("1) Visualizza ristoranti gestiti");
        	System.out.println("2) Aggiungi NUOVO ristorante al catalogo (e alla tua gestione)");
        	System.out.println("3) Aggiungi ristorante esistente dal catalogo alla tua gestione");
        	System.out.println("4) Rimuovi ristorante gestito");
        	System.out.println("5) Visualizza le recensioni dei miei ristoranti");
        	System.out.println("6) Rispondi a una recensione");
        	System.out.println("7) Logout");
        	System.out.print("Scelta: ");
            int scelta = leggiInt();

            switch (scelta) {
            case 1 -> utenteService.visualizzaRistorantiGestiti(ristoratore.getUsername());

            case 2 -> { // NUOVO RISTORANTE NEL CATALOGO
                Ristorante nuovo = creaRistoranteDaInput();
                if (nuovo == null) break; // utente ha annullato
                boolean ok = ristoranteService.aggiungiRistorante(ristoratore, nuovo);
                System.out.println(ok ? "Ristorante creato e aggiunto alla tua gestione."
                                      : "Impossibile creare/aggiungere (esiste già nome+location?).");
            }

            case 3 -> { // collega ristorante ESISTENTE dal catalogo
                Ristorante r = chiediRistorante();
                if (r != null) {
                    boolean ok = utenteService.aggiungiRistoranteGestito(ristoratore.getUsername(), r);
                    System.out.println(ok ? "Aggiunto alla gestione." : "Non aggiunto.");
                }
            }

            case 4 -> { // rimuovi dalla gestione
                Ristorante r = chiediRistorante();
                if (r != null) {
                    boolean ok = utenteService.rimuoviRistoranteGestito(ristoratore.getUsername(), r);
                    System.out.println(ok ? "Rimosso dalla gestione." : "Non rimosso.");
                }
            }

            case 5 -> ristoranteService.visualizzaRecensioniRistoratore(ristoratore);

            case 6 -> {
                Ristorante r = chiediRistorante();
                if (r == null) break;
                System.out.print("Username autore recensione: ");
                String autore = sc.nextLine().trim();
                Recensione target = r.trovaRecensioneDiUtente(autore);
                if (target == null) {
                    System.out.println("Recensione non trovata.");
                    break;
                }
                System.out.print("Risposta: ");
                String resp = sc.nextLine();
                try {
                    recensioneService.rispondiRecensione(ristoratore, r, target, resp);
                    System.out.println("Risposta inviata.");
                } catch (Exception e) {
                    System.out.println("Errore: " + e.getMessage());
                }
            }

            case 7 -> continua = false;

            default -> System.out.println("Scelta non valida.");
            }
        }
    }

    // UTIL DI I/O
    private String leggiStringa (String messaggio) {
    	System.out.print(messaggio);
        String input = sc.nextLine().trim();
        if (input.equalsIgnoreCase("annulla")) {
            throw new InputAnnullatoException();
        }
        return input.isEmpty() ? null : input;
    }
    
    private int leggiInt () {
        while (true) {
            String s = sc.nextLine().trim();
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.print("Inserisci un numero valido: ");
            }
        }
    }

    private Ruolo leggiRuolo () {
        while (true) {
            String s = sc.nextLine().trim().toUpperCase();
            try {
                return Ruolo.valueOf(s);
            } catch (IllegalArgumentException e) {
                System.out.print("Valore non valido. Scrivi CLIENTE o RISTORATORE: ");
            }
        }
    }

    private void stampaElencoRistoranti (List<Ristorante> lista) {
        if (lista.isEmpty()) {
            System.out.println("Nessun ristorante.");
            return;
        }
        int i = 1;
        for (Ristorante r : lista) {
            System.out.printf("%d) %s - %s%n", i++, r.getNome(), r.getLocation());
        }
    }

    private void cercaRistorantiConFiltri () {
    	try {
	        System.out.println("\n--- Ricerca ristoranti ---");
	        System.out.println("(Digita 'annulla' in qualsiasi momento per tornare indietro)");
	        String cucina = leggiStringa("Cucina (invio per nessun filtro): ");
	        String location = leggiStringa("Località (invio per nessun filtro): ");
	        String fascia = leggiFasciaPrezzo();
	        Boolean delivery = leggiSiNo("Delivery disponibile? (s/n/invio): ");
	        Boolean prenotazione = leggiSiNo("Prenotazione online? (s/n/invio): ");
	        Double minStelle = leggiMinStelle();
	
	        List<Ristorante> risultati = ristoranteService.cercaRistorante(
	                cucina, location, fascia, delivery, prenotazione, minStelle
	        );
	        if (risultati.isEmpty()) {
	            System.out.println("Nessun risultato.");
	        } else {
	            System.out.println("Risultati:");
	            stampaElencoRistoranti(risultati);
	        }
    	} catch (InputAnnullatoException e) {
    		System.out.println("Ricerca annullata.");
            return; // Torna al menu precedente
        }
    }

    private void visualizzaRecensioniRistorante () {
        Ristorante r = chiediRistorante();
        if (r != null) ristoranteService.visualizzaRecensioni(r);
    }
    
    private void cercaVicinoAMe() {
        try {
            String indirizzo = leggiStringa("Inserisci un indirizzo (o 'annulla'): ");
            System.out.print("Distanza massima in km: ");
            double km = Double.parseDouble(sc.nextLine().trim());
            if (km <= 0) {
                System.out.println("La distanza deve essere > 0.");
                return;
            }
            List<Ristorante> vicini = ristoranteService.cercaVicinoA(indirizzo, km);
            if (vicini.isEmpty()) {
                System.out.println("Nessun ristorante entro " + km + " km.");
            } else {
                System.out.println("Ristoranti entro " + km + " km da: " + indirizzo);
                stampaElencoRistoranti(vicini);
            }
        } catch (InputAnnullatoException ex) {
            System.out.println("Ricerca annullata.");
        } catch (NumberFormatException ex) {
            System.out.println("Distanza non valida.");
        } catch (Exception ex) {
            System.out.println("Errore durante la ricerca: " + ex.getMessage());
        }
    }

    private Ristorante chiediRistorante () {
        System.out.print("Nome ristorante: ");
        String nome = sc.nextLine().trim();
        System.out.print("Località (es. \"Milano, Italia\"): ");
        String loc = sc.nextLine().trim();
        Ristorante r = data.findRistorante(nome, loc);
        if (r == null) System.out.println("Ristorante non trovato (nome e località devono combaciare).");
        return r;
    }
    private String leggiFasciaPrezzo () {
        System.out.println("Fascia di prezzo:");
        System.out.println("1) Economico (< 20€)");
        System.out.println("2) Medio (20-50€)");
        System.out.println("3) Costoso (> 50€)");
        System.out.println("4) Qualsiasi (invio)");
        System.out.print("Scelta: ");
        
        String scelta = sc.nextLine().trim();
        return switch (scelta) {
            case "1" -> "minore di 20€";
            case "2" -> "tra 20€ e 50€";
            case "3" -> "maggiore di 50€";
            default -> null;
        };
    }
    private Boolean leggiSiNo (String messaggio) {
        System.out.print(messaggio);
        String input = sc.nextLine().trim().toLowerCase();
        if (input.isEmpty()) return null;
        if (input.equals("s") || input.equals("si") || input.equals("y") || input.equals("yes")) return true;
        if (input.equals("n") || input.equals("no")) return false;
        System.out.println("Input non valido.");
        return null;
    }
    private Double leggiMinStelle () {
        System.out.print("Voto minimo (1-5, invio per ignorare): ");
        String input = sc.nextLine().trim();
        if (input.isEmpty()) return null;
        try {
            double stelle = Double.parseDouble(input);
            if (stelle < 1 || stelle > 5) {
                System.out.println("Deve essere tra 1 e 5, uso default.");
                return null;
            }
            return stelle;
        } catch (NumberFormatException e) {
            System.out.println("Numero non valido, uso default.");
            return null;
        }
    }
    
    /** Chiede uno username non vuoto e disponibile. 'annulla' per interrompere. */
    private String leggiUsernameDisponibile() {
        while (true) {
            String u = sc.nextLine().trim();
            if ("annulla".equalsIgnoreCase(u)) throw new InputAnnullatoException();
            if (u.isEmpty()) { System.out.println("Campo obbligatorio."); continue; }
            if (utenteService.trovaUtente(u) != null) {
                System.out.println("Username non disponibile. Riprova.");
                continue;
            }
            return u;
        }
    }
    
    /** Chiede una password valida (6–12 char) con conferma. 
     *  Digita 'annulla' per interrompere. */
    private String leggiPasswordValida() {
        while (true) {           
            String p = sc.nextLine();
            if ("annulla".equalsIgnoreCase(p)) throw new InputAnnullatoException();

            if (p.length() < 6 || p.length() > 12) {
                System.out.println("La password deve contenere tra i 6 e i 12 caratteri.");
                continue;
            }

            System.out.print("Conferma password: ");
            String c = sc.nextLine();
            if (!p.equals(c)) {
                System.out.println("Le password non coincidono.");
                continue;
            }
            return p;
        }
    }
    
    /** Richiede i campi essenziali e crea un nuovo Ristorante.
     *  Digita 'annulla' in qualunque campo per interrompere.
     *  Ritorna null se annullato. */
    private Ristorante creaRistoranteDaInput() {
        System.out.println("\n--- NUOVO RISTORANTE ---");
        System.out.println("(Digita 'annulla' in qualsiasi momento per tornare indietro)");

        String nome      = leggiObbligatoria("Nome: ");
        String location  = leggiObbligatoria("Località (es. \"Milano, Italia\"): ");

        // campi opzionali
        String indirizzo = leggiStringa("Indirizzo (via e civico) [invio per saltare]: ");
        String prezzo    = leggiStringa("Prezzo medio (numero o testo, es. \"25\" o \"€€\") [invio]: ");
        String cucina    = leggiStringa("Tipo di cucina [invio]: ");
        String telefono  = leggiStringa("Telefono [invio]: ");
        String website   = leggiStringa("Sito web (URL) [invio]: ");
        Boolean delivery = leggiSiNo("Delivery? (s/n/invio): ");
        Boolean pren     = leggiSiNo("Prenotazione online? (s/n/invio): ");

        // Fallback booleani
        boolean hasDelivery = Boolean.TRUE.equals(delivery);
        boolean hasPren     = Boolean.TRUE.equals(pren);

     // === CREA L’OGGETTO usando il costruttore reale (13 argomenti) ===
        double lon = 0.0;            // se non le gestisci, metti 0.0
        double lat = 0.0;
        String premi = "";           // default
        String servizi = "";         // default

        return new Ristorante(
            nome,
            indirizzo != null ? indirizzo : "",
            location,
            prezzo != null ? prezzo : "",
            cucina != null ? cucina : "",
            lon,
            lat,
            telefono != null ? telefono : "",
            website != null ? website : "",
            premi,
            servizi,
            hasPren,       // prenotazioneOnline
            hasDelivery    // delivery
        );
    }
    
    /** Chiede una stringa non vuota; ripete finché non viene fornita. */
    private String leggiObbligatoria(String prompt) {
        while (true) {
            String s = leggiStringa(prompt);
            if (s != null && !s.isBlank()) return s;
            System.out.println("Campo obbligatorio.");
        }
    }
}