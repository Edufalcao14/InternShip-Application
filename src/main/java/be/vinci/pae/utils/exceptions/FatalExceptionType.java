package be.vinci.pae.utils.exceptions;

/**
 * Représente les différents types d'erreurs fatales pouvant être levées.
 */
public enum FatalExceptionType {

  /**
   * Indique une erreur liée à la base de données.
   */
  DATABASE,

  /**
   * Indique une erreur liée à une transaction.
   */
  TRANSACTION,

  /**
   * Indique une erreur liée au serveur.
   */
  SERVER
}
