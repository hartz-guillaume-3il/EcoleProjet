package strategy.paiement;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Jusqu’à 6 versements. Option frais fixe par versement.
 */
public final class PaiementPlusieursVersements implements CalculPaiement {
    private final int nbVersements;          // 2..6
    private final BigDecimal fraisParEcheance; // peut être 0

    public PaiementPlusieursVersements(int nbVersements, BigDecimal fraisParEcheance) {
        if (nbVersements < 2 || nbVersements > 6) throw new IllegalArgumentException("nbVersements 2..6");
        this.nbVersements = nbVersements;
        this.fraisParEcheance = fraisParEcheance == null ? BigDecimal.ZERO : fraisParEcheance;
    }

    @Override
    public PlanPaiement calculer(BigDecimal prixCours) {
        BigDecimal base = prixCours.divide(BigDecimal.valueOf(nbVersements), 2, RoundingMode.HALF_UP);
        List<Echeance> es = new ArrayList<>(nbVersements);
        BigDecimal total = BigDecimal.ZERO;

        for (int i = 0; i < nbVersements; i++) {
            BigDecimal montant = base.add(fraisParEcheance);
            // Ajuster la dernière échéance pour compenser l’arrondi
            if (i == nbVersements - 1) {
                BigDecimal cumule = base.multiply(BigDecimal.valueOf(nbVersements - 1)).add(fraisParEcheance.multiply(BigDecimal.valueOf(nbVersements - 1)));
                montant = prixCours.add(fraisParEcheance.multiply(BigDecimal.valueOf(nbVersements))).subtract(cumule);
                montant = montant.setScale(2, RoundingMode.HALF_UP);
            }
            es.add(new Echeance(LocalDate.now().plusMonths(i), montant));
            total = total.add(montant);
        }
        return new PlanPaiement(es, total);
    }

    @Override
    public String nom() {
        return "Paiement en " + nbVersements + " versements";
    }
}
