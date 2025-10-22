package strategy.affectation;

import state.Creneau;

import java.util.List;

public final class AffectationPremierArrive implements StrategieAffectation {

    @Override
    public Creneau choisir(Candidat c, List<Creneau> cs) {
        for (Creneau cr : cs) {
            if (cr.getNbInscrits() < cr.getCapaciteMax()) {
                return cr;
            }
        }
        return null;
    }

    @Override
    public String nom() {
        return "Premier arrivé";
    }
}