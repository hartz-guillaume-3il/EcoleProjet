package strategy.affectation;

import state.Creneau;

import java.util.Comparator;
import java.util.List;

public final class AffectationParAge implements StrategieAffectation {

    @Override
    public Creneau choisir(Candidat c, List<Creneau> cs) {
        return cs.stream()
                .filter(cr -> cr.getNbInscrits() < cr.getCapaciteMax())
                .sorted(Comparator.comparingInt(Creneau::getCapaciteMax))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String nom() {
        return "Priorité par âge";
    }
}