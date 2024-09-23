package be.vinci.pae.dal;

import be.vinci.pae.utils.exceptions.FatalException;
import be.vinci.pae.utils.exceptions.FatalExceptionType;

/**
 * Cette interface définit les méthodes nécessaires pour gérer les transactions au niveau de la
 * couche DAL depuis la couche UCC.
 */
public interface DALServiceUCC {

  /**
   * Initie une connexion et démarre une transaction.
   *
   * @throws FatalException {@link FatalExceptionType#DATABASE} - si une erreur de DB se produit.
   */
  void start();

  /**
   * Valide les modifications apportées lors de la transaction et ferme la connexion.
   *
   * @throws FatalException {@link FatalExceptionType#DATABASE} - si une erreur de DB se produit.
   */
  void commit();

  /**
   * Annule les modifications apportées lors de la transaction et ferme la connexion.
   *
   * @throws FatalException <ul>
   *                        <li>{@link FatalExceptionType#DATABASE}
   *                        - si une erreur de DB se produit.</li>
   *                        <li>{@link FatalExceptionType#TRANSACTION}
   *                        - si il n'y a pas de transaction en cours.</li>
   *                        </ul>
   */
  void rollBack();
}
