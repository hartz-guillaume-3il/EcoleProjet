package ui;

import User.Role;
import User.Utilisateur;
import facade.GestionCoursFacade;
import observer.CentreNotifications;
import observer.Observateur;
import observer.Notification;
import observer.ObservateurConsole;
import state.Creneau;
import strategy.affectation.AffectationParDisponibilite;
import strategy.paiement.PaiementPlusieursVersements;
import strategy.paiement.PaiementUneFois;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class JavaFXApp extends Application {

    private final GestionCoursFacade facade = new GestionCoursFacade();
    private final ObservableList<Utilisateur> modelUtilisateurs = FXCollections.observableArrayList();
    private final ObservableList<Creneau> modelCreneaux = FXCollections.observableArrayList();
    private final ObservableList<User.Inscription> modelInscriptions = FXCollections.observableArrayList();

    // Zone de notifications
    private final TextArea zoneLogs = new TextArea();

    @Override
    public void start(Stage stage) {
        facade.initFichierUtilisateurs("EcoleProjet/src/data/utilisateurs.txt");
        facade.initFichierInscriptions("EcoleProjet/src/data/inscriptions.txt");
        facade.initFichierSeance("EcoleProjet/src/data/seance.txt");

        if (!dialogueConnexion()) {
            stage.close();
            return;
        }   // ← d’abord login

        BorderPane root = new BorderPane();
        root.setCenter(creerTabs());
        root.setBottom(creerZoneLogs());

        CentreNotifications.getInstance().abonner(new ObservateurConsole("Journal"));
        CentreNotifications.getInstance().abonner(n ->
                zoneLogs.appendText("[" + n.getType() + "] " + n.getMessage() + " " + n.getDonnees() + "\n"));

        Scene scene = new Scene(root, 1100, 700);
        stage.setScene(scene);
        stage.setTitle("Gestion Cours de Soutien — JavaFX");
        stage.show();

        rafraichirTables();
    }

    private boolean dialogueConnexion() {
        Dialog<Boolean> dlg = new Dialog<>();
        dlg.setTitle("Connexion");
        ButtonType btOk = new ButtonType("Se connecter", ButtonBar.ButtonData.OK_DONE);
        ButtonType btCancel = ButtonType.CANCEL;
        dlg.getDialogPane().getButtonTypes().addAll(btOk, btCancel);

        ComboBox<String> champRole = new ComboBox<>(FXCollections.observableArrayList("PARENT", "GESTIONNAIRE"));
        champRole.getSelectionModel().selectFirst();
        TextField email = new TextField();
        email.setPromptText("email");
        PasswordField mdp = new PasswordField();
        mdp.setPromptText("mot de passe");

        Hyperlink linkCreer = new Hyperlink("Créer un compte…");

        // Si base non vide → seule connexion; la création ne propose que Parent
        boolean bootstrap = facade.utilisateursVides();
        if (!bootstrap) {
            // on laisse choisir le rôle pour la connexion mais la création sera Parent only
        }

        boolean autoriserGest = facade.peutCreerGestionnaireDepuisLogin();
        linkCreer.setOnAction(e -> dialogueCreationCompte(autoriserGest, email, mdp, champRole));

        GridPane gp = new GridPane();
        gp.setHgap(8);
        gp.setVgap(8);
        gp.setPadding(new Insets(10));
        gp.addRow(0, new Label("Rôle"), champRole);
        gp.addRow(1, new Label("Email"), email);
        gp.addRow(2, new Label("Mot de passe"), mdp);
        gp.add(linkCreer, 1, 3);
        dlg.getDialogPane().setContent(gp);

        dlg.setResultConverter(b -> {
            if (b == btOk) {
                try {
                    Role role = Role.valueOf(champRole.getValue());
                    var ok = facade.connecter(email.getText().trim(), mdp.getText().trim(), role).isPresent();
                    if (!ok) {
                        alerte("Connexion", "Identifiants invalides ou rôle incorrect.");
                        return false;
                    }
                    return true;
                } catch (Exception ex) {
                    alerte("Erreur", ex.getMessage());
                    return false;
                }
            }
            return false;
        });

        return dlg.showAndWait().orElse(false);
    }


    private TabPane creerTabs() {
        TabPane tabs = new TabPane();
        boolean isGest = facade.getSession().estGestionnaire();
        boolean isParent = facade.getSession().estParent();

        tabs.getTabs().add(tabCreneaux());
        if (isGest) tabs.getTabs().add(tabUtilisateurs());
        if (isParent) {
            tabs.getTabs().add(tabInscriptions());
            tabs.getTabs().add(tabPaiements());
        }

        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        return tabs;
    }

    private void dialogueCreationCompte(boolean autoriserGest,
                                        TextField emailLogin,
                                        PasswordField mdpLogin,
                                        ComboBox<String> roleLogin) {
        Dialog<Void> dlg = new Dialog<>();
        dlg.setTitle("Créer un compte");
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        ComboBox<String> champRole = new ComboBox<>(FXCollections.observableArrayList("PARENT", "GESTIONNAIRE"));
        if (autoriserGest) {
            champRole.getSelectionModel().select("GESTIONNAIRE");
        } else {
            champRole.getSelectionModel().select("PARENT");
            //champRole.setDisable(true);
        }
        TextField email = new TextField();
        email.setPromptText("email");
        PasswordField mdp = new PasswordField();
        mdp.setPromptText("mot de passe");
        TextField nom = new TextField();
        nom.setPromptText("nom");
        TextField prenom = new TextField();
        prenom.setPromptText("prénom (si parent)");
        GridPane gp = new GridPane();
        gp.setHgap(8);
        gp.setVgap(8);
        gp.setPadding(new Insets(10));
        gp.addRow(0, new Label("Rôle"), champRole);
        gp.addRow(1, new Label("Email"), email);
        gp.addRow(2, new Label("Mot de passe"), mdp);
        gp.addRow(3, new Label("Nom"), nom);
        gp.addRow(4, new Label("Prénom"), prenom);
        dlg.getDialogPane().setContent(gp);
        final Button btOk = (Button) dlg.getDialogPane().lookupButton(ButtonType.OK);
        btOk.addEventFilter(javafx.event.ActionEvent.ACTION, ev -> {
            try {
                Map<String, String> infos = new HashMap<>();
                infos.put("email", email.getText().trim());
                infos.put("motDePasse", mdp.getText().trim());
                infos.put("nom", nom.getText().trim());
                infos.put("prenom", prenom.getText().trim());
                String role = champRole.getValue();
                if ("GESTIONNAIRE".equals(role)) {
                    if (!autoriserGest)
                        throw new SecurityException("Création de gestionnaire non autorisée.");
                    facade.bootstrapGestionnaire(infos);
                } else {
                    facade.autoInscriptionParent(infos);
                }
                emailLogin.setText(infos.get("email"));
                mdpLogin.setText(infos.get("motDePasse"));
                roleLogin.getSelectionModel().select(role);

            } catch (Exception ex) {
                ev.consume();
                alerte("Erreur", ex.getMessage());
            }
        });
        dlg.showAndWait();
    }

    // --- Onglet Utilisateurs (création réservée au gestionnaire) ---
    private Tab tabUtilisateurs() {
        Tab tab = new Tab("Utilisateurs");

        // Formulaire
        ComboBox<String> champRole = new ComboBox<>(FXCollections.observableArrayList("PARENT", "GESTIONNAIRE"));
        champRole.getSelectionModel().selectFirst();
        TextField champEmail = new TextField();
        champEmail.setPromptText("email");
        PasswordField champMdp = new PasswordField();
        champMdp.setPromptText("mot de passe");
        TextField champNom = new TextField();
        champNom.setPromptText("nom");
        TextField champPrenom = new TextField();
        champPrenom.setPromptText("prénom (parents)");

        Button btnCreer = new Button("Créer");
        btnCreer.setOnAction(e -> {
            try {
                Role role = Role.valueOf(champRole.getValue());
                var infos = Map.of(
                        "email", champEmail.getText().trim(),
                        "motDePasse", champMdp.getText().trim(),
                        "nom", champNom.getText().trim(),
                        "prenom", champPrenom.getText().trim()
                );
                // réservé gestionnaire
                if (!facade.getSession().estGestionnaire()) {
                    alerte("Droits insuffisants", "Action réservée au gestionnaire.");
                    return;
                }
                facade.inscrireUtilisateur(role, infos);
                rafraichirUtilisateurs();
                zoneLogs.appendText("[INFO] Utilisateur créé\n");
            } catch (Exception ex) {
                alerte("Erreur création", ex.getMessage());
            }
        });

        // Désactiver si non gestionnaire
        btnCreer.setDisable(!facade.getSession().estGestionnaire());
        champRole.setDisable(!facade.getSession().estGestionnaire());
        champEmail.setDisable(!facade.getSession().estGestionnaire());
        champMdp.setDisable(!facade.getSession().estGestionnaire());
        champNom.setDisable(!facade.getSession().estGestionnaire());
        champPrenom.setDisable(!facade.getSession().estGestionnaire());

        HBox form = new HBox(10, new Label("Rôle:"), champRole, champEmail, champMdp, champNom, champPrenom, btnCreer);
        form.setPadding(new Insets(10));

        // Table
        TableView<Utilisateur> table = new TableView<>(modelUtilisateurs);
        TableColumn<Utilisateur, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(u -> new SimpleStringProperty(u.getValue().getEmail()));
        TableColumn<Utilisateur, String> colRole = new TableColumn<>("Rôle");
        colRole.setCellValueFactory(u -> new SimpleStringProperty(u.getValue().getRole().name()));
        table.getColumns().addAll(colEmail, colRole);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        VBox box = new VBox(form, table);
        tab.setContent(box);
        return tab;
    }

    private Tab tabInscriptions() {
        Tab tab = new Tab("Inscriptions");

        // Formulaire parent
        TextField champNomEnfant = new TextField();
        champNomEnfant.setPromptText("Nom de l’enfant");
        TextField champAge = new TextField();
        champAge.setPromptText("Âge");
        Button btnInscrire = new Button("Inscrire au créneau sélectionné");

        // Table créneaux pour choisir la cible
        TableView<Creneau> tableCreneaux = new TableView<>(modelCreneaux);
        TableColumn<Creneau, String> cCours = new TableColumn<>("Cours");
        cCours.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNomCours()));
        TableColumn<Creneau, String> cHoraire = new TableColumn<>("Horaire");
        cHoraire.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getHoraire().toString()));
        TableColumn<Creneau, String> cPlaces = new TableColumn<>("Places");
        cPlaces.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNbInscrits() + "/" + c.getValue().getCapaciteMax()));
        TableColumn<Creneau, String> cEtat = new TableColumn<>("État");
        cEtat.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEtat()));
        tableCreneaux.getColumns().addAll(cCours, cHoraire, cPlaces, cEtat);
        tableCreneaux.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tableCreneaux.setPrefHeight(220);

        // Table des inscriptions
        TableView<User.Inscription> tableIns = new TableView<>(modelInscriptions);
        TableColumn<User.Inscription, String> iNom = new TableColumn<>("Enfant");
        iNom.setCellValueFactory(i -> new SimpleStringProperty(i.getValue().getNomEnfant()));
        TableColumn<User.Inscription, String> iAge = new TableColumn<>("Âge");
        iAge.setCellValueFactory(i -> new SimpleStringProperty(String.valueOf(i.getValue().getAge())));
        TableColumn<User.Inscription, String> iCours = new TableColumn<>("Cours");
        iCours.setCellValueFactory(i -> new SimpleStringProperty(i.getValue().getCours()));
        TableColumn<User.Inscription, String> iHoraire = new TableColumn<>("Horaire");
        iHoraire.setCellValueFactory(i -> new SimpleStringProperty(i.getValue().getHoraireIso()));
        tableIns.getColumns().addAll(iNom, iAge, iCours, iHoraire);
        tableIns.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        btnInscrire.setOnAction(e -> {
            try {
                if (!facade.getSession().estParent()) {
                    alerte("Droits insuffisants", "Action réservée au parent.");
                    return;
                }
                Creneau cible = tableCreneaux.getSelectionModel().getSelectedItem();
                if (cible == null) {
                    alerte("Sélection", "Choisis un créneau.");
                    return;
                }
                int age = Integer.parseInt(champAge.getText().trim());
                var ins = facade.inscrireEnfantDans(cible, champNomEnfant.getText().trim(), age);
                zoneLogs.appendText("[OK] Inscription de " + ins.getNomEnfant() + " sur " + ins.getCours() + "\n");
                rafraichirCreneaux();
                rafraichirInscriptions();
            } catch (NumberFormatException ex) {
                alerte("Âge invalide", "Saisis un entier.");
            } catch (Exception ex) {
                alerte("Erreur", ex.getMessage());
            }
        });

        // Règle de droits
        boolean isParent = facade.getSession().estParent();
        champNomEnfant.setDisable(!isParent);
        champAge.setDisable(!isParent);
        btnInscrire.setDisable(!isParent);

        HBox form = new HBox(10, champNomEnfant, champAge, btnInscrire);
        form.setPadding(new Insets(10));
        VBox box = new VBox(new Label("Choisir un créneau"), tableCreneaux, form, new Label("Inscriptions"), tableIns);
        tab.setContent(box);
        return tab;
    }

    // --- Onglet Créneaux (gestion ouverte/fermée réservée au gestionnaire, affectation réservée au parent) ---
    private Tab tabCreneaux() {
        Tab tab = new Tab("Créneaux");

        TextField champNomCours = new TextField();
        champNomCours.setPromptText("Nom du cours");
        TextField champDateISO = new TextField();
        champDateISO.setPromptText("Date/heure ISO ex: 2025-10-10T14:30");
        TextField champCapacite = new TextField();
        champCapacite.setPromptText("Capacité");

        Button btnCreer = new Button("Créer créneau");
        btnCreer.setOnAction(e -> {
            try {
                if (!facade.getSession().estGestionnaire()) {
                    alerte("Droits insuffisants", "Action réservée au gestionnaire.");
                    return;
                }
                LocalDateTime dt = LocalDateTime.parse(champDateISO.getText().trim());
                int cap = Integer.parseInt(champCapacite.getText().trim());
                facade.creerCreneau(champNomCours.getText().trim(), dt, cap);
                rafraichirCreneaux();
                zoneLogs.appendText("[INFO] Créneau créé\n");
            } catch (DateTimeParseException ex) {
                alerte("Date invalide", "Utilise le format ISO: 2025-10-10T14:30");
            } catch (NumberFormatException ex) {
                alerte("Capacité invalide", "Saisis un entier.");
            }
        });

        TextField champNomEleve = new TextField();
        champNomEleve.setPromptText("Nom élève");
        TextField champAge = new TextField();
        champAge.setPromptText("Âge");
        Button btnAffecter = new Button("Affecter élève");
        btnAffecter.setOnAction(e -> {
            try {
                if (!facade.getSession().estParent()) {
                    alerte("Droits insuffisants", "Action réservée au parent.");
                    return;
                }
                int age = Integer.parseInt(champAge.getText().trim());
                var res = facade.affecterEleve(champNomEleve.getText().trim(), age);
                zoneLogs.appendText(res.isPresent()
                        ? "[OK] Affecté à " + res.get().getNomCours() + "\n"
                        : "[WARN] Aucun créneau disponible\n");
                rafraichirCreneaux();
            } catch (NumberFormatException ex) {
                alerte("Âge invalide", "Saisis un entier.");
            }
        });

        // Table
        TableView<Creneau> table = new TableView<>(modelCreneaux);
        TableColumn<Creneau, String> colCours = new TableColumn<>("Cours");
        colCours.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNomCours()));
        TableColumn<Creneau, String> colHoraire = new TableColumn<>("Horaire");
        colHoraire.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getHoraire().toString()));
        TableColumn<Creneau, String> colCap = new TableColumn<>("Capacité");
        colCap.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getCapaciteMax())));
        TableColumn<Creneau, String> colInscrits = new TableColumn<>("Inscrits");
        colInscrits.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getNbInscrits())));
        TableColumn<Creneau, String> colEtat = new TableColumn<>("État");
        colEtat.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEtat()));
        table.getColumns().addAll(colCours, colHoraire, colCap, colInscrits, colEtat);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        // Actions état (gestionnaire)
        Button btnFermer = new Button("Fermer inscriptions");
        btnFermer.setOnAction(e -> {
            Creneau sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) {
                alerte("Sélection", "Choisis un créneau.");
                return;
            }
            try {
                if (!facade.getSession().estGestionnaire()) {
                    alerte("Droits insuffisants", "Réservé au gestionnaire.");
                    return;
                }
                facade.fermerInscriptions(sel);
                rafraichirCreneaux();
                zoneLogs.appendText("[INFO] Inscriptions fermées\n");
            } catch (Exception ex) {
                alerte("Erreur", ex.getMessage());
            }
        });

        Button btnOuvrir = new Button("Ouvrir inscriptions");
        btnOuvrir.setOnAction(e -> {
            Creneau sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) {
                alerte("Sélection", "Choisis un créneau.");
                return;
            }
            try {
                if (!facade.getSession().estGestionnaire()) {
                    alerte("Droits insuffisants", "Réservé au gestionnaire.");
                    return;
                }
                facade.ouvrirInscriptions(sel);
                rafraichirCreneaux();
                zoneLogs.appendText("[INFO] Inscriptions ouvertes\n");
            } catch (Exception ex) {
                alerte("Erreur", ex.getMessage());
            }
        });

        // Désactivation selon rôles
        boolean isGest = facade.getSession().estGestionnaire();
        boolean isParent = facade.getSession().estParent();
        btnCreer.setDisable(!isGest);
        btnFermer.setDisable(!isGest);
        btnOuvrir.setDisable(!isGest);
        champNomCours.setDisable(!isGest);
        champDateISO.setDisable(!isGest);
        champCapacite.setDisable(!isGest);
        btnAffecter.setDisable(!isParent);
        champNomEleve.setDisable(!isParent);
        champAge.setDisable(!isParent);

        HBox formCreer = new HBox(10, champNomCours, champDateISO, champCapacite, btnCreer);
        HBox formAffect = new HBox(10, champNomEleve, champAge, btnAffecter);
        HBox actionsEtat = new HBox(10, btnFermer, btnOuvrir);
        formCreer.setPadding(new Insets(10));
        formAffect.setPadding(new Insets(10));
        actionsEtat.setPadding(new Insets(10));

        VBox cont = new VBox(formCreer, formAffect, actionsEtat, table);
        tab.setContent(cont);
        return tab;
    }

    // ----- Onglet Paiements -----
    private Tab tabPaiements() {
        Tab tab = new Tab("Paiements");

        ToggleGroup grp = new ToggleGroup();
        RadioButton rbUneFois = new RadioButton("Une fois");
        RadioButton rbPlusieurs = new RadioButton("Plusieurs versements");
        rbUneFois.setToggleGroup(grp);
        rbPlusieurs.setToggleGroup(grp);
        rbUneFois.setSelected(true);

        TextField champMontant = new TextField();
        champMontant.setPromptText("Montant €");
        TextField champNb = new TextField();
        champNb.setPromptText("Nb versements (2..6)");
        TextField champFrais = new TextField();
        champFrais.isDisabled();
        champFrais.setPromptText("Taux d'intérêt paiement en plusieurs fois");

        Button btnCalculer = new Button("Calculer le plan");
        TextArea sortie = new TextArea();
        sortie.setEditable(false);
        sortie.setPrefRowCount(10);

        btnCalculer.setOnAction(e -> {
            try {
                BigDecimal montant = new BigDecimal(champMontant.getText().trim());

                if (rbUneFois.isSelected()) {
                    // Paiement en une seule fois
                    facade.changerStrategiePaiement(new PaiementUneFois());
                } else {
                    int n = Integer.parseInt(champNb.getText().trim());
                    if (n < 2 || n > 6)
                        throw new IllegalArgumentException("Nb de versements entre 2 et 6");

                    // Calcul automatique : 5 % du montant total réparti sur chaque échéance
                    BigDecimal fraisTotal = montant.multiply(BigDecimal.valueOf(0.05));
                    BigDecimal taux = BigDecimal.valueOf(0.05);

                    // Afficher automatiquement la valeur dans le champ
                    champFrais.setText(taux.toPlainString());

                    // Utiliser la nouvelle version du calcul
                    facade.changerStrategiePaiement(new PaiementPlusieursVersements(n, taux));
                }

                var plan = facade.calculerPaiement(montant);

                StringBuilder sb = new StringBuilder();
                plan.echeances().forEach(ech -> sb.append(ech.date())
                        .append(" : ").append(ech.montant()).append("€\n"));
                sb.append("Total : ").append(plan.total()).append("€");

                sortie.setText(sb.toString());

            } catch (Exception ex) {
                alerte("Erreur de calcul", ex.getMessage());
            }
        });

        // Stratégie d’affectation par défaut
        facade.changerStrategieAffectation(new AffectationParDisponibilite());

        HBox ligne1 = new HBox(10, rbUneFois, rbPlusieurs, champNb, champFrais);
        HBox ligne2 = new HBox(10, champMontant, btnCalculer);
        VBox box = new VBox(10, ligne1, ligne2, sortie);
        box.setPadding(new Insets(10));
        tab.setContent(box);
        return tab;
    }

    private VBox creerZoneLogs() {
        zoneLogs.setEditable(false);
        zoneLogs.setPrefRowCount(6);
        VBox box = new VBox(new Label("Notifications"), zoneLogs);
        box.setPadding(new Insets(6));
        box.setStyle("-fx-background-color:#202225;");
        return box;
    }

    private void rafraichirTables() {
        rafraichirUtilisateurs();
        rafraichirCreneaux();
        rafraichirInscriptions();
    }

    private void rafraichirUtilisateurs() {
        modelUtilisateurs.setAll(facade.listerUtilisateurs());
    }

    private void rafraichirCreneaux() {
        modelCreneaux.setAll(facade.listerCreneaux());
    }

    private void alerte(String titre, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setTitle(titre);
        a.showAndWait();
    }

    private void rafraichirInscriptions() {
        modelInscriptions.setAll(facade.listerInscriptions());
    }
}
