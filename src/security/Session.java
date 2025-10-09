package security;

import User.Utilisateur;
import User.Role;

public final class Session {
    private Utilisateur courant;

    public void ouvrir(Utilisateur u) {
        this.courant = u;
    }

    public void fermer() {
        this.courant = null;
    }

    public boolean estOuverte() {
        return courant != null;
    }

    public Utilisateur utilisateur() {
        return courant;
    }

    public boolean estGestionnaire() {
        return courant != null && courant.getRole() == Role.GESTIONNAIRE;
    }

    public boolean estParent() {
        return courant != null && courant.getRole() == Role.PARENT;
    }
}