package User;

import persistence.FichierUtilisateursRepository;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class GestionnaireUtilisateurs {
    private static final GestionnaireUtilisateurs INSTANCE = new GestionnaireUtilisateurs();
    public static GestionnaireUtilisateurs getInstance() { return INSTANCE; }
    public boolean estVide() { return idParEmail.isEmpty(); }
    private final Map<UUID, Utilisateur> parId = new ConcurrentHashMap<>();
    private final Map<String, UUID> idParEmail = new ConcurrentHashMap<>();
    private FichierUtilisateursRepository repo;

    private GestionnaireUtilisateurs() {}

    // à appeler au démarrage
    public synchronized void initialiserFichier(String chemin) {
        this.repo = new FichierUtilisateursRepository(chemin);
        try {
            List<Utilisateur> charges = repo.charger();
            for (Utilisateur u : charges) {
                parId.put(u.getId(), u);
                idParEmail.put(u.getEmail().trim().toLowerCase(Locale.ROOT), u.getId());
            }
        } catch (IOException e) {

        }
    }

    public synchronized Utilisateur enregistrer(Utilisateur utilisateur) {
        String cle = normaliser(utilisateur.getEmail());
        if (idParEmail.containsKey(cle)) {
            throw new IllegalArgumentException("Email déjà utilisé : " + utilisateur.getEmail());
        }
        parId.put(utilisateur.getId(), utilisateur);
        idParEmail.put(cle, utilisateur.getId());
        if (repo != null) {
            try { repo.append(utilisateur); } catch (IOException ignored) {}
        }
        return utilisateur;
    }

    public Optional<Utilisateur> authentifier(String email, String motDePasse) {
        UUID id = idParEmail.get(normaliser(email));
        if (id == null) return Optional.empty();
        Utilisateur u = parId.get(id);
        return (u != null && u.verifierMotDePasse(motDePasse)) ? Optional.of(u) : Optional.empty();
    }

    public List<Utilisateur> listerTous() { return List.copyOf(parId.values()); }
    public List<Parent> listerParents() { return parId.values().stream().filter(u->u.getRole()==Role.PARENT).map(u->(Parent)u).collect(Collectors.toList()); }
    public List<Gestionnaire> listerGestionnaires(){ return parId.values().stream().filter(u->u.getRole()==Role.GESTIONNAIRE).map(u->(Gestionnaire)u).collect(Collectors.toList()); }

    private static String normaliser(String email) {
        return Objects.requireNonNull(email).trim().toLowerCase(Locale.ROOT);
    }
}
