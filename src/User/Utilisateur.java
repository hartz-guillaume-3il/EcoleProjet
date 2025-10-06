package User;

import java.util.UUID;

public abstract class Utilisateur {
    private final UUID id;
    private final Identifiants identifiants;
    private final Role role;

    protected Utilisateur(String email, String motDePasse, Role role) {
        this.id = UUID.randomUUID();
        this.identifiants = new Identifiants(email, motDePasse);
        this.role = role;
    }
    // constructeur alternatif depuis hash (persistance)
    protected Utilisateur(String email, String hashDejaCalcule, Role role, boolean depuisHash) {
        this.id = UUID.randomUUID();
        this.identifiants = Identifiants.depuisHash(email, hashDejaCalcule);
        this.role = role;
    }

    public UUID getId() { return id; }
    public String getEmail() { return identifiants.getEmail(); }
    public Role getRole() { return role; }
    public boolean verifierMotDePasse(String motDePasse) { return identifiants.correspond(motDePasse); }
    public String getEmpreinte() { return identifiants.getHash(); }   // ← pour l’écriture .txt
}
