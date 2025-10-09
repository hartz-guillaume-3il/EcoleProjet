package strategy.affectation;

import state.Creneau;

import java.util.Comparator;
import java.util.List;

/**
 * Priorise le créneau avec le plus de places restantes, puis plus proche dans le temps.
 */
public final class AffectationParDisponibilite implements StrategieAffectation {
    @Override
    public Creneau choisir(Candidat c, List<Creneau> cs) {
        return cs.stream()
                .filter(cr -> cr.getInscrits() < cr.getCapaciteMax())
                .sorted(Comparator
                        .comparingInt((Creneau cr) -> cr.getCapaciteMax() - cr.getInscrits())
                        .reversed()
                        .thenComparing(Creneau::getHoraire))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String nom() {
        return "Disponibilités maximales";
    }
}
