package User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Singleton de gestion des utilisateurs :
 * enregistrement, authentification, requêtes et suppression.
 */
public final class GestionnaireUtilisateurs {
    // --- Singleton ---
    private static final GestionnaireUtilisateurs INSTANCE = new GestionnaireUtilisateurs();
    public static GestionnaireUtilisateurs getInstance() { return INSTANCE; }

    // --- Stockage en mémoire ---
    private final Map<UUID, Utilisateur> parId = new ConcurrentHashMap<>();
    private final Map<String, UUID> idParEmail = new ConcurrentHashMap<>();

    private GestionnaireUtilisateurs() {}

    // --- Enregistrement ---
    public synchronized Utilisateur enregistrer(Utilisateur utilisateur) {
        String cle = normaliser(utilisateur.getEmail());
        if (idParEmail.containsKey(cle)) {
            throw new IllegalArgumentException("Email déjà utilisé : " + utilisateur.getEmail());
        }
        parId.put(utilisateur.getId(), utilisateur);
        idParEmail.put(cle, utilisateur.getId());
        return utilisateur;
    }

    // --- Authentification ---
    public Optional<Utilisateur> authentifier(String email, String motDePasse) {
        UUID id = idParEmail.get(normaliser(email));
        if (id == null) return Optional.empty();
        Utilisateur u = parId.get(id);
        return (u != null && u.verifierMotDePasse(motDePasse)) ? Optional.of(u) : Optional.empty();
    }

    // --- Requêtes ---
    public Optional<Utilisateur> trouverParEmail(String email) {
        UUID id = idParEmail.get(normaliser(email));
        return id == null ? Optional.empty() : Optional.ofNullable(parId.get(id));
    }

    public Optional<Utilisateur> trouverParId(UUID id) {
        return Optional.ofNullable(parId.get(id));
    }

    public List<Utilisateur> listerTous() {
        return List.copyOf(parId.values());
    }

    public List<Parent> listerParents() {
        return parId.values().stream()
                .filter(u -> u.getRole() == Role.PARENT)
                .map(u -> (Parent) u)
                .collect(Collectors.toList());
    }

    public List<Gestionnaire> listerGestionnaires() {
        return parId.values().stream()
                .filter(u -> u.getRole() == Role.GESTIONNAIRE)
                .map(u -> (Gestionnaire) u)
                .collect(Collectors.toList());
    }

    // --- Suppression ---
    public synchronized boolean supprimerParEmail(String email) {
        UUID id = idParEmail.remove(normaliser(email));
        if (id == null) return false;
        parId.remove(id);
        return true;
    }

    private static String normaliser(String email) {
        return Objects.requireNonNull(email).trim().toLowerCase(Locale.ROOT);
    }
}
