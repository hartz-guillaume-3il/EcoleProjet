package state;

public final class EtatDisponible implements EtatCreneau {

    @Override
    public void reserver(Creneau c) {
        if (c.getNbInscrits() < c.getCapaciteMax()) {
            c.incrementerInscrits();
            System.out.println("Inscription réussie. (" + c.getNbInscrits() + "/" + c.getCapaciteMax() + ")");
            if (c.getNbInscrits() >= c.getCapaciteMax()) {
                c.changerEtat(new EtatComplet());
                System.out.println("Le créneau est maintenant complet.");
            }
        } else {
            c.changerEtat(new EtatComplet());
            System.out.println("Impossible d’inscrire : créneau complet.");
        }
    }

    @Override
    public void annuler(Creneau c) {
        if (c.getNbInscrits() > 0) {
            c.decrementerInscrits();
            System.out.println("Une inscription a été annulée. (" + c.getNbInscrits() + "/" + c.getCapaciteMax() + ")");
            if (c.getEtat() instanceof EtatComplet && c.getNbInscrits() < c.getCapaciteMax()) {
                c.changerEtat(new EtatDisponible());
            }
        } else {
            System.out.println("Aucune inscription à annuler.");
        }
    }

    @Override
    public void afficherEtat() {
        System.out.println("État : disponible.");
    }

    @Override
    public String getNomEtat() {
        return "Disponible";
    }
}
