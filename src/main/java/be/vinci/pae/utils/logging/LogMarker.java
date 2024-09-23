package be.vinci.pae.utils.logging;

/**
 * Énumération représentant les marqueurs de journalisation pour les différents domaines de
 * l'application.
 */
public enum LogMarker {

  /**
   * Marqueur pour les journaux liés au serveur.
   */
  SERVER,

  /**
   * Marqueur pour les journaux liés à la base de données.
   */
  DATABASE,

  /**
   * Marqueur pour les journaux liés à la couche web de l'application.
   */
  WEB,

  /**
   * Marqueur pour les journaux liés aux opérations métier de l'application.
   */
  BUSINESS,

  /**
   * Marqueur pour les journaux liés aux transactions.
   */
  TRANSACTION,

  /**
   * Marqueur pour les journaux liés à l'Optimistic Lock.
   */
  OPTIMISTIC_LOCK
}
