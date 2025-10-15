package state;

public final class EtatComplet implements EtatCreneau {

    @Override
    public void reserver(Creneau c) {
        System.out.println("Impossible : créneau complet.");
    }

    @Override
    public void annuler(Creneau c) {
        if (c.getNbInscrits() > 0) {
            c.decrementerInscrits();
            System.out.println("Annulation enregistrée. (" + c.getNbInscrits() + "/" + c.getCapaciteMax() + ")");
            if (c.getNbInscrits() < c.getCapaciteMax()) {
                c.changerEtat(new EtatDisponible());
                System.out.println("Le créneau redevient disponible.");
            }
        } else {
            System.out.println("Aucune inscription à annuler.");
        }
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