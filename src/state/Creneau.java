package state;

import observer.CentreNotifications;
import observer.Notification;
import observer.TypeEvenement;

import java.time.LocalDateTime;
import java.util.Map;

/** Contexte State + publication d’événements vers l’Observer. */
public final class Creneau {
    private final String nomCours;
    private final LocalDateTime horaire;
    private final int capaciteMax;
    private int inscrits = 0;

    private EtatCreneau etat;

    public Creneau(String nomCours, LocalDateTime horaire, int capaciteMax) {
        this.nomCours = nomCours;
        this.horaire = horaire;
        this.capaciteMax = capaciteMax;
        this.etat = new EtatDisponible();
    }

    public void changerEtat(EtatCreneau nouvelEtat) { this.etat = nouvelEtat; }

    public void reserver() { etat.reserver(this); }

    public void annuler() { etat.annuler(this); }

    public void afficherEtat() { etat.afficherEtat(); }

    public String getNomCours() { return nomCours; }
    public int getInscrits() { return inscrits; }
    public int getCapaciteMax() { return capaciteMax; }
    public LocalDateTime getHoraire() { return horaire; }
    public EtatCreneau getEtat() { return etat; }

    public void incrementerInscrits() {
        inscrits++;
        notifier(TypeEvenement.CRENEAU_CONFIRME, "Inscription confirmée");
        if (inscrits >= capaciteMax) {
            changerEtat(new EtatComplet());
            notifier(TypeEvenement.CRENEAU_DEVENU_COMPLET, "Créneau devenu complet");
        }
    }

    public void decrementerInscrits() {
        if (inscrits > 0) {
            inscrits--;
            notifier(TypeEvenement.CRENEAU_ANNULE, "Inscription annulée");
            if (etat instanceof EtatComplet && inscrits < capaciteMax) {
                changerEtat(new EtatDisponible());
                notifier(TypeEvenement.CRENEAU_REDEVENU_DISPONIBLE, "Créneau redevenu disponible");
            }
        }
    }

    private void notifier(TypeEvenement type, String message) {
        CentreNotifications.getInstance().notifierTous(
                new Notification(
                        type,
                        message,
                        Map.of(
                                "cours", nomCours,
                                "horaire", horaire.toString(),
                                "inscrits", String.valueOf(inscrits),
                                "capacite", String.valueOf(capaciteMax)
                        )
                )
        );
    }
}
