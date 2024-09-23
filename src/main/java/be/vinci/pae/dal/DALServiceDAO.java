package be.vinci.pae.dal;

import be.vinci.pae.utils.exceptions.FatalException;
import be.vinci.pae.utils.exceptions.FatalExceptionType;
import java.sql.PreparedStatement;

/**
 * L'interface DALService définit les opérations liées aux services d'accès aux données.
 */
public interface DALServiceDAO {

  /**
   * Obtient un objet PreparedStatement pour la requête SQL fournie.
   *
   * @param sql la requête SQL.
   * @return un objet PreparedStatement prêt à être utilisé.
   * @throws FatalException <ul>
   *                        <li>{@link FatalExceptionType#DATABASE}
   *                        - si une erreur de DB se produit.</li>
   *                        <li>{@link FatalExceptionType#TRANSACTION}
   *                        - s'il n'y a pas de connexion disponible.</li>
   *                        </ul>
   */
  PreparedStatement getPs(String sql);

}
