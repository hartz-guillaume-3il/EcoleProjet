package state;

import java.time.LocalDateTime;

public class Creneau {
    private final String nomCours;
    private final LocalDateTime horaire;
    private final int capaciteMax;
    private int nbInscrits;
    private String etat;

    public Creneau(String nomCours, LocalDateTime horaire, int capaciteMax) {
        this(nomCours, horaire, capaciteMax, 0, "Disponible");
    }

    public Creneau(String nomCours, LocalDateTime horaire, int capaciteMax, int nbInscrits, String etat) {
        this.nomCours = nomCours;
        this.horaire = horaire;
        this.capaciteMax = capaciteMax;
        this.nbInscrits = nbInscrits;
        this.etat = etat;
    }

    public String getNomCours() { return nomCours; }
    public LocalDateTime getHoraire() { return horaire; }
    public int getCapaciteMax() { return capaciteMax; }
    public int getNbInscrits() { return nbInscrits; }
    public String getEtat() { return etat; }


    public void setNbInscrits(int nbInscrits) {
        this.nbInscrits = nbInscrits;
        if (nbInscrits >= capaciteMax) this.etat = "Complet";
    }

    public void setEtat(String etat) { this.etat = etat; }
}