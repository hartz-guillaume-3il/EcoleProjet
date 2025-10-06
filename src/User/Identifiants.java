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

    String getEmail() { return email; }

    boolean correspond(String motDePasseClair) {
        return motDePasseHache.equals(hacher(motDePasseClair));
    }

    private static String hacher(String texte) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(texte.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 non disponible", e);
        }
    }
}
