package be.vinci.pae.business.dto;

import be.vinci.pae.business.domain.internship.InternshipImpl;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * L'interface InternshipDTO (Data Transfer Object) définit la structure des données liées à un
 * stage dans le contexte de transfert de données.
 */
@JsonDeserialize(as = InternshipImpl.class)
public interface InternshipDTO {

  /**
   * Renvoie l'ID du stage.
   *
   * @return l'ID du stage.
   */
  int getId();

  /**
   * Modifie l'ID du stage.
   *
   * @param id le nouvel ID du stage.
   */
  void setId(int id);

  /**
   * Renvoie l'ID de l'étudiant associé au stage.
   *
   * @return l'ID de l'étudiant associé au stage.
   */
  int getStudentId();

  /**
   * Modifie l'ID de l'étudiant associé au stage.
   *
   * @param studentId le nouvel ID de l'étudiant.
   */
  void setStudentId(int studentId);

  /**
   * Renvoie le DTO de l'étudiant associé au stage.
   *
   * @return l'objet StudentDTO.
   */
  UserDTO getStudentDTO();

  /**
   * Modifie le DTO de l'étudiant associé au contrat.
   *
   * @param studentDTO le nouveau StudentDTO.
   */
  void setStudentDTO(UserDTO studentDTO);

  /**
   * Renvoie l'ID du responsable associé au stage.
   *
   * @return l'ID du responsable.
   */
  int getSupervisorId();

  /**
   * Modifie l'ID du responsable associé au stage.
   *
   * @param supervisorId le nouvel ID du responsable.
   */
  void setSupervisorId(int supervisorId);

  /**
   * Renvoie le DTO du responsable associé au stage.
   *
   * @return l'objet SupervisorDTO.
   */
  SupervisorDTO getSupervisorDTO();

  /**
   * Modifie le DTO du responsable associé au stage.
   *
   * @param supervisorDTO le nouveau supervisorDTO.
   */
  void setSupervisorDTO(SupervisorDTO supervisorDTO);

  /**
   * Renvoie l'ID du contact associé au stage.
   *
   * @return l'ID du contact.
   */
  int getContactId();

  /**
   * Modifie l'ID du contact associé au stage.
   *
   * @param contactId le nouvel ID du contact.
   */
  void setContactId(int contactId);

  /**
   * Renvoie le DTO du contact associé au stage.
   *
   * @return l'objet ContactDTO.
   */
  ContactDTO getContactDTO();

  /**
   * Modifie le DTO du contact associé au stage.
   *
   * @param contactDTO le nouveau ContactDTO.
   */
  void setContactDTO(ContactDTO contactDTO);

  /**
   * Renvoie le sujet du stage.
   *
   * @return le sujet du stage.
   */
  String getSubject();

  /**
   * Modifie le sujet du stage.
   *
   * @param subject le nouveau sujet du stage.
   */
  void setSubject(String subject);

  /**
   * Renvoie la date de signature du stage.
   *
   * @return la date de signature du stage.
   */
  String getSignatureDate();

  /**
   * Modifie la date de signature du stage.
   *
   * @param signatureDate la nouvelle date de signature.
   */
  void setSignatureDate(String signatureDate);

  /**
   * Renvoie l'année académique du stage.
   *
   * @return l'année académique du stage.
   */
  String getAcademicYear();

  /**
   * Modifie l'année académique du stage.
   *
   * @param academicYear la nouvelle année académique.
   */
  void setAcademicYear(String academicYear);

  /**
   * Renvoie la version du stage.
   *
   * @return la version du stage.
   */
  int getVersion();

  /**
   * Modifie la version du stage.
   *
   * @param version la nouvelle version.
   */
  void setVersion(int version);
}
