package be.vinci.pae.business.ucc.user;

import be.vinci.pae.business.domain.user.User;
import be.vinci.pae.business.dto.UserDTO;
import be.vinci.pae.dal.DALServiceUCC;
import be.vinci.pae.dal.dao.user.UserDAO;
import be.vinci.pae.utils.exceptions.BusinessException;
import be.vinci.pae.utils.exceptions.BusinessExceptionStatus;
import jakarta.inject.Inject;
import java.util.List;


/**
 * Implémentation de l'interface UserUCC fournissant des opérations liées à la gestion des
 * utilisateurs.
 */
public class UserUCCImpl implements UserUCC {

  @Inject
  private UserDAO myUserDAO;
  @Inject
  private DALServiceUCC myDALService;

  /**
   * {@inheritDoc}
   *
   * @throws BusinessException si l'utilisateur est introuvable.
   */
  public UserDTO getUserById(int id) {
    UserDTO userDTO;

    try {
      myDALService.start();

      userDTO = myUserDAO.getOne(id);
      if (userDTO == null) {
        throw new BusinessException("Utilisateur introuvable avec cet ID.",
            BusinessExceptionStatus.NOT_FOUND);
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
   */
  public List<UserDTO> getAllUsers() {
    List<UserDTO> users;

    try {
      myDALService.start();

      users = myUserDAO.getAll();
    } catch (Exception e) {
      myDALService.rollBack();
      throw e;
    } finally {
      myDALService.commit();
    }

    return users;
  }

  /**
   * {@inheritDoc}
   *
   * @throws BusinessException si une erreur métier survient.
   */
  @Override
  public UserDTO modifyUserField(UserDTO userDTO, String fieldType) {
    try {
      myDALService.start();

      User user = (User) userDTO;
      UserDTO newUserDto;
      switch (fieldType) {
        case "firstName":
          newUserDto = myUserDAO.modifyUser(userDTO, "firstName");
          break;
        case "lastName":
          newUserDto = myUserDAO.modifyUser(userDTO, "lastName");
          break;
        case "email":
          String email = userDTO.getEmail();
          // check si l'email est valide
          if (!user.checkEmailValidity(email)) {
            throw new BusinessException("Email invalide.", BusinessExceptionStatus.REQUEST_ERROR);
          }
          // check si le rôle est valide pour l'email
          if (!user.isRoleValidForEmail(email, userDTO.getRole())) {
            throw new BusinessException(
                "Email invalide pour le rôle '" + userDTO.getRole() + "'.",
                BusinessExceptionStatus.UNAUTHORIZED);
          }
          newUserDto = myUserDAO.modifyUser(userDTO, "email");
          break;
        case "phoneNumber":
          // check si le numéro de téléphone est valide
          if (!user.checkPhoneNumberValidity(userDTO.getPhoneNumber())) {
            throw new BusinessException("Numéro de téléphone invalide.",
                BusinessExceptionStatus.REQUEST_ERROR);
          }
          newUserDto = myUserDAO.modifyUser(userDTO, "phoneNumber");
          break;
        default:
          throw new BusinessException("Type de champ non pris en charge.",
              BusinessExceptionStatus.REQUEST_ERROR);
      }

      if (newUserDto == null) {
        throw new BusinessException("Utilisateur introuvable.", BusinessExceptionStatus.NOT_FOUND);
      }

      return newUserDto;

    } catch (Exception e) {
      myDALService.rollBack();
      throw e;
    } finally {
      myDALService.commit();
    }

  }


  /**
   * {@inheritDoc}
   *
   * @throws BusinessException si une erreur métier survient.
   */
  public boolean modifyUserPassword(int userId, String password, String newPassword, int version) {

    try {
      myDALService.start();
      UserDTO userDTO = myUserDAO.getOne(userId);

      if (userDTO == null) {
        throw new BusinessException("Utilisateur introuvable avec cet ID.",
            BusinessExceptionStatus.NOT_FOUND);
      }

      User user = (User) userDTO;

      if (!user.checkPassword(password)) {
        throw new BusinessException("Mot-de-passe incorrect.",
            BusinessExceptionStatus.UNAUTHORIZED);
      }

      if (!user.checkPasswordValidity(newPassword)) {
        throw new BusinessException(
            "Mot-de-passe invalide. "
                + "Le mot-de-passe doit contenir au moins 5 caractères dont 1 chiffre.",
            BusinessExceptionStatus.REQUEST_ERROR);
      }

      String hashedPassword = user.setEncryptedPassword(newPassword);

      boolean result = myUserDAO.modifyPassword(userId, hashedPassword, version);
      if (!result) {
        throw new BusinessException("Utilisateur introuvable.",
            BusinessExceptionStatus.NOT_FOUND);
      }

      return true;

    } catch (Exception e) {
      myDALService.rollBack();
      throw e;
    } finally {
      myDALService.commit();
    }
  }
}
