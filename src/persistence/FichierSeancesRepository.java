// src/main/java/persistence/FichierSeancesRepository.java
package persistence;

import state.Creneau;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Persistance des séances (créneaux) dans un fichier texte.
 * Format par ligne : cours;horaireISO;capacite
 */
public final class FichierSeancesRepository {
    private final Path path;

    public FichierSeancesRepository(String cheminFichier) {
        this.path = Paths.get(cheminFichier);
    }

    /**
     * Lecture complète du fichier → liste de Creneau.
     */
    public List<Creneau> charger() throws IOException {
        if (Files.notExists(path)) return List.of();
        List<Creneau> out = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            for (String l; (l = br.readLine()) != null; ) {
                if (l.isBlank()) continue;
                String[] t = l.split(";", -1); // cours;horaireISO;capacite
                if (t.length < 3) continue;
                String cours = t[0];
                LocalDateTime horaire;
                int capacite;
                try {
                    horaire = LocalDateTime.parse(t[1]);
                    capacite = Integer.parseInt(t[2]);
                } catch (Exception e) {
                    continue; // ligne invalide ignorée
                }
                out.add(new Creneau(cours, horaire, capacite));
            }
        }
        return out;
    }

    /**
     * Ajoute un créneau en fin de fichier.
     */
    public synchronized void append(Creneau c) throws IOException {
        Files.createDirectories(path.getParent());
        try (BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            bw.write(c.getNomCours() + ";" + c.getHoraire() + ";" + c.getCapaciteMax());
            bw.newLine();
        }
    }

    /**
     * Réécrit l'intégralité du fichier.
     */
    public synchronized void ecrireTous(Collection<Creneau> creneaux) throws IOException {
        Files.createDirectories(path.getParent());
        try (BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
            for (Creneau c : creneaux) {
                bw.write(c.getNomCours() + ";" + c.getHoraire() + ";" + c.getCapaciteMax());
                bw.newLine();
            }
        }
    }

    public Path getPath() {
        return path;
    }
}
