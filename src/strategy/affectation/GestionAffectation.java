package strategy.affectation;

import state.Creneau;

import java.util.List;

public final class GestionAffectation {
    private StrategieAffectation strategie;

    public GestionAffectation(StrategieAffectation strategie) {
        this.strategie = strategie;
    }

    public void changerStrategie(StrategieAffectation strategie) {
        this.strategie = strategie;
    }

    public Creneau affecter(Candidat c, List<Creneau> creneaux) {
        return strategie.choisir(c, creneaux);
    }

    public String strategieActive() {
        return strategie.nom();
    }
}
