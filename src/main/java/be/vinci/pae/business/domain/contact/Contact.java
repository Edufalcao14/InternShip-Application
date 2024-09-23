package be.vinci.pae.business.domain.contact;

import be.vinci.pae.business.dto.ContactDTO;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * L'interface Contact représente un contact dans le domaine métier.
 */
@JsonDeserialize(as = ContactImpl.class)
public interface Contact extends ContactDTO {

  /**
   * Vérifie si l'état du contact actuel est propice au changement d'état.
   *
   * @param state le nouvel état.
   * @return true si l'état est propice au changement, false sinon.
   */
  boolean checkPreviousState(State state);

  /**
   * Vérifie si le lieu de rencontre est conforme.
   *
   * @param place le lieu à vérifier.
   * @return true si le lieu est conforme, false sinon.
   */
  boolean checkPlaceValidity(String place);
}
