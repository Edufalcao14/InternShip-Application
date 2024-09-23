package be.vinci.pae.business.dto;

import be.vinci.pae.business.domain.user.Role;
import be.vinci.pae.business.domain.user.UserImpl;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * L'interface UserDTO (Data Transfer Object) définit la structure des données liées à un
 * utilisateur dans le contexte de transfert de données.
 */
@JsonDeserialize(as = UserImpl.class)
public interface UserDTO {

  /**
   * Renvoie l'ID de l'utilisateur.
   *
   * @return l'ID de l'utilisateur.
   */
  int getId();

  /**
   * Modifie l'ID de l'utilisateur.
   *
   * @param id le nouvel ID de l'utilisateur.
   */
  void setId(int id);

  /**
   * Renvoie le nom de l'utilisateur.
   *
   * @return le nom de l'utilisateur.
   */
  String getLastName();

  /**
   * Modifie le nom de l'utilisateur.
   *
   * @param lastName le nouveau nom de l'utilisateur.
   */
  void setLastName(String lastName);

  /**
   * Renvoie le prénom de l'utilisateur.
   *
   * @return le prénom de l'utilisateur.
   */
  String getFirstName();

  /**
   * Modifie le prénom de l'utilisateur.
   *
   * @param firstName le nouveau prénom de l'utilisateur.
   */
  void setFirstName(String firstName);

  /**
   * Renvoie le mot-de-passe de l'utilisateur.
   *
   * @return le mot-de-passe de l'utilisateur.
   */
  String getPassword();

  /**
   * Modifie le mot-de-passe de l'utilisateur.
   *
   * @param password le nouveau mot-de-passe de l'utilisateur.
   */
  void setPassword(String password);

  /**
   * Renvoie le numéro de téléphone de l'utilisateur.
   *
   * @return le numéro de téléphone de l'utilisateur.
   */
  String getPhoneNumber();

  /**
   * Modifie le numéro de téléphone de l'utilisateur.
   *
   * @param phoneNumber le nouveau numéro de téléphone de l'utilisateur.
   */
  void setPhoneNumber(String phoneNumber);

  /**
   * Renvoie l'email de l'utilisateur.
   *
   * @return l'email de l'utilisateur.
   */
  String getEmail();

  /**
   * Modifie l'email de l'utilisateur.
   *
   * @param email le nouvel email de l'utilisateur.
   */
  void setEmail(String email);

  /**
   * Renvoie la date d'inscription de l'utilisateur.
   *
   * @return la date d'inscription de l'utilisateur.
   */
  String getRegistrationDate();

  /**
   * Modifie la date d'inscription de l'utilisateur.
   *
   * @param dateRegistration la nouvelle date d'inscription de l'utilisateur.
   */
  void setRegistrationDate(String dateRegistration);

  /**
   * Renvoie le rôle de l'utilisateur.
   *
   * @return le rôle de l'utilisateur.
   */
  Role getRole();

  /**
   * Modifie le rôle de l'utilisateur.
   *
   * @param role le nouveau rôle de l'utilisateur.
   */
  void setRole(Role role);

  /**
   * Renvoie l'année académique de l'utilisateur.
   *
   * @return l'année académique de l'utilisateur.
   */
  String getAcademicYear();

  /**
   * Modifie l'année académique de l'utilisateur.
   *
   * @param academicYear la nouvelle année académique de l'utilisateur.
   */
  void setAcademicYear(String academicYear);

  /**
   * Renvoie la version de l'utilisateur.
   *
   * @return la version de l'utilisateur.
   */
  int getVersion();

  /**
   * Modifie la version de l'utilisateur.
   *
   * @param version la nouvelle version de l'utilisateur.
   */
  void setVersion(int version);

  /**
   * Renvoie la true si l'étudiant a un stage, false sinon.
   *
   * @return true si l'étudiant a un stage, false sinon.
   */
  boolean hasInternship();

  /**
   * Modifie la présence de stage de l'utilisateur.
   *
   * @param hasInternship la nouvelle valeur pour la présence de stage.
   */
  void setHasInternship(boolean hasInternship);
}
