package be.vinci.pae.utils.exceptions;

/**
 * L'énumération ExceptionStatus représente les différents statuts associés aux exceptions dans
 * l'application. Ces statuts sont utilisés pour indiquer la nature de l'erreur rencontrée.
 */
public enum BusinessExceptionStatus {

  /**
   * Indique une erreur de requête.
   */
  REQUEST_ERROR,

  /**
   * Indique une non-autorisation d'accès.
   */
  UNAUTHORIZED,

  /**
   * Indique un accès refusé.
   */
  ACCESS_DENIED,

  /**
   * Indique que la ressource demandée n'a pas été trouvée.
   */
  NOT_FOUND,

  /**
   * Indique que la ressource demandée existe déjà.
   */
  ALREADY_EXISTS
}
