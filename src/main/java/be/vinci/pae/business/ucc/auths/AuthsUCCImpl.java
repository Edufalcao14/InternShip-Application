package be.vinci.pae.business.ucc.auths;

import be.vinci.pae.business.domain.user.Role;
import be.vinci.pae.business.domain.user.User;
import be.vinci.pae.business.dto.UserDTO;
import be.vinci.pae.dal.DALServiceUCC;
import be.vinci.pae.dal.dao.user.UserDAO;
import be.vinci.pae.utils.exceptions.BusinessException;
import be.vinci.pae.utils.exceptions.BusinessExceptionStatus;
import jakarta.inject.Inject;

/**
 * Implémentation de l'interface AuthsUCC fournissant des opérations liées à la gestion des
 * autorisations des utilisateurs.
 */
public class AuthsUCCImpl implements AuthsUCC {

  @Inject
  private UserDAO myUserDAO;
  @Inject
  private DALServiceUCC myDALService;

  /**
   * {@inheritDoc}
   *
   * @throws BusinessException si l'utilisateur est introuvable ou si le mot-de-passe est
   *                           incorrect.
   */
  @Override
  public UserDTO login(String email, String password) {
    UserDTO userDTO;

    try {
      myDALService.start();

      userDTO = myUserDAO.getOne(email);
      if (userDTO == null) {
        throw new BusinessException("Utilisateur introuvable avec cet email.",
            BusinessExceptionStatus.NOT_FOUND);
      }

      // Caste l'userDTO en User afin de pouvoir checker son mot-de-passe
      User user = (User) userDTO;

      if (!user.checkPassword(password)) {
        throw new BusinessException("Mot-de-passe incorrect.",
            BusinessExceptionStatus.UNAUTHORIZED);
      }
    } catch (Exception e) {
      myDALService.rollBack();
      throw e;
    } finally {
      myDALService.commit();
    }

    return userDTO;
  }

  /**
   * {@inheritDoc}
   *
   * @throws BusinessException si l'utilisateur est déjà enregistré ou si les informations fournies
   *                           sont invalides.
   */
  @Override
  public UserDTO register(UserDTO userDTO, String password, String roleString) {
    UserDTO responseUserDTO;
    try {
      myDALService.start();

      String email = userDTO.getEmail();
      String phoneNumber = userDTO.getPhoneNumber();

      if (myUserDAO.getOne(email) != null) {
        throw new BusinessException("Utilisateur existant. Veuillez vous connecter.",
            BusinessExceptionStatus.ALREADY_EXISTS);
      }

      User user = (User) userDTO;

      if (!user.checkEmailValidity(email)) {
        throw new BusinessException("Email invalide.", BusinessExceptionStatus.REQUEST_ERROR);
      }

      if (!user.checkPhoneNumberValidity(phoneNumber)) {
        throw new BusinessException("Numéro de téléphone invalide.",
            BusinessExceptionStatus.REQUEST_ERROR);
      }

      if (!user.checkPasswordValidity(password)) {
        throw new BusinessException(
            "Mot-de-passe invalide. Le mot-de-passe doit contenir"
                + " au moins 5 caractères dont 1 chiffre.",
            BusinessExceptionStatus.REQUEST_ERROR);
      }

      user.setEncryptedPassword(password);

      if (!user.checkRoleValidity(roleString.toUpperCase())) {
        throw new BusinessException("Rôle invalide.", BusinessExceptionStatus.REQUEST_ERROR);
      }
      Role role = Role.valueOf(roleString.toUpperCase());
      if (!user.isRoleValidForEmail(userDTO.getEmail(), role)) {
        throw new BusinessException("Rôle interdit.", BusinessExceptionStatus.ACCESS_DENIED);
      }
      userDTO.setRole(role);

      responseUserDTO = myUserDAO.insert(userDTO);
    } catch (Exception e) {
      myDALService.rollBack();
      throw e;
    } finally {
      myDALService.commit();
    }
    return responseUserDTO;
  }
}
