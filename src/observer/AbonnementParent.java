package observer;

import User.Parent;

public final class AbonnementParent implements Observateur {
    private final Parent parent;

    public AbonnementParent(Parent parent) {
        this.parent = parent;
    }

    @Override
    public void mettreAJour(Notification n) {
        System.out.println("[Parent " + parent.getNom() + "] " + n.getType() + " : " + n.getMessage());
    }
}
