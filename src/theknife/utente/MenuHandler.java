/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/
package theknife.utente;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

import theknife.logica.DataContext;
import theknife.logica.UtenteService;
import theknife.logica.RistoranteService;
import theknife.logica.RecensioneService;

import theknife.ristorante.Ristorante;
import theknife.recensione.Recensione;

public class MenuHandler {

    // ====== HARD-CODE ======
    private static final String UTENTI_CSV     = "data/Utenti.csv";
    private static final String RISTORANTI_CSV = "data/Ristoranti.csv";
    private static final String RECENSIONI_CSV = "data/Recensioni.csv";

    private final Scanner sc = new Scanner(System.in);

    // Core app
    private final DataContext data = new DataContext();
    private final UtenteService utenteService;
    private final RistoranteService ristoranteService;
    private final RecensioneService recensioneService;

    public MenuHandler() {
        // Carica tutto in RAM
        data.loadAll(UTENTI_CSV, RISTORANTI_CSV, RECENSIONI_CSV);

        // Inizializza i service
        utenteService = new UtenteService(data);
        ristoranteService = new RistoranteService(data);
        recensioneService = new RecensioneService(data);
    }

    public void avvia() {
        boolean continua = true;
        while (continua) {
            System.out.println("\n========= MENU PRINCIPALE =========");
            System.out.println("1) Registrazione");
            System.out.println("2) Login");
            System.out.println("3) Continua come Ospite");
            System.out.println("4) Esci");
            System.out.print("Scelta: ");
            int scelta = leggiInt();

            switch (scelta) {
                case 1 -> registrazione();
                case 2 -> login();
                case 3 -> menuOspite();
                case 4 -> {
                    // Salvataggi
                    data.saveAll(UTENTI_CSV, RISTORANTI_CSV, RECENSIONI_CSV);
                    System.out.println("Ciao!");
                    continua = false;
                }
                default -> System.out.println("Scelta non valida.");
            }
        }
    }

    // ================== OSPITE ==================
    private void menuOspite() {
        boolean continua = true;
        while (continua) {
            System.out.println("\n--------- MENU OSPITE ---------");
            System.out.println("1) Elenco ristoranti (rapido)");
            System.out.println("2) Cerca ristoranti (filtri)");
            System.out.println("3) Visualizza recensioni di un ristorante");
            System.out.println("4) Torna indietro");
            System.out.print("Scelta: ");
            int s = leggiInt();

            switch (s) {
                case 1 -> stampaElencoRistoranti(data.getRistoranti());
                case 2 -> cercaRistorantiConFiltri();
                case 3 -> visualizzaRecensioniRistorante();
                case 4 -> continua = false;
                default -> System.out.println("Scelta non valida.");
            }
        }
    }

    // ================== REGISTRAZIONE / LOGIN ==================
    private void registrazione() {
        System.out.println("\n--- Registrazione ---");
        System.out.print("Nome: ");
        String nome = sc.nextLine().trim();

        System.out.print("Cognome: ");
        String cognome = sc.nextLine().trim();

        System.out.print("Username: ");
        String username = sc.nextLine().trim();

        System.out.print("Password: ");
        String password = sc.nextLine();

        System.out.print("Domicilio: ");
        String domicilio = sc.nextLine().trim();

        System.out.print("Data di nascita (YYYY-MM-DD) oppure invio per saltare: ");
        String di = sc.nextLine().trim();
        LocalDate dataNascita = null;
        if (!di.isEmpty()) {
            try {
                // Usa il tuo GestoreDate
                dataNascita = GestoreDate.parse(di);
            } catch (DateTimeParseException ex) {
                System.out.println("Formato data non valido, ignoro la data.");
            }
        }

        System.out.print("Ruolo (CLIENTE/RISTORATORE): ");
        Ruolo ruolo = leggiRuolo();

        Utente nuovo = new Utente(nome, cognome, username, password, domicilio, dataNascita, ruolo);
        boolean ok = utenteService.registrazione(nuovo);
        if (ok) System.out.println("✅ Registrazione completata.");
    }

    private void login() {
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

    // ================== CLIENTE ==================
    private void menuCliente(Utente utente) {
        boolean continua = true;
        while (continua) {
            System.out.println("\n--------- MENU CLIENTE ---------");
            System.out.println("1) Visualizza preferiti");
            System.out.println("2) Aggiungi preferito");
            System.out.println("3) Rimuovi preferito");
            System.out.println("4) Cerca ristoranti");
            System.out.println("5) Aggiungi recensione");
            System.out.println("6) Visualizza recensioni di un ristorante");
            System.out.println("7) Logout");
            System.out.print("Scelta: ");
            int s = leggiInt();

            switch (s) {
                case 1 -> utenteService.visualizzaPreferiti(utente.getUsername());

                case 2 -> {
                    Ristorante r = chiediRistoranteByNomeLoc();
                    if (r != null) {
                        boolean ok = ristoranteService.aggiungiPreferito(utente, r);
                        System.out.println(ok ? "Aggiunto ai preferiti." : "Non aggiunto.");
                    }
                }

                case 3 -> {
                    Ristorante r = chiediRistoranteByNomeLoc();
                    if (r != null) {
                        boolean ok = ristoranteService.rimuoviPreferito(utente, r);
                        System.out.println(ok ? "Rimosso dai preferiti." : "Non rimosso.");
                    }
                }

                case 4 -> cercaRistorantiConFiltri();

                case 5 -> {
                    Ristorante r = chiediRistoranteByNomeLoc();
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

                case 7 -> continua = false;

                default -> System.out.println("Scelta non valida.");
            }
        }
    }

    // ================== RISTORATORE ==================
    private void menuRistoratore(Utente ristoratore) {
        boolean continua = true;
        while (continua) {
            System.out.println("\n------ MENU RISTORATORE ------");
            System.out.println("1) Visualizza ristoranti gestiti");
            System.out.println("2) Aggiungi ristorante gestito (dal catalogo)");
            System.out.println("3) Rimuovi ristorante gestito");
            System.out.println("4) Visualizza recensioni dei miei ristoranti");
            System.out.println("5) Rispondi a una recensione");
            System.out.println("6) Logout");
            System.out.print("Scelta: ");
            int s = leggiInt();

            switch (s) {
                case 1 -> utenteService.visualizzaRistorantiGestiti(ristoratore.getUsername());

                case 2 -> {
                    Ristorante r = chiediRistoranteByNomeLoc();
                    if (r != null) {
                        boolean ok = utenteService.aggiungiRistoranteGestito(ristoratore.getUsername(), r);
                        System.out.println(ok ? "Aggiunto alla gestione." : "Non aggiunto.");
                    }
                }

                case 3 -> {
                    Ristorante r = chiediRistoranteByNomeLoc();
                    if (r != null) {
                        boolean ok = utenteService.rimuoviRistoranteGestito(ristoratore.getUsername(), r);
                        System.out.println(ok ? "Rimosso dalla gestione." : "Non rimosso.");
                    }
                }

                case 4 -> ristoranteService.visualizzaRecensioniRistoratore(ristoratore);

                case 5 -> {
                    Ristorante r = chiediRistoranteByNomeLoc();
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

                case 6 -> continua = false;

                default -> System.out.println("Scelta non valida.");
            }
        }
    }

    // ================== UTIL DI I/O ==================
    private int leggiInt() {
        while (true) {
            String s = sc.nextLine().trim();
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.print("Inserisci un numero valido: ");
            }
        }
    }

    private Ruolo leggiRuolo() {
        while (true) {
            String s = sc.nextLine().trim().toUpperCase();
            try {
                return Ruolo.valueOf(s);
            } catch (IllegalArgumentException ex) {
                System.out.print("Valore non valido. Scrivi CLIENTE o RISTORATORE: ");
            }
        }
    }

    private void stampaElencoRistoranti(List<Ristorante> lista) {
        if (lista.isEmpty()) {
            System.out.println("Nessun ristorante.");
            return;
        }
        int i = 1;
        for (Ristorante r : lista) {
            System.out.printf("%d) %s - %s%n", i++, r.getNome(), r.getLocation());
        }
    }

    private void cercaRistorantiConFiltri() {
        System.out.println("\n--- Ricerca ristoranti ---");
        System.out.print("Cucina (invio per nessun filtro): ");
        String cucina = vuotoNull(sc.nextLine());
        System.out.print("Location (invio per nessun filtro): ");
        String location = vuotoNull(sc.nextLine());
        System.out.print("Fascia prezzo (\"minore di 30€\" / \"tra 20€ e 50€\" / \"maggiore di 50€\") oppure invio: ");
        String fascia = vuotoNull(sc.nextLine());
        System.out.print("Delivery? (true/false oppure invio): ");
        String del = sc.nextLine().trim();
        Boolean delivery = del.isEmpty() ? null : Boolean.parseBoolean(del);
        System.out.print("Prenotazione online? (true/false oppure invio): ");
        String pr = sc.nextLine().trim();
        Boolean pren = pr.isEmpty() ? null : Boolean.parseBoolean(pr);
        System.out.print("Min stelle (1-5 oppure invio): ");
        String ms = sc.nextLine().trim();
        Double minStelle = ms.isEmpty() ? null : Double.parseDouble(ms);

        List<Ristorante> risultati = ristoranteService.cercaRistorante(
                cucina, location, fascia, delivery, pren, minStelle
        );
        if (risultati.isEmpty()) {
            System.out.println("Nessun risultato.");
        } else {
            System.out.println("Risultati:");
            stampaElencoRistoranti(risultati);
        }
    }

    private void visualizzaRecensioniRistorante() {
        Ristorante r = chiediRistoranteByNomeLoc();
        if (r != null) ristoranteService.visualizzaRecensioni(r);
    }

    private Ristorante chiediRistoranteByNomeLoc() {
        System.out.print("Nome ristorante: ");
        String nome = sc.nextLine().trim();
        System.out.print("Location (es. \"Faloppio, Italia\"): ");
        String loc = sc.nextLine().trim();
        Ristorante r = data.findRistorante(nome, loc);
        if (r == null) System.out.println("Ristorante non trovato (nome+location devono combaciare).");
        return r;
    }

    private String vuotoNull(String s) {
        s = s.trim();
        return s.isEmpty() ? null : s;
    }
}