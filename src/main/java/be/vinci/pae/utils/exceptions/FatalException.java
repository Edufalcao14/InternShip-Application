package be.vinci.pae.utils.exceptions;

/**
 * La classe FatalException représente une exception grave qui peut conduire à un arrêt brutal de
 * l'application. Elle étend la classe RuntimeException.
 */
public class FatalException extends RuntimeException {

  /**
   * Type de l'exception fatale. - {@link FatalExceptionType}
   */
  private FatalExceptionType type;

  /**
   * Construit une nouvelle FatalException sans message spécifique.
   */
  public FatalException() {
  }

  /**
   * Construit une nouvelle FatalException avec un message spécifique.
   *
   * @param message Le message.
   * @param type    Le type de la FatalException.
   */
  public FatalException(String message, FatalExceptionType type) {
    super(message);
    this.type = type;
  }

  /**
   * Construit une nouvelle FatalException avec la cause spécifiée.
   *
   * @param exception La cause de la FatalException.
   * @param type      Le type de la FatalException.
   */
  public FatalException(Throwable exception, FatalExceptionType type) {
    super(exception);
    this.type = type;
  }

  /**
   * Construit une nouvelle FatalException avec le message détaillé et la cause spécifiés.
   *
   * @param message   Le message détaillé.
   * @param exception La cause de la FatalException.
   * @param type      Le type de la FatalException.
   */
  public FatalException(String message, Throwable exception, FatalExceptionType type) {
    super(message, exception);
    this.type = type;
  }

  /**
   * Renvoie le type de l'exception fatale.
   *
   * @return Le type de la FatalException {@link FatalExceptionType}
   */
  public FatalExceptionType getType() {
    return type;
  }
}
