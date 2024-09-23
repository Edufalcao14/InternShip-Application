package be.vinci.pae.utils.exceptions;

/**
 * La classe BusinessException représente une exception qui se produit dans la logique métier de
 * l'application. Elle étend la classe RuntimeException.
 */
public class BusinessException extends RuntimeException {

  /**
   * Le statut de l'exception. - {@link BusinessExceptionStatus}
   */
  BusinessExceptionStatus status;

  /**
   * Le message d'erreur associé à l'exception.
   */
  String errorMessage;

  /**
   * Construit une nouvelle BusinessException avec le message d'erreur et la cause qui provoqué
   * l'exception.
   *
   * @param message   Le message.
   * @param exception L'exception qui a provoqué la BusinessException.
   */
  public BusinessException(String message, Throwable exception) {
    super(message, exception);
  }

  /**
   * Construit une nouvelle BusinessException avec le message d'erreur et le statut de l'exception.
   *
   * @param message Le message d'erreur associé à la BusinessException.
   * @param status  Le statut de la BusinessException.
   */
  public BusinessException(String message,
      BusinessExceptionStatus status) {
    super();
    this.status = status;
    this.errorMessage = message;
  }

  /**
   * Récupère le statut de la BusinessException.
   *
   * @return Le statut de la BusinessException.
   */
  public BusinessExceptionStatus getStatus() {
    return status;
  }

  /**
   * Récupère le message d'erreur associé à la BusinessException.
   *
   * @return Le message d'erreur associé à la BusinessException.
   */
  public String getErrorMessage() {
    return errorMessage;
  }
}
