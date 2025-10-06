// src/main/java/factory/UtilisateurFactory.java
package factory;

import User.*;

import java.util.Map;

public final class UtilisateurFactory {

    // Constructeur privé pour empêcher l’instanciation
    private UtilisateurFactory() {}

    /**
     * Crée un nouvel utilisateur à partir de son rôle et des informations fournies.
     *
     * @param role Rôle de l’utilisateur (PARENT ou GESTIONNAIRE)
     * @param infos Clés attendues :
     *              - "email"
     *              - "motDePasse"
     *              - "nom"
     *              - "prenom" (pour Parent uniquement)
     * @return une instance de Utilisateur (Parent ou Gestionnaire)
     */
    public static Utilisateur creerUtilisateur(Role role, Map<String, String> infos) {
        String email = infos.get("email");
        String motDePasse = infos.get("motDePasse");

        return switch (role) {
            case PARENT -> new Parent(
                    email,
                    motDePasse,
                    infos.getOrDefault("nom", ""),
                    infos.getOrDefault("prenom", "")
            );

            case GESTIONNAIRE -> new Gestionnaire(
                    email,
                    motDePasse,
                    infos.getOrDefault("nom", "")
            );
        };
    }
}
