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
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;

public class JavaFXApp extends Application {

    private final GestionCoursFacade facade = new GestionCoursFacade();

    // Modèles pour les tables
    private final ObservableList<Utilisateur> modelUtilisateurs = FXCollections.observableArrayList();
    private final ObservableList<Creneau> modelCreneaux = FXCollections.observableArrayList();

    // Zone de notifications
    private final TextArea zoneLogs = new TextArea();

    @Override
    public void start(Stage stage) {
        stage.setTitle("Gestion Cours de Soutien — JavaFX");

        // Observer → append dans la zoneLogs
        Observateur uiLogger = (Notification n) ->
                zoneLogs.appendText("[" + n.getType() + "] " + n.getMessage() + " " + n.getDonnees() + "\n");
        CentreNotifications.getInstance().abonner(new ObservateurConsole("Journal"));
        CentreNotifications.getInstance().abonner(uiLogger);

        BorderPane root = new BorderPane();
        root.setCenter(creerTabs());
        root.setBottom(creerZoneLogs());

        Scene scene = new Scene(root, 1100, 700);
        stage.setScene(scene);
        stage.show();

        // Refresh init
        rafraichirTables();
    }

    private TabPane creerTabs() {
        TabPane tabs = new TabPane();
        tabs.getTabs().add(tabUtilisateurs());
        tabs.getTabs().add(tabCreneaux());
        tabs.getTabs().add(tabPaiements());
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        return tabs;
    }

    // ----- Onglet Utilisateurs -----
    private Tab tabUtilisateurs() {
        Tab tab = new Tab("Utilisateurs");

        // Formulaire
        ComboBox<String> champRole = new ComboBox<>(FXCollections.observableArrayList("PARENT", "GESTIONNAIRE"));
        champRole.getSelectionModel().selectFirst();
        TextField champEmail = new TextField(); champEmail.setPromptText("email");
        PasswordField champMdp = new PasswordField(); champMdp.setPromptText("mot de passe");
        TextField champNom = new TextField(); champNom.setPromptText("nom");
        TextField champPrenom = new TextField(); champPrenom.setPromptText("prénom (parents)");

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
                facade.inscrireUtilisateur(role, infos);
                rafraichirUtilisateurs();
                zoneLogs.appendText("[INFO] Utilisateur créé\n");
            } catch (Exception ex) {
                alerte("Erreur création", ex.getMessage());
            }
        });

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

    // ----- Onglet Créneaux -----
    private Tab tabCreneaux() {
        Tab tab = new Tab("Créneaux");

        TextField champNomCours = new TextField(); champNomCours.setPromptText("Nom du cours");
        TextField champDateISO = new TextField(); champDateISO.setPromptText("Date/heure ISO ex: 2025-10-10T14:30");
        TextField champCapacite = new TextField(); champCapacite.setPromptText("Capacité");

        Button btnCreer = new Button("Créer créneau");
        btnCreer.setOnAction(e -> {
            try {
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

        TextField champNomEleve = new TextField(); champNomEleve.setPromptText("Nom élève");
        TextField champAge = new TextField(); champAge.setPromptText("Âge");
        Button btnAffecter = new Button("Affecter élève");
        btnAffecter.setOnAction(e -> {
            try {
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

        HBox formCreer = new HBox(10, champNomCours, champDateISO, champCapacite, btnCreer);
        HBox formAffect = new HBox(10, champNomEleve, champAge, btnAffecter);
        formCreer.setPadding(new Insets(10));
        formAffect.setPadding(new Insets(10));

        // Table des créneaux
        TableView<Creneau> table = new TableView<>(modelCreneaux);
        TableColumn<Creneau, String> colCours = new TableColumn<>("Cours");
        colCours.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNomCours()));
        TableColumn<Creneau, String> colHoraire = new TableColumn<>("Horaire");
        colHoraire.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getHoraire().toString()));
        TableColumn<Creneau, String> colCap = new TableColumn<>("Capacité");
        colCap.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getCapaciteMax())));
        TableColumn<Creneau, String> colInscrits = new TableColumn<>("Inscrits");
        colInscrits.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getInscrits())));
        TableColumn<Creneau, String> colEtat = new TableColumn<>("État");
        colEtat.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEtat().getNomEtat()));
        table.getColumns().addAll(colCours, colHoraire, colCap, colInscrits, colEtat);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        VBox cont = new VBox(formCreer, formAffect, table);
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

        TextField champMontant = new TextField(); champMontant.setPromptText("Montant €");
        TextField champNb = new TextField(); champNb.setPromptText("Nb versements (2..6)");
        TextField champFrais = new TextField(); champFrais.setPromptText("Frais par échéance €");

        Button btnCalculer = new Button("Calculer plan");
        TextArea sortie = new TextArea(); sortie.setEditable(false); sortie.setPrefRowCount(10);

        btnCalculer.setOnAction(e -> {
            try {
                BigDecimal montant = new BigDecimal(champMontant.getText().trim());
                if (rbUneFois.isSelected()) {
                    // Stratégie une fois
                    facade.changerStrategiePaiement(new PaiementUneFois());
                } else {
                    int n = Integer.parseInt(champNb.getText().trim());
                    BigDecimal frais = new BigDecimal(champFrais.getText().trim());
                    facade.changerStrategiePaiement(new PaiementPlusieursVersements(n, frais));
                }
                var plan = facade.calculerPaiement(montant);
                StringBuilder sb = new StringBuilder();
                plan.echeances().forEach(ech -> sb.append(ech.date()).append(" : ").append(ech.montant()).append("€\n"));
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
}
