package User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/** Gère la sécurité des identifiants (email + mot de passe haché). */
final class Identifiants {
    private final String email;
    private final String motDePasseHache;

    Identifiants(String email, String motDePasseClair) {
        this.email = Objects.requireNonNull(email, "email");
        this.motDePasseHache = hacher(Objects.requireNonNull(motDePasseClair, "motDePasse"));
    }

    static Identifiants depuisHash(String email, String hashDejaCalcule) {
        Objects.requireNonNull(email); Objects.requireNonNull(hashDejaCalcule);
        return new Identifiants(email, hashDejaCalcule, true);
    }

    private Identifiants(String email, String h, boolean dejaHache) {
        this.email = email;
        this.motDePasseHache = h;
    }

    String getEmail() { return email; }
    String getHash() { return motDePasseHache; }               // ← nouveau

    boolean correspond(String motDePasseClair) {
        return motDePasseHache.equals(hacher(motDePasseClair));
    }

    private static String hacher(String texte) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(texte.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 non disponible", e);
        }
    }
}
