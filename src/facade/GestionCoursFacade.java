package facade;

import User.*;
import factory.UtilisateurFactory;
import observer.*;
import state.Creneau;
import strategy.affectation.*;
import strategy.paiement.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public final class GestionCoursFacade {

    private final GestionnaireUtilisateurs gestionnaireUtilisateurs = GestionnaireUtilisateurs.getInstance();
    private final CentreNotifications notifications = CentreNotifications.getInstance();
    private final List<Creneau> creneaux = new ArrayList<>();

    // Stratégies courantes
    private CalculPaiement strategiePaiement = new PaiementUneFois();
    private StrategieAffectation strategieAffectation = new AffectationParDisponibilite();

    /** Enregistre un nouvel utilisateur via la Factory */
    public Utilisateur inscrireUtilisateur(Role role, Map<String, String> infos) {
        Utilisateur utilisateur = UtilisateurFactory.creerUtilisateur(role, infos);
        gestionnaireUtilisateurs.enregistrer(utilisateur);
        return utilisateur;
    }

    /** Authentifie un utilisateur */
    public Optional<Utilisateur> connecter(String email, String motDePasse) {
        return gestionnaireUtilisateurs.authentifier(email, motDePasse);
    }

    /** Ajoute un nouveau créneau de cours */
    public Creneau creerCreneau(String nomCours, LocalDateTime horaire, int capacite) {
        Creneau c = new Creneau(nomCours, horaire, capacite);
        creneaux.add(c);
        return c;
    }

    /** Affecte un élève à un créneau disponible */
    public Optional<Creneau> affecterEleve(String nomEleve, int age) {
        Candidat c = new Candidat(nomEleve, age, System.nanoTime());
        Creneau choix = strategieAffectation.choisir(c, creneaux);
        if (choix != null) {
            choix.reserver();
            return Optional.of(choix);
        }
        notifications.notifierTous(
                new Notification(TypeEvenement.CRENEAU_ANNULE,
                        "Aucun créneau disponible pour " + nomEleve,
                        Map.of("nomEleve", nomEleve)));
        return Optional.empty();
    }

    /** Calcule un plan de paiement pour un cours */
    public PlanPaiement calculerPaiement(BigDecimal montant) {
        return strategiePaiement.calculer(montant);
    }

    /** Permet de changer la stratégie de paiement */
    public void changerStrategiePaiement(CalculPaiement nouvelleStrategie) {
        this.strategiePaiement = nouvelleStrategie;
    }

    /** Permet de changer la stratégie d’affectation */
    public void changerStrategieAffectation(StrategieAffectation nouvelleStrategie) {
        this.strategieAffectation = nouvelleStrategie;
    }

    /** Liste tous les créneaux */
    public List<Creneau> listerCreneaux() {
        return List.copyOf(creneaux);
    }

    /** Liste tous les utilisateurs */
    public List<Utilisateur> listerUtilisateurs() {
        return gestionnaireUtilisateurs.listerTous();
    }
}
