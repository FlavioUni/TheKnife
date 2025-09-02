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
import java.util.stream.Collectors;

import theknife.ristorante.Ristorante;
import theknife.utente.*;
import theknife.eccezioni.InputAnnullatoException;
import theknife.recensione.Recensione;

public class MenuHandler {

    // ====== HARD-CODE ======
    private static final String UTENTI_CSV     = "data/Utenti.csv";
    private static final String RISTORANTI_CSV = "data/Ristoranti.csv";
    private static final String RECENSIONI_CSV = "data/Recensioni.csv";

    private final Scanner sc = new Scanner(System.in);
    private final DataContext data = new DataContext();
    private final UtenteService utenteService;
    private final RistoranteService ristoranteService;
    private final RecensioneService recensioneService;
    private final GeoService geoService = new GeoService();

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
                System.out.println("\n========= MENU PRINCIPALE =========");
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

    // ===================== OSPITE =====================
    private void menuOspite() {
        boolean continua = true;
        while (continua) {
            System.out.println("\n--------- OSPITE ---------");
            System.out.println("1) Cerca ristoranti (filtri)");
            System.out.println("2) Cerca ristoranti per indirizzo (geo + raggio km)");
            System.out.println("3) Torna indietro");
            System.out.print("Scelta: ");
            int scelta = leggiInt();

            switch (scelta) {
                case 1 -> flussoRicercaGenerale(null);
                case 2 -> flussoRicercaGeografica(null);
                case 3 -> continua = false;
                default -> System.out.println("Scelta non valida.");
            }
        }
    }

    // ===================== LOGIN / REGISTRAZIONE =====================
    private void registrazione () {
        System.out.println("\n--- REGISTRAZIONE ---");
        System.out.print("Nome: ");
        String nome = sc.nextLine().trim();

        System.out.print("Cognome: ");
        String cognome = sc.nextLine().trim();

        System.out.print("Username: ");
        String username = leggiUsernameDisponibile();

        System.out.print("Password (minimo 6 massimo 12 caratteri): ");
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
        System.out.println(ok ? "Registrazione completata." : "Registrazione NON riuscita.");
    }

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

    // ===================== CLIENTE =====================
    private void menuCliente (Utente utente) {
        boolean continua = true;
        while (continua) {
            System.out.println("\n--------- CLIENTE ---------");
            System.out.println("1) Cerca ristoranti (filtri)");
            System.out.println("2) Cerca ristoranti per indirizzo (geo + raggio km)");
            System.out.println("3) I miei preferiti");
            System.out.println("4) Le mie recensioni (visualizza/modifica)");
            System.out.println("5) Logout");
            System.out.print("Scelta: ");
            int scelta = leggiInt();

            switch (scelta) {
                case 1 -> flussoRicercaGenerale(utente);
                case 2 -> flussoRicercaGeografica(utente);
                case 3 -> flussoPreferiti(utente);
                case 4 -> flussoMieRecensioni(utente);
                case 5 -> continua = false;
                default -> System.out.println("Scelta non valida.");
            }
        }
    }

    // ===================== RISTORATORE =====================
    private void menuRistoratore (Utente ristoratore) {
        boolean continua = true;
        while (continua) {
            System.out.println("\n------ RISTORATORE ------");
            System.out.println("1) Inserisci NUOVO ristorante");
            System.out.println("2) I miei ristoranti (modifica/elimina)");
            System.out.println("3) Recensioni dei miei ristoranti (rispondi)");
            System.out.println("4) Logout");
            System.out.print("Scelta: ");
            int scelta = leggiInt();

            switch (scelta) {
                case 1 -> {
                    Ristorante nuovo = creaRistoranteConConfermaGeo();
                    if (nuovo == null) break; // annullato
                    boolean ok = ristoranteService.aggiungiRistorante(ristoratore, nuovo);
                    System.out.println(ok ? "Ristorante creato e aggiunto alla tua gestione."
                                          : "Impossibile creare/aggiungere (esiste già nome+location?).");
                }
                case 2 -> flussoGestioneRistoranti(ristoratore);
                case 3 -> flussoRecensioniGestite(ristoratore);
                case 4 -> continua = false;
                default -> System.out.println("Scelta non valida.");
            }
        }
    }

    // ==========================================================
    //                     FLUSSI COMUNI
    // ==========================================================

    /** Ricerca con filtri -> lista -> selezione -> pagina ristorante */
    private void flussoRicercaGenerale(Utente utenteCorrente) {
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
                return;
            }
            Ristorante scelto = selezionaRistoranteDaLista(risultati);
            if (scelto != null) paginaRistorante(scelto, utenteCorrente);
        } catch (InputAnnullatoException e) {
            System.out.println("Ricerca annullata.");
        }
    }

    /** Ricerca geografica -> lista -> selezione -> pagina ristorante */
    private void flussoRicercaGeografica(Utente utenteCorrente) {
        try {
            System.out.println("\n--- Ricerca per indirizzo ---");
            String indirizzo = leggiStringa("Inserisci un indirizzo (es. 'Milano' o 'Via Roma 10, Torino'): ");
            double km = leggiDoublePositivo("Distanza massima in km: ");
            List<Ristorante> vicini = ristoranteService.cercaVicinoA(indirizzo, km);
            if (vicini.isEmpty()) {
                System.out.println("Nessun ristorante entro " + km + " km.");
                return;
            }
            Ristorante scelto = selezionaRistoranteDaLista(vicini);
            if (scelto != null) paginaRistorante(scelto, utenteCorrente);
        } catch (InputAnnullatoException ex) {
            System.out.println("Ricerca annullata.");
        } catch (Exception ex) {
            System.out.println("Errore durante la ricerca: " + ex.getMessage());
        }
    }

    /** Vista dettagliata ristorante + recensioni; se cliente: azioni aggiuntive */
    private void paginaRistorante(Ristorante r, Utente utenteCorrente) {
        boolean isCliente = (utenteCorrente != null && utenteCorrente.getRuolo() == Ruolo.CLIENTE);
        while (true) {
            System.out.println("\n===== " + r.getNome() + " =====");
            System.out.println("Luogo: " + r.getLocation());
            System.out.println("Prezzo medio: " + safe(r.getPrezzoMedio()));
            System.out.println("Media stelle: " + formatMediaStelle(r));
            System.out.println("Premi: " + safe(r.getPremi()));
            System.out.println("---------------------------------");
            ristoranteService.visualizzaRecensioni(r);
            System.out.println("---------------------------------");

            if (!isCliente) {
                System.out.println("1) Torna indietro");
                System.out.print("Scelta: ");
                return;
            }

            // Cliente: opzioni extra
            System.out.println("1) Aggiungi ai preferiti");
            System.out.println("2) Rimuovi dai preferiti");
            System.out.println("3) Aggiungi/Modifica la mia recensione");
            System.out.println("4) Torna indietro");
            System.out.print("Scelta: ");
            int scelta = leggiInt();
            switch (scelta) {
                case 1 -> {
                    boolean ok = ristoranteService.aggiungiPreferito(utenteCorrente, r);
                    System.out.println(ok ? "Aggiunto ai preferiti." : "Già presente o non aggiunto.");
                }
                case 2 -> {
                    boolean ok = ristoranteService.rimuoviPreferito(utenteCorrente, r);
                    System.out.println(ok ? "Rimosso dai preferiti." : "Non presente o non rimosso.");
                }
                case 3 -> aggiungiOModificaMiaRecensione(utenteCorrente, r);
                case 4 -> { return; }
                default -> System.out.println("Scelta non valida.");
            }
        }
    }

    // ===================== CLIENTE: Preferiti + Mie Recensioni =====================
    private void flussoPreferiti(Utente utente) {
        List<Ristorante> preferiti = new ArrayList<>();
        try {
            // Provo a leggere direttamente dall'oggetto utente (pattern comune)
            if (utente.getRistorantiPreferiti() != null) {
                preferiti.addAll(utente.getRistorantiPreferiti());
            } else {
                // fallback: stampa e poi permetti apertura manuale
                utenteService.visualizzaPreferiti(utente.getUsername());
            }
        } catch (Exception ignored) {
            utenteService.visualizzaPreferiti(utente.getUsername());
        }

        if (preferiti.isEmpty()) {
            System.out.println("Nessun preferito trovato.");
            return;
        }
        Ristorante scelto = selezionaRistoranteDaLista(preferiti);
        if (scelto != null) paginaRistorante(scelto, utente);
    }

    private void flussoMieRecensioni(Utente utente) {
        List<Recensione> mie = data.getRecensioni().stream()
                .filter(r -> r.getAutore().equalsIgnoreCase(utente.getUsername()))
                .sorted(Comparator.comparing(Recensione::getData).reversed())
                .collect(Collectors.toList());

        if (mie.isEmpty()) {
            System.out.println("Non hai ancora pubblicato recensioni.");
            return;
        }

        System.out.println("\n--- Le mie recensioni ---");
        for (int i = 0; i < mie.size(); i++) {
            Recensione rec = mie.get(i);
            System.out.printf("%d) %s - %s | %d★ | \"%s\"%n",
                    i + 1,
                    rec.getNomeRistorante(),
                    rec.getLocationRistorante(),
                    rec.getStelle(),
                    rec.getDescrizione().length() > 60 ? rec.getDescrizione().substring(0, 57) + "..." : rec.getDescrizione());
        }
        System.out.println((mie.size() + 1) + ") Torna indietro");
        System.out.print("Scelta: ");
        int idx = leggiIntInRange(1, mie.size() + 1);
        if (idx == mie.size() + 1) return;

        Recensione daModificare = mie.get(idx - 1);
        Ristorante r = data.findRistorante(daModificare.getNomeRistorante(), daModificare.getNomeRistorante());
        if (r == null) {
            System.out.println("Ristorante non trovato (recensione orfana).");
            return;
        }
        modificaRecensioneFlow(utente, r, daModificare);
    }

    private void modificaRecensioneFlow(Utente utente, Ristorante r, Recensione target) {
        System.out.println("\n--- Modifica recensione su " + r.getNome() + " (" + r.getLocation() + ") ---");
        System.out.println("Attuale: " + target.getStelle() + "★ - " + target.getDescrizione());
        int nuoveStelle = leggiIntInRangePrompt("Nuove stelle (1-5): ", 1, 5);
        System.out.print("Nuovo testo: ");
        String nuovoTesto = sc.nextLine();

        try {
            // Assunzione: il service gestisce aggiornamento medie ecc.
            recensioneService.modificaRecensione(utente, target, nuoveStelle, nuovoTesto);
            System.out.println("Recensione modificata.");
        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }

    private void aggiungiOModificaMiaRecensione(Utente utente, Ristorante r) {
        Recensione esistente = r.trovaRecensioneDiUtente(utente.getUsername());
        if (esistente == null) {
            System.out.print("Stelle (1-5): ");
            int stelle = leggiIntInRange(1, 5);
            System.out.print("Commento: ");
            String testo = sc.nextLine();
            try {
                Recensione rec = recensioneService.aggiungiRecensione(utente, r, stelle, testo);
                System.out.println(rec != null ? "Recensione aggiunta." : "Impossibile aggiungere.");
            } catch (Exception e) {
                System.out.println("Errore: " + e.getMessage());
            }
        } else {
            modificaRecensioneFlow(utente, r, esistente);
        }
    }

    // ===================== RISTORATORE: gestione =====================
    private void flussoGestioneRistoranti(Utente ristoratore) {
        // Provo a leggere i ristoranti gestiti dall'utente
        List<Ristorante> miei = new ArrayList<>();
        try {
            if (ristoratore.getRistorantiGestiti() != null) {
                miei.addAll(ristoratore.getRistorantiGestiti());
            }
        } catch (Exception ignored) {}

        if (miei.isEmpty()) {
            System.out.println("Non gestisci alcun ristorante.");
            return;
        }

        Ristorante scelto = selezionaRistoranteDaLista(miei);
        if (scelto == null) return;

        boolean stay = true;
        while (stay) {
            System.out.println("\n--- Gestione: " + scelto.getNome() + " - " + scelto.getLocation() + " ---");
            System.out.println("1) Modifica campi principali");
            System.out.println("2) Elimina ristorante dalla mia gestione");
            System.out.println("3) Torna indietro");
            System.out.print("Scelta: ");
            int s = leggiInt();

            switch (s) {
                case 1 -> modificaCampiRistorante(scelto);
                case 2 -> {
                    boolean ok = utenteService.rimuoviRistoranteGestito(ristoratore.getUsername(), scelto);
                    System.out.println(ok ? "Rimosso dalla gestione." : "Non rimosso.");
                    stay = false;
                }
                case 3 -> stay = false;
                default -> System.out.println("Scelta non valida.");
            }
        }
    }

    private void modificaCampiRistorante(Ristorante r) {
        System.out.println("\n--- Modifica campi --- (invio per saltare)");
        System.out.print("Prezzo medio attuale: " + safe(r.getPrezzoMedio()) + " -> nuovo: ");
        String prezzo = sc.nextLine().trim();
        if (!prezzo.isEmpty()) r.setPrezzoMedio(prezzo);

        System.out.print("Telefono attuale: " + safe(r.getNumeroTelefono()) + " -> nuovo: ");
        String tel = sc.nextLine().trim();
        if (!tel.isEmpty()) r.setNumeroTelefono(tel);

        System.out.print("Website attuale: " + safe(r.getWebsiteUrl()) + " -> nuovo: ");
        String web = sc.nextLine().trim();
        if (!web.isEmpty()) r.setWebsiteUrl(web);

        System.out.print("Premi attuali: " + safe(r.getPremi()) + " -> nuovi: ");
        String premi = sc.nextLine().trim();
        if (!premi.isEmpty()) r.setPremi(premi);

        System.out.print("Servizi attuali: " + safe(r.getServizi()) + " -> nuovi: ");
        String servizi = sc.nextLine().trim();
        if (!servizi.isEmpty()) r.setServizi(servizi);

        Boolean delivery = leggiSiNo("Delivery? (s/n/invio per lasciare): ");
        if (delivery != null) r.setDelivery(delivery);
        Boolean pren = leggiSiNo("Prenotazione online? (s/n/invio per lasciare): ");
        if (pren != null) r.setPrenotazioneOnline(pren);

        System.out.println("Aggiornato.");
    }

    private void flussoRecensioniGestite(Utente ristoratore) {
        List<Ristorante> miei = new ArrayList<>();
        try {
            if (ristoratore.getRistorantiGestiti() != null) miei.addAll(ristoratore.getRistorantiGestiti());
        } catch (Exception ignored) {}

        if (miei.isEmpty()) {
            System.out.println("Non gestisci alcun ristorante.");
            return;
        }

        Ristorante scelto = selezionaRistoranteDaLista(miei);
        if (scelto == null) return;

        List<Recensione> recensioni = new ArrayList<>(scelto.getRecensioni());
        if (recensioni.isEmpty()) {
            System.out.println("Nessuna recensione per questo ristorante.");
            return;
        }

        System.out.println("\n--- Recensioni di " + scelto.getNome() + " ---");
        for (int i = 0; i < recensioni.size(); i++) {
            Recensione rec = recensioni.get(i);
            System.out.printf("%d) %s - %d★ - \"%s\"%n",
                    i + 1, rec.getAutore(), rec.getStelle(),
                    rec.getDescrizione().length() > 80 ? rec.getDescrizione().substring(0, 77) + "..." : rec.getDescrizione());
        }
        System.out.println((recensioni.size() + 1) + ") Torna indietro");
        System.out.print("Scelta: ");
        int idx = leggiIntInRange(1, recensioni.size() + 1);
        if (idx == recensioni.size() + 1) return;

        Recensione target = recensioni.get(idx - 1);
        System.out.print("Risposta: ");
        String resp = sc.nextLine();
        try {
            recensioneService.rispondiRecensione(ristoratore, scelto, target, resp);
            System.out.println("Risposta inviata.");
        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }

    // ===================== CREAZIONE RISTORANTE (con conferma Geo) =====================
    /** Flusso di creazione ristorante con conferma dell'indirizzo tramite geocoding. */
    private Ristorante creaRistoranteConConfermaGeo() {
        System.out.println("\n--- NUOVO RISTORANTE ---");
        System.out.println("(Digita 'annulla' in qualsiasi momento per tornare indietro)");

        String nome      = leggiObbligatoria("Nome: ");
        String location  = leggiObbligatoria("Località (es. \"Vienna, Austria\"): ");
        String indirizzo = leggiStringa("Indirizzo (via e civico) [invio per saltare]: ");
        String prezzo    = leggiStringa("Prezzo medio (es. \"25\" o \"€€\") [invio]: ");
        String cucina    = leggiStringa("Tipo di cucina [invio]: ");
        String telefono  = leggiStringa("Telefono [invio]: ");
        String website   = leggiStringa("Sito web (URL) [invio]: ");
        Boolean delivery = leggiSiNo("Delivery? (s/n/invio): ");
        Boolean pren     = leggiSiNo("Prenotazione online? (s/n/invio): ");

        double lat = 0.0, lon = 0.0;
        // Conferma geocoding sul miglior query
        while (true) {
            String query = buildBestGeoQuery(nome, location, indirizzo);
            double[] coords = null;
            try {
                coords = geoService.geocode(query); // atteso {lat, lon}
            } catch (Exception e) {
                System.out.println("[Geo] Errore geocoding: " + e.getMessage());
            }

            String display = query + (coords != null ? String.format(" [lat=%.6f lon=%.6f]", coords[0], coords[1]) : " [N/D]");
            System.out.println("Indirizzo interpretato: " + display);
            Boolean ok = leggiSiNo("Confermi? (s/n): ");
            if (Boolean.TRUE.equals(ok)) {
                if (coords != null) { lat = coords[0]; lon = coords[1]; }
                break;
            } else if (Boolean.FALSE.equals(ok)) {
                indirizzo = leggiStringa("Reinserisci indirizzo (invio per lasciare vuoto): ");
            } else {
                // utente ha dato invio -> considera come non confermato, ma esci
                break;
            }
        }

        Ristorante r = new Ristorante(
                nome,
                indirizzo != null ? indirizzo : "",
                location,
                prezzo != null ? prezzo : "",
                cucina != null ? cucina : "",
                lon, // ATTENZIONE: nella tua classe è (longitudine, latitudine)
                lat,
                telefono != null ? telefono : "",
                website != null ? website : "",
                "", // premi
                "", // servizi
                Boolean.TRUE.equals(pren),
                Boolean.TRUE.equals(delivery)
        );
        // salva coords se impostate
        r.setLatitudine(lat);
        r.setLongitudine(lon);
        return r;
    }

    private String buildBestGeoQuery(String nome, String location, String indirizzo) {
        if (indirizzo != null && !indirizzo.isBlank()) return indirizzo + ", " + location;
        if (nome != null && !nome.isBlank()) return nome + ", " + location;
        return location;
    }

    // ==========================================================
    //                      UTIL DI NAVIGAZIONE
    // ==========================================================
    private Ristorante selezionaRistoranteDaLista(List<Ristorante> lista) {
        if (lista == null || lista.isEmpty()) {
            System.out.println("Nessun ristorante.");
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
        System.out.println((lista.size() + 1) + ") Annulla / Indietro");
        System.out.print("Seleziona: ");
        int idx = leggiIntInRange(1, lista.size() + 1);
        if (idx == lista.size() + 1) return null;
        return lista.get(idx - 1);
    }

    private String formatMediaStelle(Ristorante r) {
        try {
            double media = r.mediaStelle();
            if (Double.isNaN(media) || media <= 0) return "-";
            return String.format("%.1f", media);
        } catch (Exception e) {
            return "-";
        }
    }

    private String safe(String s) {
        return (s == null || s.isBlank()) ? "-" : s;
    }

    // ==========================================================
    //                        I/O Helpers
    // ==========================================================
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

    private int leggiIntInRange(int min, int max) {
        while (true) {
            int v = leggiInt();
            if (v >= min && v <= max) return v;
            System.out.print("Valore non valido. Inserisci tra " + min + " e " + max + ": ");
        }
    }

    private int leggiIntInRangePrompt(String prompt, int min, int max) {
        System.out.print(prompt);
        return leggiIntInRange(min, max);
    }

    private double leggiDoublePositivo(String prompt) {
        System.out.print(prompt);
        while (true) {
            String s = sc.nextLine().trim();
            try {
                double v = Double.parseDouble(s);
                if (v > 0) return v;
                System.out.print("La distanza deve essere > 0: ");
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

    /** Chiede una password valida (6–12 char) con conferma. 'annulla' per interrompere. */
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

    /** Chiede una stringa non vuota; ripete finché non viene fornita. */
    private String leggiObbligatoria(String prompt) {
        while (true) {
            String s = leggiStringa(prompt);
            if (s != null && !s.isBlank()) return s;
            System.out.println("Campo obbligatorio.");
        }
    }

}