package state;

public final class EtatFerme implements EtatCreneau {

    @Override
    public void reserver(Creneau c) {
        System.out.println("Réservation impossible : le créneau est fermé.");
    }

    @Override
    public void annuler(Creneau c) {
        System.out.println("Annulation impossible : le créneau est fermé.");
    }

    @Override
    public void afficherEtat() {
        System.out.println("État : fermé.");
    }

    @Override
    public String getNomEtat() {
        return "Fermé";
    }
}