package factory;

import User.*;
import java.util.Map;

/**
 * Patron de conception Factory :
 * centralise la création des différents types d'utilisateurs.
 */
public final class UserFactory {

    private UserFactory() {} // Empêche l’instanciation

    /**
     * Crée un utilisateur en fonction de son rôle.
     * @param role Rôle de l'utilisateur (PARENT ou GESTIONNAIRE)
     * @param informations Données nécessaires à la création (email, motDePasse, nom, prénom)
     * @return un objet Utilisateur correspondant
     */
    public static Utilisateur creerUtilisateur(Role role, Map<String, String> informations) {
        String email = informations.get("email");
        String motDePasse = informations.get("motDePasse");

        return switch (role) {
            case PARENT -> new Parent(
                    email,
                    motDePasse,
                    informations.getOrDefault("nom", ""),
                    informations.getOrDefault("prenom", "")
            );

            case GESTIONNAIRE -> new Gestionnaire(
                    email,
                    motDePasse,
                    informations.getOrDefault("nom", "")
            );
        };
    }
}
