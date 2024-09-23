package be.vinci.pae.business.ucc.user;

import be.vinci.pae.business.dto.UserDTO;
import be.vinci.pae.utils.exceptions.BusinessException;
import java.util.List;

/**
 * L'interface UserUCC (User Control Component) définit les opérations liées à la gestion des
 * utilisateurs.
 */
public interface UserUCC {

  /**
   * Récupère les informations de l'utilisateur à partir de son ID.
   *
   * @param id ID de l'utilisateur.
   * @return UserDTO contenant les informations de l'utilisateur.
   * @throws BusinessException si une erreur métier survient.
   */
  UserDTO getUserById(int id);

  /**
   * Récupère les informations de tous les utilisateurs.
   *
   * @return Une liste de UserDTO contenant les informations de tous les utilisateurs
   */
  List<UserDTO> getAllUsers();

  /**
   * Modifie un champ spécifique d'un utilisateur.
   *
   * @param userDTO   l'utilisateur.
   * @param fieldType le champ à modifier.
   * @return l'utilisateur modifié.
   * @throws BusinessException si une erreur métier survient.
   */
  UserDTO modifyUserField(UserDTO userDTO, String fieldType);

  /**
   * Modifie le mot-de-passe d'un utilisateur.
   *
   * @param userId      l'ID de l'utilisateur.
   * @param password    le mot-de-passe de l'utilisateur.
   * @param newPassword le nouveau mot-de-passe de l'utilisateur.
   * @param version     la version de l'utilisateur.
   * @return true si le mot de passe a été changé, false sinon.
   * @throws BusinessException si une erreur métier survient.
   */
  boolean modifyUserPassword(int userId, String password, String newPassword, int version);

}
