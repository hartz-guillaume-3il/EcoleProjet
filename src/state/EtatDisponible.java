package state;

public final class EtatDisponible implements EtatCreneau {
    @Override
    public void reserver(Creneau c) {
        c.incrementerInscrits();
        System.out.println("Inscription réussie. (" + c.getInscrits() + "/" + c.getCapaciteMax() + ")");
    }

    @Override
    public void annuler(Creneau c) {
        System.out.println("Aucune annulation à effectuer.");
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
