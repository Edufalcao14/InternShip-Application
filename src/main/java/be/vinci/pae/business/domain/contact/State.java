package be.vinci.pae.business.domain.contact;

/**
 * L'énumération State représente les différents états d'un contact.
 */
public enum State {
  /**
   * État Initié.
   */
  INITIE,
  /**
   * État Pris.
   */
  PRIS,
  /**
   * État Accepté.
   */
  ACCEPTE,
  /**
   * État Suspendu.
   */
  SUSPENDU,
  /**
   * État Refusé.
   */
  REFUSE,
  /**
   * État Blacklisté.
   */
  BLACKLISTE,
  /**
   * État Non Suivi.
   */
  NON_SUIVI
}
