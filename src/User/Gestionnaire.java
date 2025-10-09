package User;

public final class Gestionnaire extends Utilisateur {
    private final String nom;

    public Gestionnaire(String email, String motDePasse, String nom) {
        super(email, motDePasse, Role.GESTIONNAIRE);
        this.nom = nom;
    }

    // depuis hash
    public Gestionnaire(String email, String hash, String nom, boolean depuisHash) {
        super(email, hash, Role.GESTIONNAIRE, true);
        this.nom = nom;
    }

    public String getNom() {
        return nom;
    }
}
