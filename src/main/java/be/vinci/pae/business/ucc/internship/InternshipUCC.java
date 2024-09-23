package be.vinci.pae.business.ucc.internship;

import be.vinci.pae.business.dto.InternshipDTO;
import be.vinci.pae.utils.exceptions.BusinessException;
import java.util.List;

/**
 * L'interface InternshipUCC (User Control Component) définit les opérations liées à la gestion des
 * stages.
 */
public interface InternshipUCC {

  /**
   * Récupère la liste de tous les stages.
   *
   * @return Liste de tous les stages.
   */
  List<InternshipDTO> getAllInternships();

  /**
   * Récupère le stage de l'étudiant.
   *
   * @param id l'ID de l'étudiant
   * @return le stage de l'étudiant.
   * @throws BusinessException si le stage est introuvable.
   */
  InternshipDTO getInternshipByUserId(int id);

  /**
   * Ajoute un nouveau stage.
   *
   * @param internshipDTO le stage à ajouter.
   * @return le stage ajouté.
   * @throws BusinessException si une erreur métier survient.
   */
  InternshipDTO addInternship(InternshipDTO internshipDTO);

  /**
   * Modifie le sujet de stage de l'étudiant.
   *
   * @param id      stage à modifier.
   * @param subject le nouveau sujet de stage.
   * @param version la version du stage.
   * @return le stage modifié.
   * @throws BusinessException si une erreur métier survient.
   */
  InternshipDTO modifyInternshipSubject(int id, String subject, int version);

  /**
   * Récupère le nombre d'étudiants ayant un stage pour une année académique.
   *
   * @param year l'année académique.
   * @return le nombre d'étudiants.
   * @throws BusinessException si l'année académique est invalide.
   */
  int nbrOfStudentsWithInternship(String year);

  /**
   * Récupère le nombre d'étudiants n'ayant pas de stage pour une année académique.
   *
   * @param year l'année académique.
   * @return le nombre d'étudiants.
   * @throws BusinessException si l'année académique est invalide.
   */
  int nbrOfStudentsWithoutInternship(String year);
}
