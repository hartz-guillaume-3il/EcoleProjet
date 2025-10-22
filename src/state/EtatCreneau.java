package state;

public interface EtatCreneau {
    void reserver(Creneau creneau);

    void annuler(Creneau creneau);

    void afficherEtat();

    String getNomEtat();
}
