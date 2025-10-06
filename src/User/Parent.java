package User;

/** Représente un parent pouvant inscrire ses enfants. */
public final class Parent extends Utilisateur {
    private final String nom;
    private final String prenom;

    public Parent(String email, String motDePasse, String nom, String prenom) {
        super(email, motDePasse, Role.PARENT);
        this.nom = nom;
        this.prenom = prenom;
    }

    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
}
