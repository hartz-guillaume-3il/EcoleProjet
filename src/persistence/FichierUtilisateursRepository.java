package persistence;

import User.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Persistance des utilisateurs dans un fichier texte.
 * Format par ligne : email;hash;nom;prenom;role
 */
public final class FichierUtilisateursRepository {
    private final Path path;

    public FichierUtilisateursRepository(String cheminFichier) {
        this.path = Paths.get(cheminFichier);
    }

    /**
     * Lecture complète du fichier → liste d'utilisateurs.
     */
    public List<Utilisateur> charger() throws IOException {
        if (Files.notExists(path)) return List.of();
        List<Utilisateur> out = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            for (String l; (l = br.readLine()) != null; ) {
                if (l.isBlank()) continue;
                String[] t = l.split(";", -1); // email;hash;nom;prenom;role
                if (t.length < 5) continue;

                String email = t[0];
                String hash = t[1];
                String nom = t[2];
                String prenom = t[3];
                String role = t[4].toUpperCase(Locale.ROOT);

                switch (role) {
                    case "PARENT" -> out.add(new Parent(email, hash, nom, prenom, true));
                    case "GESTIONNAIRE" -> out.add(new Gestionnaire(email, hash, nom, true));
                    default -> { /* ignore ligne invalide */ }
                }
            }
        }
        return out;
    }

    /**
     * Ajoute un utilisateur en fin de fichier.
     */
    public synchronized void append(Utilisateur u) throws IOException {
        Files.createDirectories(path.getParent());
        try (BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            String nom = "", prenom = "";
            if (u instanceof Parent p) {
                nom = p.getNom();
                prenom = p.getPrenom();
            } else if (u instanceof Gestionnaire g) {
                nom = g.getNom();
            }
            bw.write(u.getEmail() + ";" + u.getEmpreinte() + ";" + nom + ";" + prenom + ";" + u.getRole());
            bw.newLine();
        }
    }

    /**
     * Réécrit l'intégralité du fichier à partir d'une collection.
     */
    public synchronized void ecrireTous(Collection<Utilisateur> users) throws IOException {
        Files.createDirectories(path.getParent());
        try (BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
            for (Utilisateur u : users) {
                String nom = "", prenom = "";
                if (u instanceof Parent p) {
                    nom = p.getNom();
                    prenom = p.getPrenom();
                } else if (u instanceof Gestionnaire g) {
                    nom = g.getNom();
                }
                bw.write(u.getEmail() + ";" + u.getEmpreinte() + ";" + nom + ";" + prenom + ";" + u.getRole());
                bw.newLine();
            }
        }
    }

    /**
     * Chemin du fichier pour debug/tests.
     */
    public Path getPath() {
        return path;
    }
}
