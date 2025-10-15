package state;

public final class EtatComplet implements EtatCreneau {
    @Override
    public void reserver(Creneau c) {
        System.out.println("Impossible : créneau complet.");
    }

    @Override
    public void annuler(Creneau c) {
        c.setNbInscrits();
        System.out.println("Annulation enregistrée.");
    }

    @Override
    public void afficherEtat() {
        System.out.println("État : complet.");
    }

    @Override
    public String getNomEtat() {
        return "Complet";
    }
}
