package facade;

import User.*;
import factory.UtilisateurFactory;
import observer.*;
import security.Session;
import state.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import strategy.affectation.*;
import strategy.paiement.*;

public final class GestionCoursFacade {
    private final GestionnaireUtilisateurs gestionnaireUtilisateurs = GestionnaireUtilisateurs.getInstance();
    private final CentreNotifications notifications = CentreNotifications.getInstance();
    private final List<Creneau> creneaux = new ArrayList<>();
    private final Session session = new Session();
    private final List<Inscription> inscriptions = new ArrayList<>();

    private CalculPaiement strategiePaiement = new PaiementUneFois();
    private StrategieAffectation strategieAffectation = new AffectationParDisponibilite();

    public void initFichierUtilisateurs(String chemin) {
        gestionnaireUtilisateurs.initialiserFichier(chemin);
    }

    public Optional<Utilisateur> connecter(String email, String motDePasse, Role roleAttendu) {
        Optional<Utilisateur> opt = gestionnaireUtilisateurs.authentifier(email, motDePasse);
        if (opt.isPresent() && opt.get().getRole()==roleAttendu) {
            session.ouvrir(opt.get());
            return opt;
        }
        return Optional.empty();
    }

    public Inscription inscrireEnfantDans(Creneau c, String nomEnfant, int age) {
        if (!session.estParent()) throw new SecurityException("Réservé au parent");
        if (c == null) throw new IllegalArgumentException("Créneau manquant");
        c.reserver(); // gère plein/état via State + Observer
        Inscription ins = new Inscription(nomEnfant, age, c);
        inscriptions.add(ins);
        return ins;
    }

    public List<Inscription> listerInscriptions() { return List.copyOf(inscriptions); }
    public void deconnecter() { session.fermer(); }
    public Session getSession(){ return session; }

    public Utilisateur inscrireUtilisateur(Role role, Map<String, String> infos) {
        if (!session.estGestionnaire()) throw new SecurityException("Réservé au gestionnaire");
        Utilisateur u = UtilisateurFactory.creerUtilisateur(role, infos);
        gestionnaireUtilisateurs.enregistrer(u);
        return u;
    }

    public Creneau creerCreneau(String nomCours, LocalDateTime horaire, int capacite) {
        if (!session.estGestionnaire()) throw new SecurityException("Réservé au gestionnaire");
        Creneau c = new Creneau(nomCours, horaire, capacite);
        creneaux.add(c);
        return c;
    }

    public void fermerInscriptions(Creneau c) {
        if (!session.estGestionnaire()) throw new SecurityException("Réservé au gestionnaire");
        c.changerEtat(new EtatFerme());
    }

    public void ouvrirInscriptions(Creneau c) {
        if (!session.estGestionnaire()) throw new SecurityException("Réservé au gestionnaire");
        c.changerEtat(new EtatDisponible());
    }

    public Optional<Creneau> affecterEleve(String nomEleve, int age) {
        if (!session.estParent()) throw new SecurityException("Réservé au parent");
        Candidat c = new Candidat(nomEleve, age, System.nanoTime());
        Creneau choix = strategieAffectation.choisir(c, creneaux);
        if (choix != null) {
            choix.reserver();
            return Optional.of(choix);
        }
        notifications.notifierTous(new Notification(TypeEvenement.CRENEAU_ANNULE,"Aucun créneau disponible", Map.of("eleve", nomEleve)));
        return Optional.empty();
    }

    public PlanPaiement calculerPaiement(BigDecimal montant) { return strategiePaiement.calculer(montant); }
    public void changerStrategiePaiement(CalculPaiement s) { this.strategiePaiement = s; }
    public void changerStrategieAffectation(StrategieAffectation s) { this.strategieAffectation = s; }

    public List<Creneau> listerCreneaux() { return List.copyOf(creneaux); }
    public List<Utilisateur> listerUtilisateurs() { return gestionnaireUtilisateurs.listerTous(); }
    public boolean utilisateursVides() { return gestionnaireUtilisateurs.estVide(); }

    public Utilisateur autoInscriptionParent(Map<String,String> infos) {
        Utilisateur u = UtilisateurFactory.creerUtilisateur(Role.PARENT, infos);
        gestionnaireUtilisateurs.enregistrer(u);
        return u;
    }
    public Utilisateur bootstrapGestionnaire(Map<String,String> infos) {
        if (!utilisateursVides()) throw new SecurityException("Déjà initialisé");
        Utilisateur u = UtilisateurFactory.creerUtilisateur(Role.GESTIONNAIRE, infos);
        gestionnaireUtilisateurs.enregistrer(u);
        return u;
    }

}
