package state;

public final class EtatFerme implements EtatCreneau {
    @Override public void reserver(Creneau c) { System.out.println("Créneau fermé. Réservation impossible."); }
    @Override public void annuler(Creneau c) { System.out.println("Créneau fermé. Annulation impossible."); }
    @Override public void afficherEtat() { System.out.println("État : fermé."); }
    @Override public String getNomEtat() { return "Fermé"; }
}
