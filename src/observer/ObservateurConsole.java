package observer;

public final class ObservateurConsole implements Observateur {
    private final String nom;

    public ObservateurConsole(String nom) {
        this.nom = nom;
    }

    @Override
    public void mettreAJour(Notification n) {
        System.out.println("[" + nom + "] " + n.getType() + " : " + n.getMessage() + " " + n.getDonnees());
    }
}
