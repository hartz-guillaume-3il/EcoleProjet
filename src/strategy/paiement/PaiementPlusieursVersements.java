package strategy.paiement;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class PaiementPlusieursVersements implements CalculPaiement {
    private final int nbVersements;
    private final BigDecimal tauxAnnuel;

    public PaiementPlusieursVersements(int nbVersements, BigDecimal tauxAnnuel) {
        if (nbVersements < 2 || nbVersements > 6)
            throw new IllegalArgumentException("nbVersements 2..6");
        this.nbVersements = nbVersements;
        this.tauxAnnuel = tauxAnnuel == null ? BigDecimal.ZERO : tauxAnnuel;
    }

    @Override
    public PlanPaiement calculer(BigDecimal prixCours) {
        // Taux mensuel simple = taux annuel / 12
        BigDecimal tauxMensuel = tauxAnnuel.divide(BigDecimal.valueOf(12), 6, RoundingMode.HALF_UP);
        BigDecimal montantTotal = prixCours;

        BigDecimal interet = prixCours.multiply(tauxMensuel).multiply(BigDecimal.valueOf(nbVersements));
        montantTotal = montantTotal.add(interet).setScale(2, RoundingMode.HALF_UP);

        BigDecimal base = montantTotal.divide(BigDecimal.valueOf(nbVersements), 2, RoundingMode.HALF_UP);

        List<Echeance> echeances = new ArrayList<>(nbVersements);
        BigDecimal total = BigDecimal.ZERO;

        for (int i = 0; i < nbVersements; i++) {
            BigDecimal montant = base;
            if (i == nbVersements - 1) {
                montant = montantTotal.subtract(base.multiply(BigDecimal.valueOf(nbVersements - 1)))
                        .setScale(2, RoundingMode.HALF_UP);
            }
            echeances.add(new Echeance(LocalDate.now().plusMonths(i), montant));
            total = total.add(montant);
        }

        return new PlanPaiement(echeances, total);
    }

    @Override
    public String nom() {
        return "Paiement en " + nbVersements + " versements (" + tauxAnnuel.multiply(BigDecimal.valueOf(100)) + "% d'intérêt annuel)";
    }
}