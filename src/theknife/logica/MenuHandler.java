/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/

package theknife.logica;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.Locale;

import theknife.ristorante.Ristorante;
import theknife.utente.*;
import theknife.eccezioni.InputAnnullatoException;
import theknife.recensione.Recensione;

/**
 * Gestisce l'interazione a riga di comando dell'applicazione TheKnife.
 * Mostra i menu per OSPITE, CLIENTE e RISTORATORE e orchestra i flussi
 * chiamando i rispettivi service. Carica e salva i dati tramite DataContext.
 * 
 * Per gli switch case viene utilizzata la sintassi introdotta in Java 14,
 * non è necessario scrivere break, ogni caso è isolato di default.
 * 
 * @author Gasparini Lorenzo
 * @author Ciani Flavio Angelo
 * @author Scolaro Gabriele
 */
public class MenuHandler {

	// CAMPI
    private static final String UTENTI_CSV     = "data/Utenti.csv";
    private static final String RISTORANTI_CSV = "data/Ristoranti.csv";
    private static final String RECENSIONI_CSV = "data/Recensioni.csv";

    private static final String BACK_KEY = "*";

    private final Scanner sc = new Scanner(System.in);
    private final DataContext data = new DataContext();
    private final UtenteService utenteService;
    private final RistoranteService ristoranteService;
    private final RecensioneService recensioneService;
    private final GeoService geoService = new GeoService();
    
    /**
     * COSTRUTTORE della classe MenuHandler: carica i dati da CSV in RAM e inizializza i service.
     */
    public MenuHandler () {
        data.loadAll(UTENTI_CSV, RISTORANTI_CSV, RECENSIONI_CSV);
        utenteService = new UtenteService(data);
        ristoranteService = new RistoranteService(data, geoService);
        recensioneService = new RecensioneService(data);
    }

    /**
     * Pulisce il terminale. Non garantito su tutte le console.
     */
    private static void pulisciTerminale() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
    	
    //================= MENU PRINCIPALE =========================
    
    /**
     * Avvia il menu principale del programma.
     * Permette la registrazione, il login, l’uso come ospite o l’uscita.
     * Gestisce eccezioni previste e salva i dati prima di chiudere.
     */
    public void avvia() {
        try {
            boolean continua = true;
            while (continua) {
                pulisciTerminale();
                
                System.out.println("########################################");
                System.out.println("###           THE KNIFE              ###");
                System.out.println("########################################");
                System.out.println();
                
                System.out.println("\n========= MENU PRINCIPALE =========");
                System.out.println("1) Registrazione");
                System.out.println("2) Login");
                System.out.println("3) Continua come ospite");
                System.out.println("4) Esci");
                System.out.print("Scelta (o * per indietro): ");
                
                try {
                    int scelta = leggiInt();
                    switch (scelta) {
                        case 1 -> registrazione();
                        case 2 -> login();
                        case 3 -> menuOspite();
                        case 4 -> {
                            continua = false;
                            System.out.println("Chiusura programma in corso.");
                        }
                        default -> {
                            System.out.println("Scelta non valida.");
                            pausa();
                        }
                    }
                } catch (InputAnnullatoException e) {
                    return;
                }
            }
        } catch (InputAnnullatoException e) {
            System.out.println("Operazione annullata, ritorno al menu principale.");
        } catch (Exception e) {
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
    
    // ===================== MENU OSPITE =====================

    /**
     * Menu per OSPITE: ricerche con filtri o per geolocalizzazione e visualizzione
     * ristoranti con annesse informazioni e recensionj.
     */
    private void menuOspite() {
        boolean continua = true;
        while (continua) {
            pulisciTerminale();
            System.out.println("\n--------- OSPITE ---------");
            System.out.println("1) Cerca ristoranti (filtri)");
            System.out.println("2) Cerca ristoranti per indirizzo (geo + raggio km)");
            System.out.println("3) Torna indietro");
            System.out.print("Scelta (o * per indietro): ");
            try {
                int scelta = leggiInt();
                switch (scelta) {
                    case 1 -> flussoRicercaGenerale(null);
                    case 2 -> flussoRicercaGeografica(null);
                    case 3 -> continua = false;
                    default -> {
                        System.out.println("Scelta non valida.");
                        pausa();
                    }
                }
            } catch (InputAnnullatoException e) {
                return;
            }
        }
    }

    // ===================== LOGIN / REGISTRAZIONE =====================

    /**
     * Flusso di registrazione di un nuovo utente con convalida basilare degli input.
     * In caso di annullamento con * ritorna al menu precedente.
     */
    private void registrazione () {
        pulisciTerminale();
        try {
            System.out.println("\n--- REGISTRAZIONE --- (o * per indietro in qualunque campo)");
            System.out.print("Nome: ");
            String nome = leggiLineaRaw();

            System.out.print("Cognome: ");
            String cognome = leggiLineaRaw();

            System.out.print("Username: ");
            String username = leggiUsernameDisponibile();

            System.out.print("Password (minimo 6 massimo 12 caratteri): ");
            String password = leggiPasswordValida();

            System.out.print("Domicilio: ");
            String domicilio = leggiLineaRaw();

            System.out.print("Data di nascita DD/MM/YYYY (o invio/* per saltare): ");
            String dataInput = leggiLineaOpt();
            LocalDate dataNascita = null;
            if (dataInput != null && !dataInput.isEmpty()) {
                try {
                    dataNascita = GestoreDate.parse(dataInput);
                } catch (IllegalArgumentException e) {
                    System.out.println("Formato della data non valido, ignoro la data.");
                }
            }

            System.out.print("Ruolo (CLIENTE/RISTORATORE, o * per indietro): ");
            Ruolo ruolo = leggiRuolo();

            Utente nuovo = new Utente(nome, cognome, username, password, domicilio, dataNascita, ruolo);
            boolean ok = utenteService.registrazione(nuovo);
            System.out.println(ok ? "Registrazione completata." : "Registrazione NON riuscita.");
            pausa();
        } catch (InputAnnullatoException e) {
            System.out.println("Registrazione annullata.");
            pausa();
        }
    }

    /**
     * Flusso di login. Reindirizza ai menu CLIENTE o RISTORATORE in base al ruolo.
     */
    private void login () {
        pulisciTerminale();
        try {
            System.out.println("\n--- LOGIN ---");
            System.out.print("Username (o * per indietro): ");
            String u = leggiLineaRaw();
            System.out.print("Password (o * per indietro): ");
            String p = leggiLineaRaw();

            Utente loggato = utenteService.login(u, p);
            if (loggato == null) { pausa(); return; }

            if (loggato.getRuolo() == Ruolo.CLIENTE) {
                menuCliente(loggato);
            } else if (loggato.getRuolo() == Ruolo.RISTORATORE) {
                menuRistoratore(loggato);
            } else {
                System.out.println("Ruolo non gestito.");
                pausa();
            }
        } catch (InputAnnullatoException e) {
            System.out.println("Login annullato.");
            pausa();
        }
    }

    // ===================== CLIENTE =====================

    /**
     * Menu per CLIENTE: ricerche con filtri o per geolocalizzazione, visualizzazione 
     * ristoranti, gestione lista preferiti, gestione recensioni e logout.
     * 
     * @param utente Utente autenticato con ruolo CLIENTE
     */
    private void menuCliente (Utente utente) {
        boolean continua = true;
        while (continua) {
            pulisciTerminale();
            System.out.println("\n--------- CLIENTE ---------");
            System.out.println("1) Cerca ristoranti (filtri)");
            System.out.println("2) Cerca ristoranti per indirizzo (geo + raggio km)");
            System.out.println("3) I miei preferiti");
            System.out.println("4) Le mie recensioni (visualizza/modifica)");
            System.out.println("5) Logout");
            System.out.print("Scelta (o * per indietro): ");
            try {
                int scelta = leggiInt();
                switch (scelta) {
                    case 1 -> flussoRicercaGenerale(utente);
                    case 2 -> flussoRicercaGeografica(utente);
                    case 3 -> flussoPreferiti(utente);
                    case 4 -> flussoMieRecensioni(utente);
                    case 5 -> continua = false;
                    default -> {
                        System.out.println("Scelta non valida.");
                        pausa();
                    }
                }
            } catch (InputAnnullatoException e) {
                return;
            }
        }
    }

    // ===================== RISTORATORE =====================

    /**
     * Menu per RISTORATORE: creazione/gestione ristoranti, gestione risposte a recensioni.
     * 
     * @param ristoratore utente autenticato con ruolo RISTORATORE
     */
    private void menuRistoratore (Utente ristoratore) {
        boolean continua = true;
        while (continua) {
            pulisciTerminale();
            System.out.println("\n------ RISTORATORE ------");
            System.out.println("1) Inserisci NUOVO ristorante");
            System.out.println("2) I miei ristoranti (modifica/elimina)");
            System.out.println("3) Prendi in gestione ristorante esistente");
            System.out.println("4) Recensioni dei miei ristoranti (rispondi)");
            System.out.println("5) Logout");
            System.out.print("Scelta (o * per indietro): ");
            try {
                int scelta = leggiInt();
                switch (scelta) {
                    case 1 -> {
                        Ristorante nuovo = creaRistoranteConConfermaGeo();
                        if (nuovo == null) 
                        	break; 
                        boolean ok = ristoranteService.aggiungiRistorante(ristoratore, nuovo);
                        System.out.println(ok ? "Ristorante creato e aggiunto alla tua gestione."
                                              : "Impossibile creare o aggiungere il ristorante (ID già esistente o altro errore).");
                        pausa();
                    }
                    case 2 -> flussoGestioneRistoranti(ristoratore);
                    case 3 -> flussoPrendiInGestione(ristoratore);
                    case 4 -> flussoRecensioniGestite(ristoratore);
                    case 5 -> continua = false;
                    default -> {
                        System.out.println("Scelta non valida.");
                        pausa();
                    }
                }
            } catch (InputAnnullatoException e) {
                return;
            }
        }
    }

    // ==========================================================
    //                     FLUSSI COMUNI
    // ==========================================================

    /**
     * Ricerca con filtri, selezione da lista e apertura pagina ristorante.
     * 
     * @param utenteCorrente null se ospite, altrimenti utente loggato
     */
    private void flussoRicercaGenerale(Utente utenteCorrente) {
        try {
            pulisciTerminale();
            System.out.println("\n--- RICERCA RISTORANTI ---");
            System.out.println("(Digita 'annulla' o * in qualsiasi momento per tornare indietro)");

            String nome = leggiStringa("Nome del ristorante (invio per nessun filtro, * per indietro): ");
            String cucina = leggiStringa("Cucina (invio per nessun filtro, * per indietro): ");
            String location = leggiStringa("Località (invio per nessun filtro, * per indietro): ");
            String fascia = leggiFasciaPrezzo();
            Boolean delivery = leggiSiNo("Delivery disponibile? (s/n/invio, * per indietro): ");
            Boolean prenotazione = leggiSiNo("Prenotazione online? (s/n/invio, * per indietro): ");
            Double minStelle = leggiMinStelle();

            List<Ristorante> risultati = ristoranteService.cercaRistorantePerFiltri(
                    nome, cucina, location, fascia, delivery, prenotazione, minStelle
            );

            if (risultati.isEmpty()) {
                System.out.println("Nessun risultato.");
                pausa();
                return;
            }

            Ristorante scelto = selezionaRistoranteDaLista(risultati);
            if (scelto != null) paginaRistorante(scelto, utenteCorrente);
        } catch (InputAnnullatoException e) {
            System.out.println("Ricerca annullata.");
            pausa();
        }
    }

    /**
     * Ricerca geografica per indirizzo + raggio, selezione e apertura pagina ristorante.
     * 
     * @param utenteCorrente null se ospite, altrimenti utente loggato
     */
    private void flussoRicercaGeografica(Utente utenteCorrente) {
        try {
            pulisciTerminale();
            System.out.println("\n--- RICERCA PER INDIRIZZO ---");
            String indirizzo = leggiStringa("Inserisci un indirizzo (es. 'Milano' o 'Via Roma 10, Torino', * per indietro): ");
            double km = leggiDoublePositivo("Distanza massima in km: ");
            List<Ristorante> vicini = ristoranteService.cercaVicinoA(indirizzo, km);
            if (vicini.isEmpty()) {
                System.out.println("Nessun ristorante entro " + km + " km.");
                pausa();
                return;
            }
            Ristorante scelto = selezionaRistoranteDaLista(vicini);
            if (scelto != null) 
            	paginaRistorante(scelto, utenteCorrente);
        } catch (InputAnnullatoException ex) {
            System.out.println("Ricerca annullata.");
            pausa();
        } catch (Exception ex) {
            System.out.println("Errore durante la ricerca: " + ex.getMessage());
            pausa();
        }
    }

    /**
     * Vista dettagliata del ristorante con campi ristorante,
     * recensioni e azioni aggiuntive per CLIENTE.
     * 
     * @param r ristorante selezionato
     * @param utenteCorrente Utente corrente o null se ospite
     */
    private void paginaRistorante(Ristorante r, Utente utenteCorrente) {
        boolean isCliente = (utenteCorrente != null && utenteCorrente.getRuolo() == Ruolo.CLIENTE);
        while (true) {
            pulisciTerminale();
            System.out.println("\n===== " + r.getNome() + " =====");
            System.out.println("Luogo: " + r.getLocation());
            System.out.println("Prezzo medio: " + safe(r.getPrezzoMedio()));
            System.out.println("Media stelle: " + formatMediaStelle(r));
            System.out.println("Premi: " + safe(r.getPremi()));
            System.out.println("Maps: " + mapsLink(r));
            System.out.println("---------------------------------");
            System.out.println("Recensioni positive recenti:");
            for (Recensione rec : r.getRecensioni()) {
                if (rec.isPositiva() && rec.isRecente()) {
                    System.out.println("- " + rec.getAutore() + ": " + rec.getStelle() + " stelle - " + rec.getDescrizione());
                }
            }
            
            System.out.println("Altre recensioni:");
            for (Recensione rec : r.getRecensioni()) {
                if (!(rec.isPositiva() && rec.isRecente())) {
                    System.out.println("- " + rec.getAutore() + ": " + rec.getStelle() + "★ - " + rec.getDescrizione());
                }
            }
            System.out.println("---------------------------------");

            if (!isCliente) {
                System.out.println("1) Torna indietro");
                System.out.print("Scelta (o * per indietro): ");
                try {
                    leggiInt(); 
                } catch (InputAnnullatoException e) {
                    return;
                }
                return;
            }

            // OPZIONI CLIENTE
            System.out.println("1) Aggiungi ai preferiti");
            System.out.println("2) Rimuovi dai preferiti");
            System.out.println("3) Aggiungi/Modifica la mia recensione");
            System.out.println("4) Torna indietro");
            System.out.print("Scelta (o * per indietro): ");
            try {
                int scelta = leggiInt();
                switch (scelta) {
                    case 1 -> {
                        boolean ok = ristoranteService.aggiungiPreferito(utenteCorrente, r);
                        System.out.println(ok ? "Aggiunto ai preferiti." : "Già presente o non aggiunto.");
                        pausa();
                    }
                    case 2 -> {
                        boolean ok = ristoranteService.rimuoviPreferito(utenteCorrente, r);
                        System.out.println(ok ? "Rimosso dai preferiti." : "Non presente o non rimosso.");
                        pausa();
                    }
                    case 3 -> {
                        aggiungiOModificaMiaRecensione(utenteCorrente, r);
                        pausa();
                    }
                    case 4 -> { return; }
                    default -> {
                        System.out.println("Scelta non valida.");
                        pausa();
                    }
                }
            } catch (InputAnnullatoException e) {
                return;
            }
        }
    }

    // ===================== CLIENTE: Preferiti e Recensioni =====================

    /**
     * Mostra la lista dei preferiti dell'utente e apre la pagina del ristorante selezionato.
     * 
     * @param utente CLIENTE autenticato
     */
    private void flussoPreferiti(Utente utente) {
        pulisciTerminale();
        try {
            List<Ristorante> preferiti = new ArrayList<>();
            if (utente.getRistorantiPreferiti() != null) {
                preferiti.addAll(utente.getRistorantiPreferiti());
            } else {
                utenteService.visualizzaPreferiti(utente.getUsername());
            }

            if (preferiti.isEmpty()) {
                System.out.println("Nessun preferito trovato.");
                pausa();
                return;
            }
            Ristorante scelto = selezionaRistoranteDaLista(preferiti);
            if (scelto != null) paginaRistorante(scelto, utente);
        } catch (InputAnnullatoException e) {
            return;
        }
    }

    /**
     * Visualizza e permette la modifica delle recensioni pubblicate dall'utente.
     * 
     * @param utente CLIENTE autenticato
     */
    private void flussoMieRecensioni(Utente utente) {
        pulisciTerminale();
        try {
            List<Recensione> mie = new ArrayList<>();
            List<Recensione> tutte = data.getRecensioni();
            if (tutte != null) {
                for (Recensione rec : tutte) {
                    if (rec != null && rec.getAutore() != null &&
                        rec.getAutore().equalsIgnoreCase(utente.getUsername())) {
                        mie.add(rec);
                    }
                }
            }
            mie.sort(Comparator.comparing(Recensione::getData).reversed());

            if (mie.isEmpty()) {
                System.out.println("Non hai ancora pubblicato recensioni.");
                pausa();
                return;
            }

            System.out.println("\n--- Le mie recensioni --- (seleziona numero, * per indietro)");
            for (int i = 0; i < mie.size(); i++) {
                Recensione rec = mie.get(i);
                Ristorante r = data.findRistoranteById(rec.getIdRistorante());
                String nomeR = (r != null) ? r.getNome() : "???";
                String locR  = (r != null) ? r.getLocation() : "???";

                System.out.printf("%d) %s - %s | %d★ | \"%s\"%n",
                    i + 1,
                    nomeR,
                    locR,
                    rec.getStelle(),
                    rec.getDescrizione().length() > 60 ? rec.getDescrizione().substring(0, 57) + "..." : rec.getDescrizione());
            }
            System.out.println((mie.size() + 1) + ") Torna indietro");
            System.out.print("Scelta (o * per indietro): ");
            int idx = leggiIntInRange(1, mie.size() + 1);
            if (idx == mie.size() + 1) 
            	return;

            Recensione daModificare = mie.get(idx - 1);
            Ristorante r = data.findRistoranteById(daModificare.getIdRistorante());
            if (r == null) {
                System.out.println("Ristorante non trovato (recensione orfana).");
                pausa();
                return;
            }
            modificaRecensioneFlow(utente, r, daModificare);
            pausa();
        } catch (InputAnnullatoException e) {
            return;
        }
    }

    /**
     * Flusso di modifica guidata di una recensione esistente.
     * 
     * @param utente Utente autore
     * @param r Ristorante di riferimento
     * @param target Recensione da modificare
     */
    private void modificaRecensioneFlow(Utente utente, Ristorante r, Recensione target) {
        pulisciTerminale();
        try {
            System.out.println("\n--- Modifica recensione su " + r.getNome() + " (" + r.getLocation() + ") ---");
            System.out.println("Attuale: " + target.getStelle() + "★ - " + target.getDescrizione());
            int nuoveStelle = leggiIntInRangePrompt("Nuove stelle (1-5, * per indietro): ", 1, 5);
            System.out.print("Nuovo testo (o * per indietro): ");
            String nuovoTesto = leggiLineaRaw();

            try {
                recensioneService.modificaRecensione(utente, target, nuoveStelle, nuovoTesto);
                System.out.println("Recensione modificata.");
            } catch (Exception e) {
                System.out.println("Errore: " + e.getMessage());
            }
        } catch (InputAnnullatoException e) {
            System.out.println("Modifica annullata.");
        }
    }

    /**
     * Se non esiste una recensione dell'utente sul ristorante, la crea; altrimenti apre la modifica.
     * 
     * @param utente Utente autore
     * @param r Ristorante
     */
    private void aggiungiOModificaMiaRecensione(Utente utente, Ristorante r) {
        try {
            Recensione esistente = r.trovaRecensioneDiUtente(utente.getUsername());
            if (esistente == null) {
                System.out.print("Stelle (1-5, * per indietro): ");
                int stelle = leggiIntInRange(1, 5);
                System.out.print("Commento (o * per indietro): ");
                String testo = leggiLineaRaw();
                try {
                    Recensione rec = recensioneService.aggiungiRecensione(utente, r, stelle, testo);
                    System.out.println(rec != null ? "Recensione aggiunta." : "Impossibile aggiungere.");
                } catch (Exception e) {
                    System.out.println("Errore: " + e.getMessage());
                }
            } else {
                modificaRecensioneFlow(utente, r, esistente);
            }
        } catch (InputAnnullatoException e) {
            System.out.println("Operazione annullata.");
        }
    }

    // ===================== RISTORATORE: gestione/creazione ristoranti =====================

    /**
     * Gestione dei ristoranti di un ristoratore (modifica campi, rimozione dalla gestione).
     * 
     * @param ristoratore Utente RISTORATORE
     */
    private void flussoGestioneRistoranti(Utente ristoratore) {
        pulisciTerminale();
        try {
            List<Ristorante> miei = new ArrayList<>();
            try {
                if (ristoratore.getRistorantiGestiti() != null) {
                    miei.addAll(ristoratore.getRistorantiGestiti());
                }
            } catch (Exception ignored) {
            	return;
            }

            if (miei.isEmpty()) {
                System.out.println("Non gestisci alcun ristorante.");
                pausa();
                return;
            }

            Ristorante scelto = selezionaRistoranteDaLista(miei);
            if (scelto == null) 
            	return;

            boolean stay = true;
            while (stay) {
                pulisciTerminale();
                System.out.println("\n--- Gestione: " + scelto.getNome() + " - " + scelto.getLocation() + " ---");
                System.out.println("1) Modifica campi principali");
                System.out.println("2) Elimina ristorante dalla mia gestione");
                System.out.println("3) Torna indietro");
                System.out.print("Scelta (o * per indietro): ");
                try {
                    int s = leggiInt();
                    switch (s) {
                        case 1 -> {
                            modificaCampiRistorante(scelto);
                            pausa();
                        }
                        case 2 -> {
                            boolean ok = utenteService.rimuoviRistoranteGestito(ristoratore.getUsername(), scelto);
                            System.out.println(ok ? "Rimosso dalla gestione." : "Non rimosso.");
                            pausa();
                            stay = false;
                        }
                        case 3 -> stay = false;
                        default -> {
                            System.out.println("Scelta non valida.");
                            pausa();
                        }
                    }
                } catch (InputAnnullatoException e) {
                    stay = false;
                }
            }
        } catch (InputAnnullatoException e) {
            return;		
        }
    }

    /**
     * Modifica alcuni campi del ristorante selezionato.
     * 
     * @param r Ristorante da modificare
     */
    private void modificaCampiRistorante(Ristorante r) {
        pulisciTerminale();
        try {
            System.out.println("\n--- Modifica campi --- (invio per saltare, * per indietro)");

            System.out.print("Prezzo medio attuale: " + safe(r.getPrezzoMedio()) + " -> nuovo: ");
            String prezzo = leggiLineaOpt();
            if (prezzo != null && !prezzo.isEmpty()) 
                r.setPrezzoMedio(prezzo);

            System.out.print("Telefono attuale: " + safe(r.getNumeroTelefono()) + " -> nuovo: ");
            String tel = leggiLineaOpt();
            if (tel != null && !tel.isEmpty()) 
                r.setNumeroTelefono(tel);

            System.out.print("Website attuale: " + safe(r.getWebsiteUrl()) + " -> nuovo: ");
            String web = leggiLineaOpt();
            if (web != null && !web.isEmpty()) 
                r.setWebsiteUrl(web);

            System.out.print("Premi attuali: " + safe(r.getPremi()) + " -> nuovi: ");
            String premi = leggiLineaOpt();
            if (premi != null && !premi.isEmpty()) 
                r.setPremi(premi);

            System.out.print("Servizi attuali: " + safe(r.getServizi()) + " -> nuovi: ");
            String servizi = leggiLineaOpt();
            if (servizi != null && !servizi.isEmpty()) 
                r.setServizi(servizi);

            System.out.print("Prenotazione online attuale: " + (r.isPrenotazioneOnline() ? "sì" : "no") + " -> nuova (s/n/invio, * per indietro): ");
            Boolean pren = leggiSiNo("");
            if (pren != null) 
                r.setPrenotazioneOnline(pren);

            System.out.print("Delivery attuale: " + (r.isDelivery() ? "sì" : "no") + " -> nuovo (s/n/invio, * per indietro): ");
            Boolean delivery = leggiSiNo("");
            if (delivery != null) 
                r.setDelivery(delivery);

            System.out.println("Aggiornato.");
        } catch (InputAnnullatoException e) {
            System.out.println("Modifica annullata.");
        }
    }

    /**
     * Visualizza le recensioni dei ristoranti gestiti e permette di rispondere o eliminare una risposta.
     * 
     * @param ristoratore Utente con ruolo RISTORATORE
     */
    private void flussoRecensioniGestite(Utente ristoratore) {
        pulisciTerminale();
        try {
            List<Ristorante> miei = new ArrayList<>();
            try {
                if (ristoratore.getRistorantiGestiti() != null) 
                	miei.addAll(ristoratore.getRistorantiGestiti());
            } catch (Exception ignored) {}

            if (miei.isEmpty()) {
                System.out.println("Non gestisci alcun ristorante.");
                pausa();
                return;
            }

            Ristorante scelto = selezionaRistoranteDaLista(miei);
            if (scelto == null) 
            	return;

            List<Recensione> recensioni = new ArrayList<>(scelto.getRecensioni());
            if (recensioni.isEmpty()) {
                System.out.println("Nessuna recensione per questo ristorante.");
                pausa();
                return;
            }

            System.out.println("\n--- Recensioni di " + scelto.getNome() + " ---");
            for (int i = 0; i < recensioni.size(); i++) {
                Recensione rec = recensioni.get(i);
                boolean evidenzia = rec.isPositiva() && rec.isRecente();
                System.out.printf("%d) %s - %d stelle - \"%s\" %s\n",
                        i + 1,
                        rec.getAutore(),
                        rec.getStelle(),
                        rec.getDescrizione().length() > 80 ? rec.getDescrizione().substring(0, 77) + "..." : rec.getDescrizione(),
                        evidenzia ? "[RECENTE E POSITIVA]" : ""
                );
            }
            System.out.println((recensioni.size() + 1) + ") Torna indietro");
            System.out.print("Scelta (o * per indietro): ");
            int idx = leggiIntInRange(1, recensioni.size() + 1);
            if (idx == recensioni.size() + 1) 
            	return;

            Recensione target = recensioni.get(idx - 1);
            System.out.println("\n--- Recensione selezionata ---");
            System.out.println("Autore: " + target.getAutore());
            System.out.println("Voto: " + target.getStelle());
            System.out.println("Commento: " + target.getDescrizione());
            System.out.println("Risposta attuale: " + (target.getRisposta().isBlank() ? "(nessuna)" : target.getRisposta()));
            System.out.println();
            System.out.println("1) Rispondi alla recensione");
            System.out.println("2) Elimina risposta");
            System.out.println("3) Annulla");
            System.out.print("Scelta: ");
            int scelta = leggiIntInRange(1, 3);

            switch (scelta) {
                case 1 -> {
                    System.out.print("Risposta: ");
                    String resp = leggiLineaRaw();
                    try {
                        recensioneService.rispondiRecensione(ristoratore, scelto, target, resp);
                        System.out.println("Risposta inviata.");
                    } catch (Exception e) {
                        System.out.println("Errore: " + e.getMessage());
                    }
                }
                case 2 -> {
                    target.eliminaRisposta();
                    System.out.println("Risposta eliminata.");
                    data.saveAll(UTENTI_CSV, RISTORANTI_CSV, RECENSIONI_CSV);
                }
                case 3 -> {
                    System.out.println("Operazione annullata.");
                }
            }
            pausa();
        } catch (InputAnnullatoException e) {
            return;
        }
    }

    /**
     * Permette al ristoratore di prendere in gestione un ristorante non ancora gestito
     * ma presente nel file Ristoranti.csv.
     * 
     * @param ristoratore Utente con ruolo RISTORATORE
     */
    private void flussoPrendiInGestione(Utente ristoratore) {
        pulisciTerminale();
        try {
            List<Ristorante> nonGestiti = new ArrayList<>();
            List<Ristorante> tutti = data.getRistoranti();
            if (tutti != null) {
                for (Ristorante r : tutti) {
                    if (r != null && !ristoratore.gestisce(r)) {
                        nonGestiti.add(r);
                    }
                }
            }

            if (nonGestiti.isEmpty()) {
                System.out.println("Tutti i ristoranti sono già sotto la tua gestione.");
                pausa();
                return;
            }

            String keyword = leggiStringa("Inserisci nome ristorante (o * per indietro): ");

            List<Ristorante> filtrati = new ArrayList<>();
            String kw = (keyword == null ? "" : keyword.toLowerCase());
            for (Ristorante r : nonGestiti) {
                String nome = (r != null ? r.getNome() : null);
                if (nome != null && nome.toLowerCase().contains(kw)) {
                    filtrati.add(r);
                }
            }

            if (filtrati.isEmpty()) {
                System.out.println("Nessun ristorante trovato con questo nome.");
                pausa();
                return;
            }

            Ristorante scelto = selezionaRistoranteDaLista(filtrati);
            if (scelto == null) 
            	return;

            System.out.println("Hai selezionato: " + scelto.getNome() + " - " + scelto.getLocation());
            Boolean conferma = leggiSiNo("Vuoi prendere in gestione questo ristorante? (s/n, * per indietro): ");
            if (Boolean.TRUE.equals(conferma)) {
                boolean ok = utenteService.aggiungiRistoranteGestito(ristoratore.getUsername(), scelto);
                System.out.println(ok ? "Ora gestisci questo ristorante." : "Operazione non riuscita.");
                if (ok) data.saveAll(UTENTI_CSV, RISTORANTI_CSV, RECENSIONI_CSV);
            }
            pausa();
        } catch (InputAnnullatoException e) {
            return;
        }
    }

    // ===================== CREAZIONE RISTORANTE con conferma GeoLocalizzazione =====================

    /**
     * Flusso di creazione di un nuovo ristorante con tentativo di geocoding e conferma da parte dell'utente.
     * 
     * @return ristorante creato o null se annullato
     */
    private Ristorante creaRistoranteConConfermaGeo() {
        pulisciTerminale();
        try {
            System.out.println("\n--- NUOVO RISTORANTE ---");
            System.out.println("(Digita 'annulla' o * in qualsiasi momento per tornare indietro)");

            String nome = leggiObbligatoria("Nome (o * per indietro): ");
            String location = leggiObbligatoria("Località (es. \"Vienna, Austria\", o * per indietro): ");
            String indirizzo = leggiStringa("Indirizzo (via e civico) [invio per saltare, * per indietro]: ");
            String prezzo = leggiStringa("Prezzo medio (es. \"25\" o \"€€\") [invio, * per indietro]: ");
            String cucina = leggiStringa("Tipo di cucina [invio, * per indietro]: ");
            String telefono = leggiStringa("Telefono [invio, * per indietro]: ");
            String website = leggiStringa("Sito web (URL) [invio, * per indietro]: ");
            Boolean pren = leggiSiNo("Prenotazione online? (s/n/invio, * per indietro): ");
            Boolean delivery = leggiSiNo("Delivery? (s/n/invio, * per indietro): ");

            double lat = 0.0, lon = 0.0;
           
            while (true) {
                String query = buildBestGeoQuery(nome, location, indirizzo);
                double[] coords = null;
                try {
                    coords = geoService.geocode(query); 
                } catch (Exception e) {
                    System.out.println("[Geo] Errore geocoding: " + e.getMessage());
                }

                if (coords != null) {
                    lat = coords[0];
                    lon = coords[1];
                    System.out.println("✅ Indirizzo interpretato: " + query);
                    System.out.printf("   ➜ Latitudine: %.6f\n", lat);
                    System.out.printf("   ➜ Longitudine: %.6f\n", lon);

                    String latFormatted = String.format(Locale.ROOT, "%.6f", lat);
                    String lonFormatted = String.format(Locale.ROOT, "%.6f", lon);
                    System.out.printf("   🌍 Google Maps: https://maps.google.com/?q=%s,%s\n", latFormatted, lonFormatted);
                } else {
                    System.out.println("❌ Impossibile ottenere coordinate per: " + query);
                }
                
                Boolean ok = leggiSiNo("Confermi questo indirizzo? (s/n, * per indietro): ");
                if (Boolean.TRUE.equals(ok)) {
                    if (coords != null) { lat = coords[0]; lon = coords[1]; }
                    break;
                } else if (Boolean.FALSE.equals(ok)) {
                    indirizzo = leggiStringa("Reinserisci indirizzo (invio per lasciare vuoto, * per indietro): ");
                    pulisciTerminale();
                } else {
                    break;
                }
            }

            Ristorante r = new Ristorante(
                    nome,
                    indirizzo != null ? indirizzo : "",
                    location,
                    prezzo != null ? prezzo : "",
                    cucina != null ? cucina : "",
                    lon,
                    lat,
                    telefono != null ? telefono : "",
                    website != null ? website : "",
                    "", // premi
                    "", // servizi
                    Boolean.TRUE.equals(pren),
                    Boolean.TRUE.equals(delivery)
            );
            r.setLatitudine(lat);
            r.setLongitudine(lon);
            return r;
        } catch (InputAnnullatoException e) {
            System.out.println("Creazione annullata.");
            return null;
        }
    }

    /**
     * Costruisce la stringa da usare per cercare le coordinate geografiche.
     * 
     * L'ordine di priorità è:
     * - Se c'è l'indirizzo, usa "indirizzo, location"
     * - Altrimenti se c'è il nome, usa "nome, location"
     * - Altrimenti solo la location
     * 
     * @param nome Nome del ristorante
     * @param location Città o paese
     * @param indirizzo Via e numero civico
     * @return Stringa da passare al geocoder
     */
    private String buildBestGeoQuery(String nome, String location, String indirizzo) {
        if (indirizzo != null && !indirizzo.isBlank()) 
        	return indirizzo + ", " + location;
        if (nome != null && !nome.isBlank()) 
        	return nome + ", " + location;
        return location;
    }

    // ==========================================================
    //                      UTIL DI NAVIGAZIONE
    // ==========================================================

    /**
     * Stampa una lista numerata di ristoranti e consente la selezione.
     * 
     * @param lista elenco ristoranti
     * @return ristorante selezionato o null se annullato
     */
    private Ristorante selezionaRistoranteDaLista(List<Ristorante> lista) {
        pulisciTerminale();
        if (lista == null || lista.isEmpty()) {
            System.out.println("Nessun ristorante.");
            pausa();
            return null;
        }
        System.out.println("\n--- Risultati ---");
        for (int i = 0; i < lista.size(); i++) {
            Ristorante r = lista.get(i);
            System.out.printf("%d) %s - %s  |  %s  |  ★%s%n",
                    i + 1,
                    r.getNome(),
                    r.getLocation(),
                    safe(r.getPrezzoMedio()),
                    formatMediaStelle(r));
        }
        System.out.println((lista.size() + 1) + ") Annulla / Indietro (* supportato)");
        System.out.print("Seleziona numero (o * per indietro): ");
        try {
            int idx = leggiIntInRange(1, lista.size() + 1);
            if (idx == lista.size() + 1) return null;
            return lista.get(idx - 1);
        } catch (InputAnnullatoException e) {
            return null;
        }
    }

    /**
     * Restituisce la media stelle formattata con una cifra decimale o "-" se non disponibile.
     * Vedi mediaStelle() della classe Ristorante.
     */
    private String formatMediaStelle(Ristorante r) {
        try {
            double media = r.mediaStelle();
            if (Double.isNaN(media) || media <= 0) 
            	return "-";
            return String.format("%.1f", media);
        } catch (Exception e) {
            return "-";
        }
    }

    /**
     * Restituisce "-" se la stringa è nulla o vuota, altrimenti la stringa originale.
     */
    private String safe(String s) {
        return (s == null || s.isBlank()) ? "-" : s;
    }

    // ==========================================================
    //                        I/O Helpers
    // ==========================================================

    /**
     * Attende invio per proseguire.
     */
    private void pausa() {
        System.out.print("\n(Invio per continuare) ");
        sc.nextLine();
    }

    /**
     * Legge una riga di testo. Se è * lancia InputAnnullatoException.
     * 
     * @return stringa letta 
     */
    private String leggiLineaRaw() {
        String s = sc.nextLine().trim();
        if (BACK_KEY.equals(s)) 
        	throw new InputAnnullatoException();
        return s;
    }

    /**
     * Legge una riga opzionale. Se è * lancia InputAnnullatoException.
     * Se vuota ritorna stringa vuota.
     */
    private String leggiLineaOpt() {
        String s = sc.nextLine().trim();
        if (BACK_KEY.equals(s)) 
        	throw new InputAnnullatoException();
        return s;
    }

    /**
     * Legge una stringa con prompt. Supporta "annulla" o * per annullare.
     * 
     * @param messaggio messaggio di input
     * @return null se invio, altrimenti stringa
     */
    private String leggiStringa (String messaggio) {
        System.out.print(messaggio);
        String input = sc.nextLine().trim();
        if (input.equalsIgnoreCase("annulla") || BACK_KEY.equals(input)) {
            throw new InputAnnullatoException();
        }
        return input.isEmpty() ? null : input;
    }

    /**
     * Legge un intero. * annulla.
     * 
     * @return valore intero valido
     */
    private int leggiInt () {
        while (true) {
            String s = sc.nextLine().trim();
            if (BACK_KEY.equals(s)) 
            	throw new InputAnnullatoException();
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.print("Inserisci un numero valido (o * per indietro): ");
            }
        }
    }

    /**
     * Stampa il messaggio di prompt e legge un intero nell’intervallo [min, max].
     * È un semplice wrapper di leggiIntInRange, ma con un prompt personalizzato.
     *
     * @param prompt messaggio da mostrare all’utente
     * @param min valore minimo accettato (incluso)
     * @param max valore massimo accettato (incluso)
     * @return intero inserito dall’utente nell’intervallo [min, max]
     * @throws InputAnnullatoException se l’utente inserisce '*' per annullare
     */
    private int leggiIntInRange(int min, int max) {
        while (true) {
            String s = sc.nextLine().trim();
            if (BACK_KEY.equals(s)) 
            	throw new InputAnnullatoException();
            try {
                int v = Integer.parseInt(s);
                if (v >= min && v <= max) 
                	return v;
                System.out.print("Valore non valido. Inserisci tra " + min + " e " + max + " (o * per indietro): ");
            } catch (NumberFormatException e) {
                System.out.print("Inserisci un numero valido (o * per indietro): ");
            }
        }
    }

    /**
     * Stampa il messaggio di prompt e legge un intero compreso tra min e max.
     * È un semplice wrapper di leggiIntInRange, ma con un prompt personalizzato.
     *
     * @param prompt messaggio da mostrare all’utente
     * @param min valore minimo accettato (incluso)
     * @param max valore massimo accettato (incluso)
     * @return intero scelto dall’utente nell’intervallo [min, max]
     * @throws InputAnnullatoException se l’utente inserisce '*' per annullare
     */
    private int leggiIntInRangePrompt(String prompt, int min, int max) {
        System.out.print(prompt);
        return leggiIntInRange(min, max);
    }

    /**
     * Legge un numero double positivo da input.
     * L’utente può annullare digitando '*'.
     *
     * @param prompt messaggio da mostrare all’utente
     * @return un double maggiore di 0
     * @throws InputAnnullatoException se l’utente inserisce '*'
     */
    private double leggiDoublePositivo(String prompt) {
        System.out.print(prompt);
        while (true) {
            String s = sc.nextLine().trim();
            if (BACK_KEY.equals(s)) 
            	throw new InputAnnullatoException();
            try {
                double v = Double.parseDouble(s);
                if (v > 0) 
                	return v;
                System.out.print("La distanza deve essere > 0 (o * per indietro): ");
            } catch (NumberFormatException e) {
                System.out.print("Inserisci un numero valido (o * per indietro): ");
            }
        }
    }

    /**
     * Legge un valore di Ruolo accettando CLIENTE o RISTORATORE. * annulla.
     */
    private Ruolo leggiRuolo () {
        while (true) {
            String s = sc.nextLine().trim();
            if (BACK_KEY.equals(s)) 
            	throw new InputAnnullatoException();
            try {
                return Ruolo.valueOf(s.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.print("Valore non valido. Scrivi CLIENTE o RISTORATORE (o * per indietro): ");
            }
        }
    }

    /**
     * Mostra una scelta guidata e restituisce la fascia prezzo testuale.
     * 
     * @return stringa fascia di prezzo oppure null per qualsiasi
     */
    private String leggiFasciaPrezzo () {
        System.out.println("Fascia di prezzo:");
        System.out.println("1) Economico (< 20€)");
        System.out.println("2) Medio (20-50€)");
        System.out.println("3) Costoso (> 50€)");
        System.out.println("4) Qualsiasi (invio)");
        System.out.print("Scelta (o * per indietro): ");

        String scelta = sc.nextLine().trim();
        if (BACK_KEY.equals(scelta)) 
        	throw new InputAnnullatoException();
        return switch (scelta) {
            case "1" -> "minore di 20€";
            case "2" -> "tra 20€ e 50€";
            case "3" -> "maggiore di 50€";
            default -> null;
        };
    }

    /**
     * Legge una risposta sì/no. Restituisce true/false oppure null se invio. * annulla.
     */
    private Boolean leggiSiNo (String messaggio) {
        System.out.print(messaggio);
        String input = sc.nextLine().trim().toLowerCase();
        if (BACK_KEY.equals(input)) 
        	throw new InputAnnullatoException();
        if (input.isEmpty()) 
        	return null;
        if (input.equals("s") || input.equals("si") || input.equals("y") || input.equals("yes")) 
        	return true;
        if (input.equals("n") || input.equals("no")) 
        	return false;
        System.out.println("Input non valido.");
        return null;
    }

    /**
     * Legge un minimo di stelle tra 1 e 5. Restituisce null se invio.
     */
    private Double leggiMinStelle () {
        System.out.print("Voto minimo (1-5, invio per ignorare, * per indietro): ");
        String input = sc.nextLine().trim();
        if (BACK_KEY.equals(input)) throw new InputAnnullatoException();
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

    /**
     * Chiede uno username non vuoto e non già esistente. * o "annulla" per interrompere.
     * 
     * @return username disponibile
     */
    private String leggiUsernameDisponibile() {
        while (true) {
            String u = sc.nextLine().trim();
            if ("annulla".equalsIgnoreCase(u) || BACK_KEY.equals(u)) 
            	throw new InputAnnullatoException();
            if (u.isEmpty()) { System.out.println("Campo obbligatorio."); continue; }
            if (utenteService.trovaUtente(u) != null) {
                System.out.println("Username non disponibile. Riprova (o * per indietro):");
                continue;
            }
            return u;
        }
    }

    /**
     * Chiede una password di 6–12 caratteri con conferma. * o "annulla" per interrompere.
     * 
     * @return password valida e confermata
     */
    private String leggiPasswordValida() {
        while (true) {
            String p = sc.nextLine();
            if ("annulla".equalsIgnoreCase(p) || BACK_KEY.equals(p)) throw new InputAnnullatoException();

            if (p.length() < 6 || p.length() > 12) {
                System.out.println("La password deve contenere tra i 6 e i 12 caratteri (o * per indietro).");
                continue;
            }

            System.out.print("Conferma password (o * per indietro): ");
            String c = sc.nextLine();
            if (BACK_KEY.equals(c)) throw new InputAnnullatoException();
            if (!p.equals(c)) {
                System.out.println("Le password non coincidono.");
                continue;
            }
            return p;
        }
    }

    /**
     * Chiede una stringa non vuota. Ripete finché non è fornita. * per indietro.
     * 
     * @param prompt messaggio da mostrare all'utente
     * @return stringa non vuota inserita dall'utente
     */
    private String leggiObbligatoria(String prompt) {
        while (true) {
            String s = leggiStringa(prompt);
            if (s != null && !s.isBlank()) 
            	return s;
            System.out.println("Campo obbligatorio (o * per indietro).");
        }
    }
    
    /**
     * Costruisce il link Google Maps per il ristorante.
     * Preferisce coordinate se presenti; altrimenti query testuale.
     * 
     * @param r Ristorante
     * @return URL Google Maps
     */
    private String mapsLink(Ristorante r) {
        double lat = 0.0, lon = 0.0;
        try {
            lat = r.getLatitudine();
            lon = r.getLongitudine();
        } catch (Exception ignored) {}

        boolean hasCoords = !Double.isNaN(lat) && !Double.isNaN(lon) && (lat != 0.0 || lon != 0.0);
        if (hasCoords) {
            return String.format(java.util.Locale.ROOT, "https://maps.google.com/?q=%.6f,%.6f", lat, lon);
        }

        String indirizzo = null;
        try { indirizzo = r.getIndirizzo(); } catch (Exception ignored) {}
        String location = safe(r.getLocation());
        String query = (indirizzo != null && !indirizzo.isBlank())
                ? indirizzo + ", " + location
                : r.getNome() + ", " + location;

        String enc = java.net.URLEncoder.encode(query, java.nio.charset.StandardCharsets.UTF_8);
        return "https://www.google.com/maps/search/?api=1&query=" + enc;
    }

}