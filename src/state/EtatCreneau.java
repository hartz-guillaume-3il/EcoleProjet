package state;

/** Interface commune à tous les états possibles d’un créneau. */
public interface EtatCreneau {
    void reserver(Creneau creneau);
    void annuler(Creneau creneau);
    void afficherEtat();
    String getNomEtat();
}
