package state;

import java.time.LocalDateTime;

public class Creneau {
    private final String nomCours;
    private final LocalDateTime horaire;
    private final int capaciteMax;
    private int nbInscrits;
    private EtatCreneau etat;

    public Creneau(String nomCours, LocalDateTime horaire, int capaciteMax) {
        this(nomCours, horaire, capaciteMax, 0, "Disponible");
    }

    public Creneau(String nomCours, LocalDateTime horaire, int capaciteMax, int nbInscrits, String nomEtat) {
        this.nomCours = nomCours;
        this.horaire = horaire;
        this.capaciteMax = capaciteMax;
        this.nbInscrits = nbInscrits;
        this.etat = creerEtatDepuisNom(nomEtat);
    }

    public String getNomCours() { return nomCours; }
    public LocalDateTime getHoraire() { return horaire; }
    public int getCapaciteMax() { return capaciteMax; }
    public int getNbInscrits() { return nbInscrits; }
    public String getEtat() { return etat.getNomEtat(); }

    public void reserver() {
        etat.reserver(this);
    }

    public void annuler() {
        etat.annuler(this);
    }

    public void changerEtat(EtatCreneau nouvelEtat) {
        this.etat = nouvelEtat;
    }

    public void incrementerInscrits() {
        nbInscrits++;
        if (nbInscrits >= capaciteMax) {
            etat = new EtatComplet();
        }
    }

    public void decrementerInscrits() {
        if (nbInscrits > 0) nbInscrits--;
        if (nbInscrits < capaciteMax && !(etat instanceof EtatDisponible)) {
            etat = new EtatDisponible();
        }
    }

    public void setNbInscrits(int nbInscrits) {
        this.nbInscrits = nbInscrits;
        if (nbInscrits >= capaciteMax) etat = new EtatComplet();
    }

    public void setEtat(String nomEtat) {
        this.etat = creerEtatDepuisNom(nomEtat);
    }

    private EtatCreneau creerEtatDepuisNom(String nomEtat) {
        return switch (nomEtat.toLowerCase()) {
            case "complet" -> new EtatComplet();
            case "fermé", "ferme" -> new EtatFerme();
            default -> new EtatDisponible();
        };
    }

    public EtatCreneau getEtatObjet() {
        return etat;
    }

    @Override
    public String toString() {
        return nomCours + " (" + horaire + ") - " + nbInscrits + "/" + capaciteMax + " - " + getEtat();
    }
}