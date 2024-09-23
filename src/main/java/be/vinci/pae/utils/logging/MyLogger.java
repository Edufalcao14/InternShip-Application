package be.vinci.pae.utils.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.MarkerManager;

/**
 * Classe utilitaire pour la journalisation des messages à l'aide de Log4j.
 */
public class MyLogger {

  private static final Logger LOGGER = LogManager.getLogger(MyLogger.class);

  /**
   * Enregistre un message d'erreur avec un marqueur spécifique.
   *
   * @param marker  Le marqueur associé au message.
   * @param message Le message d'erreur à enregistrer.
   */
  public static void logError(LogMarker marker, String message) {
    LOGGER.error(MarkerManager.getMarker(marker.toString()), message);
  }

  /**
   * Enregistre un message d'erreur avec un marqueur spécifique et une exception associée.
   *
   * @param marker    Le marqueur associé au message.
   * @param message   Le message d'erreur à enregistrer.
   * @param throwable L'exception associée à l'erreur.
   */
  public static void logError(LogMarker marker, String message, Throwable throwable) {
    LOGGER.error(MarkerManager.getMarker(marker.toString()), message, throwable);
  }

  /**
   * Enregistre un message d'information.
   *
   * @param message Le message d'information à enregistrer.
   */
  public static void logInfo(String message) {
    LOGGER.info(message);
  }

  /**
   * Enregistre un message d'information avec un marqueur spécifique.
   *
   * @param marker  Le marqueur associé au message.
   * @param message Le message d'information à enregistrer.
   */
  public static void logInfo(LogMarker marker, String message) {
    LOGGER.info(MarkerManager.getMarker(marker.toString()), message);
  }

  /**
   * Enregistre un message d'avertissement.
   *
   * @param message Le message d'avertissement à enregistrer.
   */
  public static void logWarn(String message) {
    LOGGER.warn(message);
  }

  /**
   * Enregistre un message d'avertissement avec un marqueur spécifique et une exception associée.
   *
   * @param marker    Le marqueur associé au message.
   * @param message   Le message d'avertissement à enregistrer.
   * @param throwable L'exception associée au message d'avertissement.
   */
  public static void logWarn(LogMarker marker, String message, Throwable throwable) {
    LOGGER.warn(MarkerManager.getMarker(marker.toString()), message, throwable);
  }
}
