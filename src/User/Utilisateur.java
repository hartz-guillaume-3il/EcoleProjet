package User;

import java.util.UUID;

/** Classe de base commune à tous les utilisateurs. */
public abstract class Utilisateur {
    private final UUID id;
    private final Identifiants identifiants;
    private final Role role;

    protected Utilisateur(String email, String motDePasse, Role role) {
        this.id = UUID.randomUUID();
        this.identifiants = new Identifiants(email, motDePasse);
        this.role = role;
    }

    public UUID getId() { return id; }
    public String getEmail() { return identifiants.getEmail(); }
    public Role getRole() { return role; }

    public boolean verifierMotDePasse(String motDePasse) {
        return identifiants.correspond(motDePasse);
    }
}
