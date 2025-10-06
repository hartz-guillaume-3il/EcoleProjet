package User;

public final class Parent extends Utilisateur {
    private final String nom;
    private final String prenom;

    public Parent(String email, String motDePasse, String nom, String prenom) {
        super(email, motDePasse, Role.PARENT);
        this.nom = nom; this.prenom = prenom;
    }
    public Parent(String email, String hash, String nom, String prenom, boolean depuisHash) {
        super(email, hash, Role.PARENT, true);
        this.nom = nom; this.prenom = prenom;
    }

    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
}
