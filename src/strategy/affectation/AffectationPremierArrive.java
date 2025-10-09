package strategy.affectation;

import state.Creneau;

import java.util.List;

/**
 * Premier arrivé, premier servi: on prend le premier créneau encore ouvert.
 */
public final class AffectationPremierArrive implements StrategieAffectation {
    @Override
    public Creneau choisir(Candidat c, List<Creneau> cs) {
        for (Creneau cr : cs) {
            if (cr.getInscrits() < cr.getCapaciteMax()) return cr;
        }
        return null;
    }

    @Override
    public String nom() {
        return "Premier arrivé";
    }
}
