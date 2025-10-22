package main;

import User.*;
import factory.UtilisateurFactory;
import facade.GestionCoursFacade;
import observer.*;
import state.Creneau;
import state.EtatFerme;
import strategy.paiement.*;
import strategy.affectation.*;
import decorator.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        System.out.println("=== DÉMONSTRATION DU SYSTÈME COMPLET ===\n");

        // Initialisation des notifications
        NotificationService base = new NotificationServiceBase();
        NotificationService serviceEmail = new NotificationEmail(new NotificationLogger(base));

        CentreNotifications centre = CentreNotifications.getInstance();
        centre.abonner(new ObservateurConsole("Console"));
        centre.abonner(notification -> serviceEmail.envoyer(notification));

        GestionCoursFacade facade = new GestionCoursFacade();

        // --- Création du premier gestionnaire via la Factory directement ---
        System.out.println("\n--- Création du gestionnaire ---");
        Utilisateur gestionnaire = UtilisateurFactory.creerUtilisateur(Role.GESTIONNAIRE, Map.of(
                "email", "admin@mail.com",
                "motDePasse", "admin123",
                "nom", "Martin"
        ));
        facade.ajouterUtilisateur(gestionnaire);

        // --- Ajout d'autres utilisateurs via la façade ---
        System.out.println("\n--- Création des utilisateurs ---");
        facade.inscrireUtilisateur(Role.PARENT, Map.of(
                "email", "parent1@mail.com",
                "motDePasse", "parent123",
                "nom", "Durand"
        ));
        facade.inscrireUtilisateur(Role.PARENT, Map.of(
                "email", "parent2@mail.com",
                "motDePasse", "parent456",
                "nom", "Lemoine"
        ));

        System.out.println("Utilisateurs enregistrés :");
        for (Utilisateur u : facade.listerUtilisateurs()) {
            System.out.println("- " + u.getEmail() + " (" + u.getRole() + ")");
        }

        // --- State : gestion des créneaux ---
        System.out.println("\n--- Gestion des créneaux (State + Observer) ---");
        Creneau c1 = facade.creerCreneau("Maths", LocalDateTime.now().plusDays(1), 2);
        Creneau c2 = facade.creerCreneau("Physique", LocalDateTime.now().plusDays(2), 1);

        facade.affecterEleve("Paul", 12);
        facade.affecterEleve("Emma", 14);
        facade.affecterEleve("Lucas", 13);

        c2.annuler();
        c2.changerEtat(new EtatFerme());
        c2.reserver();

        // --- Strategy : paiements multiples ---
        System.out.println("\n--- Calcul de paiements (Strategy) ---");
        facade.changerStrategiePaiement(new PaiementPlusieursVersements(3, BigDecimal.valueOf(2)));
        PlanPaiement plan = facade.calculerPaiement(BigDecimal.valueOf(180));
        plan.echeances().forEach(e ->
                System.out.println("Échéance : " + e.date() + " -> " + e.montant() + "€")
        );

        // --- Strategy : affectation ---
        System.out.println("\n--- Affectation (Strategy d’affectation) ---");
        facade.changerStrategieAffectation(new AffectationParDisponibilite());
        facade.affecterEleve("Nina", 11);

        // --- Observer + Decorator ---
        System.out.println("\n--- Notification manuelle (Decorator) ---");
        Notification notif = new Notification(
                TypeEvenement.PAIEMENT_RECU,
                "Votre paiement a bien été reçu.",
                Map.of("montant", "180€", "cours", "Maths"));
        serviceEmail.envoyer(notif);

        System.out.println("\n=== TEST TERMINÉ ===");
    }
}
