package be.vinci.pae.business.domain.supervisor;

import be.vinci.pae.business.dto.SupervisorDTO;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * L'interface Supervisor représente un responsable de stage dans le domaine métier.
 */
@JsonDeserialize(as = SupervisorImpl.class)
public interface Supervisor extends SupervisorDTO {

  /**
   * Vérifie si le responsable fait partie de l'entreprise.
   *
   * @param companyId l'ID de l'entreprise.
   * @return true si le responsable fait partie de l'entreprise, false sinon.
   */
  boolean isASupervisorOfTheCompany(int companyId);

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
