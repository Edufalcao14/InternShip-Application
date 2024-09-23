package be.vinci.pae.business.dto;

import be.vinci.pae.business.domain.contact.ContactImpl;
import be.vinci.pae.business.domain.contact.Place;
import be.vinci.pae.business.domain.contact.State;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * L'interface ContactDTO (Data Transfer Object) définit la structure des données liées à un contact
 * dans le contexte de transfert de données.
 */
@JsonDeserialize(as = ContactImpl.class)
public interface ContactDTO {

  /**
   * Renvoie l'ID du contact.
   *
   * @return l'ID du contact.
   */
  int getId();

  /**
   * Modifie l'ID du contact.
   *
   * @param id le nouvel ID.
   */
  void setId(int id);

  /**
   * Renvoie l'ID de l'entreprise liée au contact.
   *
   * @return l'ID de l'entreprise.
   */
  int getCompanyId();

  /**
   * Modifie l'ID de l'entreprise liée au contact.
   *
   * @param companyId le nouvel ID de l'entreprise.
   */
  void setCompanyId(int companyId);

  /**
   * Renvoie le DTO de l'entreprise liée au contact.
   *
   * @return l'objet CompanyDTO.
   */
  CompanyDTO getCompanyDTO();

  /**
   * Modifie le DTO de l'entreprise liée au contact.
   *
   * @param companyDTO le nouveau CompanyDTO.
   */
  void setCompanyDTO(CompanyDTO companyDTO);

  /**
   * Renvoie l'ID de l'étudiant lié au contact.
   *
   * @return l'ID de l'étudiant.
   */
  int getStudentId();

  /**
   * Modifie l'ID de l'étudiant lié au contact.
   *
   * @param studentId le nouvel ID de l'étudiant.
   */
  void setStudentId(int studentId);

  /**
   * Renvoie l'état du contact.
   *
   * @return l'état du contact.
   */
  State getState();

  /**
   * Modifie l'état du contact.
   *
   * @param state le nouvel état du contact.
   */
  void setState(State state);

  /**
   * Renvoie le lieu de rencontre du contact.
   *
   * @return le lieu de rencontre du contact.
   */
  Place getMeetingPlace();

  /**
   * Modifie le lieu de rencontre du contact.
   *
   * @param meetingPlace le nouveau lieu de rencontre.
   */
  void setMeetingPlace(Place meetingPlace);

  /**
   * Renvoie la raison de refus du contact.
   *
   * @return la raison de refus du contact.
   */
  String getRefusalReason();

  /**
   * Modifie la raison de refus du contact.
   *
   * @param refusalReason la nouvelle raison de refus.
   */
  void setRefusalReason(String refusalReason);

  /**
   * Renvoie l'année académique associée au contact.
   *
   * @return l'année académique associée au contact.
   */
  String getAcademicYear();

  /**
   * Modifie l'année académique associée au contact.
   *
   * @param academicYear la nouvelle année académique.
   */
  void setAcademicYear(String academicYear);

  /**
   * Renvoie version associée au contact.
   *
   * @return la version associée au contact.
   */
  int getVersion();

  /**
   * Modifie version associée au contact.
   *
   * @param version la nouvelle version.
   */
  void setVersion(int version);

  /**
   * Renvoie le DTO de l'étudiant lié au contact.
   *
   * @return l'objet studentDTO.
   */
  UserDTO getStudentDTO();

  /**
   * Modifie le DTO de l'étudiant lié au contact.
   *
   * @param studentDTO le nouveau studentDTO.
   */
  void setStudentDTO(UserDTO studentDTO);
}
