package persistence;

import User.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public final class FichierUtilisateursRepository {
    private final Path chemin;

    public FichierUtilisateursRepository(String cheminFichier) {
        this.chemin = Paths.get(cheminFichier);
    }

    public List<Utilisateur> charger() throws IOException {
        if (Files.notExists(chemin)) return List.of();
        List<Utilisateur> res = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(chemin, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                // email;hash;nom;prenom;role
                if (line.isBlank()) continue;
                String[] t = line.split(";", -1);
                if (t.length < 5) continue;
                String email = t[0], hash = t[1], nom = t[2], prenom = t[3], role = t[4];
                if ("PARENT".equalsIgnoreCase(role)) {
                    res.add(new Parent(email, hash, nom, prenom, true));
                } else if ("GESTIONNAIRE".equalsIgnoreCase(role)) {
                    res.add(new Gestionnaire(email, hash, nom, true));
                }
            }
        }
        return res;
    }

    public synchronized void ecrireTous(Collection<Utilisateur> users) throws IOException {
        Files.createDirectories(chemin.getParent());
        try (BufferedWriter bw = Files.newBufferedWriter(chemin, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
            for (Utilisateur u : users) {
                String nom="", prenom="";
                if (u instanceof Parent p) { nom = p.getNom(); prenom = p.getPrenom(); }
                else if (u instanceof Gestionnaire g) { nom = g.getNom(); }
                bw.write(u.getEmail() + ";" + u.getEmpreinte() + ";" + nom + ";" + prenom + ";" + u.getRole());
                bw.newLine();
            }
        }
    }

    public synchronized void append(Utilisateur u) throws IOException {
        Files.createDirectories(chemin.getParent());
        try (BufferedWriter bw = Files.newBufferedWriter(chemin, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            String nom="", prenom="";
            if (u instanceof Parent p) { nom = p.getNom(); prenom = p.getPrenom(); }
            else if (u instanceof Gestionnaire g) { nom = g.getNom(); }
            bw.write(u.getEmail() + ";" + u.getEmpreinte() + ";" + nom + ";" + prenom + ";" + u.getRole());
            bw.newLine();
        }
    }
}
