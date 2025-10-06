package User;

/** Représente un gestionnaire administrant l’application. */
public final class Gestionnaire extends Utilisateur {
    private final String nom;

    public Gestionnaire(String email, String motDePasse, String nom) {
        super(email, motDePasse, Role.GESTIONNAIRE);
        this.nom = nom;
    }

    public String getNom() { return nom; }
}
