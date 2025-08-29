package theknife.test;

import theknife.csv.GestoreRistoranti;
import theknife.csv.GestoreUtenti;
import theknife.csv.GestoreRecensioni;

import theknife.ristorante.Ristorante;
import theknife.recensione.Recensione;
import theknife.utente.Utente;
import theknife.utente.Ruolo;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class TestGestoriCSV {

    public static void main(String[] args) throws Exception {
        // Percorsi CSV (adatta se i nomi sono diversi)
        String pUtenti     = Paths.get("data", "Utenti.csv").toString();
        String pRistoranti = Paths.get("data", "Ristoranti.csv").toString();
        String pRecensioni = Paths.get("data", "Recensioni.csv").toString();

        // 1) Caricamento
        GestoreUtenti gu = new GestoreUtenti();         gu.caricaDaCSV(pUtenti);
        GestoreRistoranti gr = new GestoreRistoranti(); gr.caricaDaCSV(pRistoranti);
        GestoreRecensioni gre = new GestoreRecensioni();gre.caricaDaCSV(pRecensioni);

        System.out.println("=== CARICAMENTO ===");
        System.out.println("Utenti:      " + gu.getElementi().size());
        System.out.println("Ristoranti:  " + gr.getElementi().size());
        System.out.println("Recensioni:  " + gre.getElementi().size());

        // 2) Collego Recensioni -> Ristoranti in RAM
        Map<String, Ristorante> byNomeRisto = new HashMap<>();
        for (Ristorante r : gr.getElementi()) {
            byNomeRisto.put(r.getNome().toLowerCase(), r);
            if (r.getRecensioni() != null) r.getRecensioni().clear(); // riparti pulito
        }
        int orfani = 0;
        for (Recensione rec : gre.getElementi()) {
            Ristorante r = byNomeRisto.get(rec.getNomeRistorante().toLowerCase());
            if (r != null) r.aggiungiRecensione(rec);
            else orfani++;
        }
        if (orfani > 0) System.out.println("Recensioni senza ristorante associato: " + orfani);

        // 3) Stampe rapide di verifica
        System.out.println("\n=== PRIME 3 RECENSIONI ===");
        gre.getElementi().stream().limit(3).forEach(r -> {
            System.out.println(r.visualizzaRecensione());
            System.out.println("--------------------");
        });

        System.out.println("=== PRIME 3 RISTORANTI (con numero recensioni) ===");
        gr.getElementi().stream().limit(3).forEach(r -> {
            int n = (r.getRecensioni() == null) ? 0 : r.getRecensioni().size();
            System.out.println("- " + r.getNome() + " | " + r.getLocation() + " | rec: " + n);
        });

        System.out.println("\n=== TOP 5 PER NUMERO DI RECENSIONI ===");
        gr.getElementi().stream()
                .sorted(Comparator.comparingInt((Ristorante r) ->
                        r.getRecensioni() == null ? 0 : r.getRecensioni().size()).reversed())
                .limit(5)
                .forEach(r -> {
                    int n = r.getRecensioni() == null ? 0 : r.getRecensioni().size();
                    System.out.println(n + "  -  " + r.getNome() + " (" + r.getLocation() + ")");
                });

        // 4) Modifiche di prova in RAM
        String userTest = "testuser_" + (System.currentTimeMillis() % 100000);
        Utente nuovo = new Utente("Test", "User", userTest, "Segre12", "Varese", LocalDate.now(), Ruolo.CLIENTE);
        gu.getElementi().add(nuovo);

        Recensione recTest = null;
        if (!gr.getElementi().isEmpty()) {
            Ristorante primo = gr.getElementi().get(0);
            recTest = new Recensione(
                    userTest,
                    primo.getNome(),
                    5,
                    "Recensione di prova dal main",
                    LocalDate.now(),
                    ""
            );
            gre.getElementi().add(recTest);
            primo.aggiungiRecensione(recTest); // mantieni coerente la RAM
        }

        System.out.println("\n=== DOPO AGGIUNTE DI PROVA ===");
        System.out.println("Utenti:     " + gu.getElementi().size());
        System.out.println("Recensioni: " + gre.getElementi().size());

        // 5) TEST SCRITTURA: salva su file OUT separati, ricarica e verifica
        Path outDir = Paths.get("data", "_out");
        Files.createDirectories(outDir);

        String uOut = outDir.resolve("Utenti_out.csv").toString();
        String rOut = outDir.resolve("Ristoranti_out.csv").toString();
        String reOut = outDir.resolve("Recensioni_out.csv").toString();

        gu.salvaSuCSV(uOut);
        gr.salvaSuCSV(rOut);
        gre.salvaSuCSV(reOut);
        System.out.println("\n[WRITE] CSV scritti in: " + outDir.toAbsolutePath());

        // Ricarico dai file OUT
        GestoreUtenti gu2 = new GestoreUtenti();         gu2.caricaDaCSV(uOut);
        GestoreRistoranti gr2 = new GestoreRistoranti(); gr2.caricaDaCSV(rOut);
        GestoreRecensioni gre2 = new GestoreRecensioni();gre2.caricaDaCSV(reOut);

        // Verifiche base: conteggi
        check("Utenti count", gu.getElementi().size(), gu2.getElementi().size());
        check("Ristoranti count", gr.getElementi().size(), gr2.getElementi().size());
        check("Recensioni count", gre.getElementi().size(), gre2.getElementi().size());

        // Verifica mirata: l'utente e la recensione di test esistono dopo reload
        boolean userOk = gu2.getElementi().stream().anyMatch(u -> u.getUsername().equals(userTest));
        System.out.println("[CHECK] Utente di test presente dopo reload: " + (userOk ? "OK" : "MANCANTE"));

        if (recTest != null) {
            String key = recTest.getAutore() + "||" + recTest.getNomeRistorante() + "||" + recTest.getDescrizione();
            Set<String> setReload = gre2.getElementi().stream()
                    .map(r -> r.getAutore() + "||" + r.getNomeRistorante() + "||" + r.getDescrizione())
                    .collect(Collectors.toSet());
            System.out.println("[CHECK] Recensione di test presente dopo reload: " + (setReload.contains(key) ? "OK" : "MANCANTE"));
        }

        System.out.println("\nFine test. I CSV originali NON sono stati toccati. I file di prova sono in data/_out/");
    }

    private static void check(String label, int expected, int actual) {
        System.out.println("[CHECK] " + label + " -> expected=" + expected + " | actual=" + actual +
                "  => " + (expected == actual ? "OK" : "DIFF"));
    }
}