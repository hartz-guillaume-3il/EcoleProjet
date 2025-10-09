package persistence;

import User.Inscription;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;

/**
 * Persistance des inscriptions dans un fichier texte.
 * Format par ligne : enfant;age;cours;horaireISO;horodatageISO
 */
public final class FichierInscriptionsRepository {
    private final Path path;

    public FichierInscriptionsRepository(String cheminFichier) {
        this.path = Paths.get(cheminFichier);
    }

    /**
     * Lecture complète du fichier → liste d'inscriptions.
     */
    public List<Inscription> charger() throws IOException {
        if (Files.notExists(path)) return List.of();
        List<Inscription> out = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            for (String l; (l = br.readLine()) != null; ) {
                if (l.isBlank()) continue;
                String[] t = l.split(";", -1); // enfant;age;cours;horaireISO;ts
                if (t.length < 5) continue;
                String enfant = t[0];
                int age;
                try {
                    age = Integer.parseInt(t[1]);
                } catch (NumberFormatException nfe) {
                    continue;
                }
                String cours = t[2];
                String horaireIso = t[3];
                Instant ts;
                try {
                    ts = Instant.parse(t[4]);
                } catch (Exception e) {
                    continue;
                }

                out.add(new Inscription(enfant, age, cours, horaireIso, ts));
            }
        }
        return out;
    }

    /**
     * Ajoute une inscription en fin de fichier.
     */
    public synchronized void append(Inscription i) throws IOException {
        Files.createDirectories(path.getParent());
        try (BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            bw.write(i.getNomEnfant() + ";" + i.getAge() + ";" + i.getCours() + ";" + i.getHoraireIso() + ";" + i.getHorodatage());
            bw.newLine();
        }
    }

    /**
     * Réécrit l'intégralité du fichier à partir d'une collection.
     */
    public synchronized void ecrireTous(Collection<Inscription> ins) throws IOException {
        Files.createDirectories(path.getParent());
        try (BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
            for (Inscription i : ins) {
                bw.write(i.getNomEnfant() + ";" + i.getAge() + ";" + i.getCours() + ";" + i.getHoraireIso() + ";" + i.getHorodatage());
                bw.newLine();
            }
        }
    }

    public Path getPath() {
        return path;
    }
}
