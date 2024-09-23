package be.vinci.pae.dal.dao.internship;

import be.vinci.pae.business.dto.InternshipDTO;
import be.vinci.pae.utils.exceptions.FatalException;
import be.vinci.pae.utils.exceptions.FatalExceptionType;
import java.util.List;

/**
 * L'interface InternshipDAO définit les méthodes pour accéder et manipuler les données des stages
 * dans la base de données.
 */
public interface InternshipDAO {

  /**
   * Récupère la liste de tous les stages.
   *
   * @return Liste de tous les stages.
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  List<InternshipDTO> getAll();

  /**
   * Récupère un stage à partir de l'ID du stage.
   *
   * @param id l'ID du stage.
   * @return Le stage correspondant à l'ID spécifié, null si aucun n'est trouvé
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  InternshipDTO getOne(int id);

  /**
   * Récupère un contact à partir de l'identifiant de l'utilisateur.
   *
   * @param studentId l'identifiant de l'utilisateur.
   * @return Le stage correspondant à l'utilisateur spécifié, null si aucun n'est trouvé.
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  InternshipDTO getOneByUserId(int studentId);

  /**
   * Ajoute un nouveau stage à la DB.
   *
   * @param internshipDTO le stage à ajouter.
   * @return le stage ajouté.
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  InternshipDTO insert(InternshipDTO internshipDTO);

  /**
   * Renvoie le nombre d'étudiants ayant un stage pour une année académique donnée.
   *
   * @param year l'année académique sélectionnée.
   * @return le nombre d'étudiants ayant un stage.
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  int getCountOfAllInternships(String year);

  /**
   * Renvoie le nombre d'étudiants n'ayant pas de stage pour une année académique donnée.
   *
   * @param year l'année académique sélectionnée.
   * @return le nombre d'étudiants ayant un stage.
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  int getCountOfStudentsWithoutInternship(String year);

  /**
   * Modifie le sujet de stage de l'étudiant.
   *
   * @param id      l'ID du stage.
   * @param subject le nouveau sujet.
   * @param version la version du stage.
   * @return le stage modifié.
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  InternshipDTO modifyOneInternshipSubject(int id, String subject, int version);
}