package be.vinci.pae.dal.dao.user;

import be.vinci.pae.business.dto.UserDTO;
import be.vinci.pae.utils.exceptions.FatalException;
import be.vinci.pae.utils.exceptions.FatalExceptionType;
import java.util.List;

/**
 * L'interface UserDAO définit les méthodes pour accéder et manipuler les données des utilisateurs
 * dans la base de données.
 */
public interface UserDAO {

  /**
   * Récupère un objet UserDTO correspondant à l'utilisateur ayant l'email spécifié.
   *
   * @param email l'email de l'utilisateur.
   * @return un objet UserDTO représentant l'utilisateur, ou null si aucun utilisateur n'est trouvé.
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  UserDTO getOne(String email);

  /**
   * Récupère un objet UserDTO correspondant à l'utilisateur ayant l'ID spécifié.
   *
   * @param id l'ID de l'utilisateur.
   * @return un objet UserDTO représentant l'utilisateur, ou null si aucun utilisateur n'est trouvé.
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  UserDTO getOne(int id);

  /**
   * Récupère tous les objets UserDTO.
   *
   * @return tous les objets UserDTO.
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  List<UserDTO> getAll();


  /**
   * Modifie les informations d'un utilisateur.
   *
   * @param userDTO   l'utilisateur à modifier.
   * @param fieldType le champ à modifier.
   * @return l'user après les modifications.
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  UserDTO modifyUser(UserDTO userDTO, String fieldType);

  /**
   * Reçoit en paramètre l'ID et le mot-de-passe hashé et effectue la modification du mot-de-passe.
   *
   * @param userId         l'ID de l'utilisateur
   * @param hashedPassword le nouveau mot-de-passe hashé
   * @param version        la version de l'utilisateur
   * @return true si le mot-de-passe a été changé, false sinon.
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  boolean modifyPassword(int userId, String hashedPassword, int version);

  /**
   * Enregistre un utilisateur à l'aide des informations fournies.
   *
   * @param userDTO l'utilisateur à enregistrer.
   * @return un objet UserDTO représentant l'utilisateur, null si l'enregistrement échoue.
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  UserDTO insert(UserDTO userDTO);

}
