package be.vinci.pae.ihm.filters;

import be.vinci.pae.business.domain.user.Role;
import be.vinci.pae.business.dto.UserDTO;
import be.vinci.pae.business.ucc.user.UserUCC;
import be.vinci.pae.utils.Config;
import be.vinci.pae.utils.logging.LogMarker;
import be.vinci.pae.utils.logging.MyLogger;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.Provider;
import java.lang.reflect.Method;

/**
 * Filtre JAX-RS qui gère l'autorisation des requêtes en vérifiant la validité du token JWT.
 */
@Singleton
@Provider
@Authorize({Role.ADMINISTRATIF, Role.ETUDIANT, Role.PROFESSEUR})
public class AuthorizationRequestFilter implements ContainerRequestFilter {

  private final Algorithm jwtAlgorithm = Algorithm.HMAC256(Config.getProperty("JWTSecret"));
  private final JWTVerifier jwtVerifier = JWT.require(this.jwtAlgorithm).withIssuer("auth0")
      .build();

  @Context
  ResourceInfo resourceInfo;
  @Inject
  private UserUCC myUserUCC;

  /**
   * Filtre les requêtes entrantes en vérifiant la validité du token JWT dans l'en-tête
   * "Authorization".
   *
   * @param requestContext le contexte de la requête
   */
  @Override
  public void filter(ContainerRequestContext requestContext) {
    String token = requestContext.getHeaderString("Authorization");
    if (token == null) {
      MyLogger.logInfo(LogMarker.WEB,
          "UNAUTHORIZED : Un token est nécessaire pour accéder à cette page.");
      requestContext.abortWith(Response.status(Status.UNAUTHORIZED)
          .entity("Un token est nécessaire pour accéder à cette page.").build());
    } else {
      DecodedJWT decodedToken;
      try {
        decodedToken = this.jwtVerifier.verify(token);
      } catch (Exception e) {
        MyLogger.logInfo(LogMarker.WEB, "UNAUTHORIZED : Token invalide.");
        throw new WebApplicationException(
            Response.status(Status.UNAUTHORIZED).entity("Échec de la vérification du token.")
                .build());
      }
      UserDTO authenticatedUser = myUserUCC.getUserById(decodedToken.getClaim("userID").asInt());
      if (authenticatedUser == null) {
        MyLogger.logInfo(LogMarker.WEB, "FORBIDDEN : Accès interdit.");
        requestContext.abortWith(Response.status(Status.FORBIDDEN)
            .entity("Accès interdit. Veuillez contacter l'administrateur.").build());
        return;
      }

      Method resourceMethod = resourceInfo.getResourceMethod();
      Authorize[] annotations = resourceMethod.getAnnotationsByType(Authorize.class);

      boolean authorized = false;
      for (Authorize annotation : annotations) {
        for (Role role : annotation.value()) {
          if (role == authenticatedUser.getRole()) {
            authorized = true;
            break;
          }
        }
        if (authorized) {
          break;
        }
      }
      if (!authorized) {
        MyLogger.logInfo(LogMarker.WEB, "FORBIDDEN : Accès interdit.");
        requestContext.abortWith(Response.status(Status.FORBIDDEN)
            .entity("Accès interdit. Vous n'avez pas les droits.").build());
        return;
      }

      requestContext.setProperty("user", authenticatedUser);
    }
  }
}