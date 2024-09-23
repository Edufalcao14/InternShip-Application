package be.vinci.pae.business.domain.company;

import be.vinci.pae.business.dto.CompanyDTO;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * L'interface Company représente une entreprise dans le domaine métier.
 */
@JsonDeserialize(as = CompanyImpl.class)
public interface Company extends CompanyDTO {

  /**
   * Vérifie si l'email est conforme.
   *
   * @param email l'email à vérifier.
   * @return true si l'email est conforme, false sinon.
   */
  boolean checkEmailValidity(String email);

  /**
   * Vérifie si le numéro de téléphone est conforme.
   *
   * @param phoneNumber le numéro de téléphone à vérifier.
   * @return true si le numéro de téléphone est conforme, false sinon.
   */
  boolean checkPhoneNumberValidity(String phoneNumber);
}
