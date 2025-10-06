package strategy.affectation;

import state.Creneau;
import java.util.List;

/** Choisit le meilleur créneau pour un candidat, ou null si aucun. */
public interface StrategieAffectation {
    Creneau choisir(Candidat candidat, List<Creneau> creneaux);
    String nom();
}
