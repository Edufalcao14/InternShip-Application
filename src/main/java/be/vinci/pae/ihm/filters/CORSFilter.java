package be.vinci.pae.ihm.filters;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;

/**
 * Ce filtre permet de gérer les en-têtes CORS (Cross-Origin Resource Sharing) pour autoriser les
 * requêtes provenant d'un domaine spécifique.
 */
public class CORSFilter implements ContainerResponseFilter {

  /**
   * Filtre les réponses sortantes pour ajouter les en-têtes CORS nécessaires.
   *
   * @param requestContext  Le contexte de la requête entrante.
   * @param responseContext Le contexte de la réponse sortante.
   */
  @Override
  public void filter(ContainerRequestContext requestContext,
      ContainerResponseContext responseContext) {

    responseContext.getHeaders().add("Access-Control-Allow-Origin", "http://localhost:8080");
    responseContext.getHeaders().add("Access-Control-Allow-Headers",
        "origin, content-type, accept, authorization");
    responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
    responseContext.getHeaders().add("Access-Control-Allow-Methods",
        "GET, POST, PUT, DELETE, OPTIONS, HEAD, PATCH");
    responseContext.getHeaders().add("Access-Control-Max-Age", "1209600");
  }
}
