package be.vinci.pae.ihm.filters;

import be.vinci.pae.business.domain.user.Role;
import jakarta.ws.rs.NameBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation utilisée pour lier un filtre d'autorisation à une ressource ou à une méthode JAX-RS.
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
public @interface Authorize {

  /**
   * Les rôles autorisés.
   *
   * @return le(s) rôle(s) autorisé(s).
   */
  Role[] value();
}
