package be.vinci.pae.utils;

import be.vinci.pae.utils.exceptions.BusinessException;
import be.vinci.pae.utils.exceptions.FatalException;
import be.vinci.pae.utils.exceptions.OptimisticLockException;
import be.vinci.pae.utils.logging.LogMarker;
import be.vinci.pae.utils.logging.MyLogger;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.Provider;

/**
 * Cette classe est un gestionnaire d'exceptions pour les exceptions lancées dans le contexte.
 */
@Provider
public class ExceptionMapper implements jakarta.ws.rs.ext.ExceptionMapper<Throwable> {

  /**
   * Convertit une exception en une réponse HTTP appropriée.
   *
   * @param exception L'exception a mappé en réponse HTTP.
   * @return Une réponse HTTP appropriée en fonction de l'exception fournie.
   */
  @Override
  public Response toResponse(Throwable exception) {
    if (exception instanceof IllegalStateException && exception.getMessage().equals("Forbidden")) {
      MyLogger.logWarn("Tentative d'accès interdit détectée");
      return Response.status(Response.Status.FORBIDDEN)
          .entity("Accès interdit. Veuillez contacter l'administrateur.").build();
    }

    if (exception instanceof OptimisticLockException) {
      MyLogger.logInfo(LogMarker.OPTIMISTIC_LOCK, "Erreur de version.");
      return Response.status(Response.Status.CONFLICT)
          .entity("Conflit lors de la modification. Veuillez réessayer.").build();
    }

    if (exception instanceof BusinessException businessException) {
      MyLogger.logInfo(LogMarker.BUSINESS,
          businessException.getStatus().toString() + " : " + businessException.getErrorMessage());
      switch (businessException.getStatus()) {
        case REQUEST_ERROR:
          return Response.status(Status.BAD_REQUEST).entity(businessException.getErrorMessage())
              .build();
        case UNAUTHORIZED:
          return Response.status(Status.UNAUTHORIZED).entity(businessException.getErrorMessage())
              .build();
        case ACCESS_DENIED:
          return Response.status(Status.FORBIDDEN).entity(businessException.getErrorMessage())
              .build();
        case NOT_FOUND:
          return Response.status(Status.NOT_FOUND).entity(businessException.getErrorMessage())
              .build();
        case ALREADY_EXISTS:
          return Response.status(Status.CONFLICT).entity(businessException.getErrorMessage())
              .build();
        default:
          MyLogger.logInfo(businessException.getStatus().toString());
      }
    }

    LogMarker marker = LogMarker.SERVER;
    if (exception instanceof FatalException) {
      marker = LogMarker.valueOf(((FatalException) exception).getType().toString());
    }
    MyLogger.logError(marker, exception.getMessage(), exception);
    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
        .entity("Erreur du serveur. Veuillez réessayer plus tard et/ou contacter l'administrateur.")
        .build();
  }
}
