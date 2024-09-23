package be.vinci.pae.ihm.filters;

import be.vinci.pae.utils.logging.LogMarker;
import be.vinci.pae.utils.logging.MyLogger;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

/**
 * Filtre de requête pour la journalisation des détails des requêtes entrantes.
 */
@Provider
public class LoggingFilter implements ContainerRequestFilter {

  /**
   * Intercepte chaque requête entrante et journalise les détails pertinents tels que le chemin
   * (path) et l'opération (GET, POST, etc.) avant de la transmettre à la ressource web.
   *
   * @param requestContext Le contexte de la requête entrante.
   */
  @Override
  public void filter(ContainerRequestContext requestContext) {
    String path = requestContext.getUriInfo().getPath();
    String method = requestContext.getMethod();
    MyLogger.logInfo(LogMarker.WEB, method + " " + path);
  }
}
