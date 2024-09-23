package be.vinci.pae.business.dto;

import be.vinci.pae.business.domain.supervisor.SupervisorImpl;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * L'interface SupervisorDTO (Data Transfer Object) définit la structure des données liées à un
 * responsable de stage dans le contexte de transfert de données.
 */
@JsonDeserialize(as = SupervisorImpl.class)
public interface SupervisorDTO {

  /**
   * Renvoie l'ID du responsable de stage.
   *
   * @return l'ID du responsable de stage.
   */
  int getId();

  /**
   * Modifie l'ID du responsable de stage.
   *
   * @param id le nouvel ID.
   */
  void setId(int id);

  /**
   * Renvoie le prénom du responsable de stage.
   *
   * @return le prénom du responsable de stage.
   */
  String getFirstName();

  /**
   * Modifie le prénom du responsable de stage.
   *
   * @param firstName le nouveau prénom.
   */
  void setFirstName(String firstName);

  /**
   * Renvoie le nom du responsable de stage.
   *
   * @return le nom du responsable de stage.
   */
  String getLastName();

  /**
   * Modifie le nom du responsable de stage.
   *
   * @param lastName le nouveau nom.
   */
  void setLastName(String lastName);

  /**
   * Renvoie le numéro de téléphone du responsable de stage.
   *
   * @return le numéro de téléphone du responsable de stage.
   */
  String getPhoneNumber();

  /**
   * Modifie le numéro de téléphone du responsable de stage.
   *
   * @param phoneNumber le nouveau numéro de téléphone.
   */
  void setPhoneNumber(String phoneNumber);

  /**
   * Renvoie l'adresse e-mail du responsable de stage.
   *
   * @return l'adresse e-mail du responsable de stage.
   */
  String getEmail();

  /**
   * Modifie l'adresse e-mail du responsable de stage.
   *
   * @param email la nouvelle adresse e-mail.
   */
  void setEmail(String email);

  /**
   * Renvoie la version du responsable de stage.
   *
   * @return la version du responsable de stage.
   */
  int getVersion();

  /**
   * Modifie la version du responsable de stage.
   *
   * @param version la nouvelle version.
   */
  void setVersion(int version);

  /**
   * Renvoie l'ID de l'entreprise associée au responsable de stage.
   *
   * @return l'ID de l'entreprise.
   */
  int getCompanyId();

  /**
   * Modifie l'ID de l'entreprise associée au responsable de stage.
   *
   * @param companyId le nouvel ID de l'entreprise.
   */
  void setCompanyId(int companyId);

  /**
   * Renvoie le DTO de l'entreprise associée au responsable de stage.
   *
   * @return l'objet CompanyDTO.
   */
  CompanyDTO getCompanyDTO();

  /**
   * Modifie le DTO de l'entreprise associée au responsable de stage.
   *
   * @param companyDTO le nouveau CompanyDTO.
   */
  void setCompanyDTO(CompanyDTO companyDTO);
}
