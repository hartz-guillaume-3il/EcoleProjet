package strategy.affectation;

import state.Creneau;

import java.util.List;

public interface StrategieAffectation {
    Creneau choisir(Candidat candidat, List<Creneau> creneaux);

    String nom();
}
