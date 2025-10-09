package observer;

import User.Parent;

/**
 * Adaptateur : un Parent devient Observateur.
 */
public final class AbonnementParent implements Observateur {
    private final Parent parent;

    public AbonnementParent(Parent parent) {
        this.parent = parent;
    }

    @Override
    public void mettreAJour(Notification n) {
        // Ici, brancher un service d’email/SMS plus tard.
        System.out.println("[Parent " + parent.getNom() + "] " + n.getType() + " : " + n.getMessage());
    }
}
