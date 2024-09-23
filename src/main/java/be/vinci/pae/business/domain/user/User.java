package be.vinci.pae.business.domain.user;

import be.vinci.pae.business.dto.UserDTO;
import be.vinci.pae.utils.exceptions.FatalException;
import be.vinci.pae.utils.exceptions.FatalExceptionType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * L'interface User représente un utilisateur dans le domaine métier.
 */
@JsonDeserialize(as = UserImpl.class)
public interface User extends UserDTO {

  /**
   * Vérifie si le mot-de-passe fourni correspond au mot-de-passe de l'utilisateur.
   *
   * @param password le mot de passe à vérifier.
   * @return true si le mot de passe est correct, false sinon.
   */
  boolean checkPassword(String password);

  /**
   * Vérifie si le mot-de-passe fourni contient au moins 5 caractères dont 1 chiffre.
   *
   * @param password le mot-de-passe à vérifier.
   * @return true si le mot-de-passe est conforme, false sinon.
   */
  boolean checkPasswordValidity(String password);

  /**
   * Crypte le mot-de-passe fourni et modifie le mot-de-passe de l'utilisateur avec le mot-de-passe
   * crypté.
   *
   * @param password le nouveau mot-de-passe à crypter.
   * @return le mot-de-passe crypté.
   * @throws FatalException {@link FatalExceptionType#SERVER} si une erreur s'est produite lors du
   *                        cryptage.
   */
  String setEncryptedPassword(String password);

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

  /**
   * Vérifie si le rôle est conforme.
   *
   * @param role le rôle à vérifier
   * @return true si le rôle est conforme, false sinon
   */
  boolean checkRoleValidity(String role);

  /**
   * Vérifie si le rôle fourni peut être attribué à l'utilisateur avec l'email fourni.
   *
   * @param email l'email de l'utilisateur.
   * @param role  le rôle à attribuer.
   * @return true si le rôle peut être attribué, false sinon.
   */
  boolean isRoleValidForEmail(String email, Role role);

}
