package be.vinci.pae.business.ucc.auths;

import be.vinci.pae.business.dto.UserDTO;
import be.vinci.pae.utils.exceptions.BusinessException;

/**
 * L'interface AuthsUCC (User Control Component) définit les opérations liées à la gestion des
 * autorisations des utilisateurs.
 */
public interface AuthsUCC {

  /**
   * Tente de connecter un utilisateur en utilisant l'email et le mot-de-passe fournis.
   *
   * @param email    l'email de l'utilisateur.
   * @param password le mot de passe de l'utilisateur.
   * @return un objet UserDTO représentant l'utilisateur connecté.
   * @throws BusinessException si l'utilisateur est introuvable ou si le mot-de-passe est
   *                           incorrect.
   */
  UserDTO login(String email, String password);

  /**
   * Tente d'inscrire un utilisateur à l'aide des informations fournies.
   *
   * @param userDTO  l'utilisateur à inscrire
   * @param password le mot-de-passe non-crypté de l'utilisateur
   * @param role     le rôle de l'utilisateur
   * @return un objet UserDTO représentant l'utilisateur inscrit, ou null si l'inscription échoue
   * @throws BusinessException si l'utilisateur est déjà enregistré ou si les informations fournies
   *                           sont invalides.
   */
  UserDTO register(UserDTO userDTO, String password, String role);
}
