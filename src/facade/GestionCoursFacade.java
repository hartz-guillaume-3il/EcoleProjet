package facade;

import User.*;
import persistence.*;
import factory.UtilisateurFactory;
import observer.*;
import security.Session;
import state.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import strategy.affectation.*;
import strategy.paiement.*;

public final class GestionCoursFacade {
    private final GestionnaireUtilisateurs gestionnaireUtilisateurs = GestionnaireUtilisateurs.getInstance();
    private FichierSeancesRepository repoSea;
    private final CentreNotifications notifications = CentreNotifications.getInstance();
    private final List<Creneau> creneaux = new ArrayList<>();
    private final Session session = new Session();
    private final List<Inscription> inscriptions = new ArrayList<>();
    private FichierInscriptionsRepository repoIns;

    // ---------- STRATEGIES ----------
    private final GestionPaiement gestionPaiement = new GestionPaiement(new PaiementUneFois());
    private StrategieAffectation strategieAffectation = new AffectationParDisponibilite();

    // ---------- Initialisation ----------

    public void initFichierUtilisateurs(String chemin) {
        gestionnaireUtilisateurs.initialiserFichier("EcoleProjet/src/data/utilisateurs.txt");
    }

    public void initFichierSeance(String chemin) {
        repoSea = new FichierSeancesRepository("EcoleProjet/src/data/seance.txt");
        try {
            creneaux.addAll(repoSea.charger());
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement des séances : " + e.getMessage());
        }
    }

    public void initFichierInscriptions(String chemin) {
        repoIns = new FichierInscriptionsRepository("EcoleProjet/src/data/inscriptions.txt");
        try {
            inscriptions.addAll(repoIns.charger());
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement des inscriptions : " + e.getMessage());
        }
    }

    // ---------- Authentification ----------

    public Optional<Utilisateur> connecter(String email, String motDePasse, Role roleAttendu) {
        Optional<Utilisateur> opt = gestionnaireUtilisateurs.authentifier(email, motDePasse);
        if (opt.isPresent() && opt.get().getRole() == roleAttendu) {
            session.ouvrir(opt.get());
            return opt;
        }
        return Optional.empty();
    }

    public void deconnecter() {
        session.fermer();
    }

    public Session getSession() {
        return session;
    }

    // ---------- Gestion des utilisateurs ----------

    public Utilisateur inscrireUtilisateur(Role role, Map<String, String> infos) {
        if (!session.estGestionnaire()) throw new SecurityException("Réservé au gestionnaire");
        Utilisateur u = UtilisateurFactory.creerUtilisateur(role, infos);
        gestionnaireUtilisateurs.enregistrer(u);
        return u;
    }

    public Utilisateur autoInscriptionParent(Map<String, String> infos) {
        Utilisateur u = UtilisateurFactory.creerUtilisateur(Role.PARENT, infos);
        gestionnaireUtilisateurs.enregistrer(u);
        return u;
    }

    public Utilisateur bootstrapGestionnaire(Map<String, String> infos) {
        if (existeGestionnaire()) throw new SecurityException("Déjà initialisé");
        Utilisateur u = UtilisateurFactory.creerUtilisateur(Role.GESTIONNAIRE, infos);
        gestionnaireUtilisateurs.enregistrer(u);
        return u;
    }

    public boolean peutCreerGestionnaireDepuisLogin() {
        return gestionnaireUtilisateurs.estVide() || !gestionnaireUtilisateurs.existeGestionnaire();
    }

    public boolean existeGestionnaire() {
        return gestionnaireUtilisateurs.existeGestionnaire();
    }

    // ---------- Gestion des créneaux ----------

    public Creneau creerCreneau(String nomCours, LocalDateTime horaire, int capacite) {
        if (!session.estGestionnaire()) throw new SecurityException("Réservé au gestionnaire");
        Creneau c = new Creneau(nomCours, horaire, capacite);
        creneaux.add(c);
        try {
            if (repoSea != null) repoSea.append(c);
        } catch (IOException e) {
            System.err.println("Erreur lors de l’écriture d’un créneau : " + e.getMessage());
        }
        return c;
    }

    public void fermerInscriptions(Creneau c) {
        if (!session.estGestionnaire()) throw new SecurityException("Réservé au gestionnaire");
        c.setEtat("Fermé");
        sauvegarderSeances();
    }

    public void ouvrirInscriptions(Creneau c) {
        if (!session.estGestionnaire()) throw new SecurityException("Réservé au gestionnaire");
        c.setEtat("Disponible");
        sauvegarderSeances();
    }

    public List<Creneau> listerCreneaux() {
        return List.copyOf(creneaux);
    }

    private void sauvegarderSeances() {
        try {
            if (repoSea != null) repoSea.ecrireTous(creneaux);
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde des séances : " + e.getMessage());
        }
    }

    // ---------- Inscriptions ----------

    public Inscription inscrireEnfantDans(Creneau c, String nomEnfant, int age) {
        if (!session.estParent()) throw new SecurityException("Réservé au parent");
        c.setNbInscrits(c.getNbInscrits() + 1);
        Inscription i = new Inscription(nomEnfant, age, c);
        inscriptions.add(i);

        try {
            if (repoIns != null) repoIns.append(i);
            if (repoSea != null) repoSea.ecrireTous(creneaux);
        } catch (IOException e) {
            System.err.println("Erreur lors de l’inscription : " + e.getMessage());
        }

        return i;
    }

    public List<Inscription> listerInscriptions() {
        return List.copyOf(inscriptions);
    }

    // ---------- Affectation ----------

    public Optional<Creneau> affecterEleve(String nomEleve, int age) {
        if (!session.estParent()) throw new SecurityException("Réservé au parent");
        Candidat c = new Candidat(nomEleve, age, System.nanoTime());
        Creneau choix = strategieAffectation.choisir(c, creneaux);
        if (choix != null) {
            choix.setNbInscrits(choix.getNbInscrits() + 1);
            sauvegarderSeances();
            return Optional.of(choix);
        }
        notifications.notifierTous(new Notification(
                TypeEvenement.CRENEAU_ANNULE,
                "Aucun créneau disponible",
                Map.of("eleve", nomEleve)
        ));
        return Optional.empty();
    }

    // ---------- Paiement (avec GestionPaiement) ----------

    public PlanPaiement calculerPaiement(BigDecimal montant) {
        return gestionPaiement.genererPlan(montant);
    }

    public void changerStrategiePaiement(CalculPaiement nouvelleStrategie) {
        gestionPaiement.changerStrategie(nouvelleStrategie);
    }

    public String strategiePaiementActive() {
        return gestionPaiement.strategieActive();
    }

    // ---------- Affectation ----------

    public void changerStrategieAffectation(StrategieAffectation s) {
        this.strategieAffectation = s;
    }

    // ---------- Consultation ----------

    public List<Utilisateur> listerUtilisateurs() {
        return gestionnaireUtilisateurs.listerTous();
    }

    public boolean utilisateursVides() {
        return gestionnaireUtilisateurs.estVide();
    }
}