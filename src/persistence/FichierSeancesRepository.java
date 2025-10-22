package persistence;

import state.Creneau;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

public final class FichierSeancesRepository {
    private final Path path;

    public FichierSeancesRepository(String cheminFichier) {
        this.path = Paths.get(cheminFichier);
    }

    public List<Creneau> charger() throws IOException {
        if (Files.notExists(path)) return List.of();
        List<Creneau> out = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            for (String l; (l = br.readLine()) != null; ) {
                if (l.isBlank()) continue;
                String[] t = l.split(";", -1);
                if (t.length < 5) continue;  // 5 colonnes attendues
                try {
                    String cours = t[0];
                    LocalDateTime horaire = LocalDateTime.parse(t[1]);
                    int capacite = Integer.parseInt(t[2]);
                    int inscrits = Integer.parseInt(t[3]);
                    String etat = t[4];
                    out.add(new Creneau(cours, horaire, capacite, inscrits, etat));
                } catch (Exception ignored) {}
            }
        }
        return out;
    }

    public synchronized void append(Creneau c) throws IOException {
        Files.createDirectories(path.getParent());
        try (BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            bw.write(String.join(";", c.getNomCours(),
                    c.getHoraire().toString(),
                    String.valueOf(c.getCapaciteMax()),
                    String.valueOf(c.getNbInscrits()),
                    c.getEtat()));
            bw.newLine();
        }
    }

    public synchronized void ecrireTous(Collection<Creneau> creneaux) throws IOException {
        Files.createDirectories(path.getParent());
        try (BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
            for (Creneau c : creneaux) {
                bw.write(String.join(";", c.getNomCours(),
                        c.getHoraire().toString(),
                        String.valueOf(c.getCapaciteMax()),
                        String.valueOf(c.getNbInscrits()),
                        c.getEtat()));
                bw.newLine();
            }
        }
    }
}
