package be.vinci.pae.dal.dao.user;

import be.vinci.pae.business.Factory;
import be.vinci.pae.business.domain.user.Role;
import be.vinci.pae.business.dto.UserDTO;
import be.vinci.pae.dal.DALServiceDAO;
import be.vinci.pae.utils.Utils;
import be.vinci.pae.utils.exceptions.FatalException;
import be.vinci.pae.utils.exceptions.FatalExceptionType;
import be.vinci.pae.utils.exceptions.OptimisticLockException;
import jakarta.inject.Inject;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation de l'interface UserDAO.
 *
 * @see UserDAO
 */
public class UserDAOImpl implements UserDAO {

  @Inject
  private Factory myFactory;
  @Inject
  private DALServiceDAO myDalService;

  /**
   * {@inheritDoc}
   *
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  @Override
  public UserDTO getOne(String email) {
    try (PreparedStatement psByEmail = myDalService.getPs(
        "SELECT * FROM pae.users WHERE LOWER(users.email) = LOWER(?);")) {

      psByEmail.setString(1, email);

      List<UserDTO> users = getData(psByEmail, false);
      if (users.isEmpty()) {
        return null;
      }
      return users.get(0);

    } catch (SQLException e) {
      throw new FatalException("Get One User (email)", e, FatalExceptionType.DATABASE);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  @Override
  public UserDTO getOne(int id) {
    try (PreparedStatement psById = myDalService.getPs("SELECT * FROM pae.users WHERE id = ?;")) {

      psById.setInt(1, id);
      List<UserDTO> user = getData(psById, false);
      if (user.isEmpty()) {
        return null;
      }
      return user.get(0);
    } catch (SQLException e) {
      throw new FatalException("Get One User (ID)", e, FatalExceptionType.DATABASE);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  @Override
  public List<UserDTO> getAll() {
    try (PreparedStatement ps = myDalService.getPs("""
        SELECT u.*, i.id AS internship
        FROM pae.users u
                 LEFT OUTER JOIN pae.internships i ON u.id = i.student;""")) {
      return getData(ps, true);
    } catch (SQLException e) {
      throw new FatalException("Get All Users", e, FatalExceptionType.DATABASE);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  @Override
  public UserDTO insert(UserDTO userDTO) {
    String academicYear = Utils.calculateAcademicYear();
    String registrationDate = LocalDate.now().toString();

    try (PreparedStatement ps = myDalService.getPs(
        "INSERT INTO pae.users (first_name, last_name, password, phone_number, email, role, "
            + "registration_date, academic_year, version) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) "
            + "RETURNING *;")) {

      ps.setString(1, userDTO.getFirstName());
      ps.setString(2, userDTO.getLastName());
      ps.setString(3, userDTO.getPassword());
      ps.setString(4, userDTO.getPhoneNumber());
      ps.setString(5, userDTO.getEmail());
      ps.setString(6, userDTO.getRole().toString());
      ps.setDate(7, Date.valueOf(registrationDate));
      ps.setString(8, academicYear);
      ps.setInt(9, 1);

      List<UserDTO> users = getData(ps, false);
      if (!checkOptimisticLock(users, userDTO.getId())) {
        return null;
      }
      return users.get(0);

    } catch (SQLException e) {
      throw new FatalException("Add One User", e, FatalExceptionType.DATABASE);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  @Override
  public UserDTO modifyUser(UserDTO userDTO, String fieldType) {
    String sql;
    String fieldValue;
    switch (fieldType) {
      case "lastName":
        sql = "UPDATE pae.users SET last_name = ? , version= ? "
            + "WHERE id = ? AND version = ? RETURNING *;";
        fieldValue = userDTO.getLastName();
        break;
      case "email":
        sql = "UPDATE pae.users SET email = ? , version= ? "
            + "WHERE id = ? AND version = ? RETURNING *;";
        fieldValue = userDTO.getEmail();
        break;
      case "phoneNumber":
        sql = "UPDATE pae.users SET phone_number = ? , version= ? "
            + "WHERE id = ? AND version = ? RETURNING *;";
        fieldValue = userDTO.getPhoneNumber();
        break;
      default:
        sql = "UPDATE pae.users SET first_name = ? , version= ? "
            + "WHERE id = ? AND version = ? RETURNING *;";
        fieldValue = userDTO.getFirstName();
    }
    try (PreparedStatement ps = myDalService.getPs(sql)) {

      ps.setString(1, fieldValue);
      ps.setInt(2, userDTO.getVersion() + 1);
      ps.setInt(3, userDTO.getId());
      ps.setInt(4, userDTO.getVersion());

      List<UserDTO> users = getData(ps, false);
      if (!checkOptimisticLock(users, userDTO.getId())) {
        return null;
      }

      return users.get(0);

    } catch (SQLException e) {
      throw new FatalException("Modify User", e, FatalExceptionType.DATABASE);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  @Override
  public boolean modifyPassword(int userId, String hashedPassword, int version) {

    try (PreparedStatement ps = myDalService.getPs("UPDATE pae.users SET password = ? ,"
        + " version= ? WHERE id = ? AND version = ? RETURNING *;")) {

      ps.setString(1, hashedPassword);
      ps.setInt(2, version + 1);
      ps.setInt(3, userId);
      ps.setInt(4, version);

      List<UserDTO> users = getData(ps, false);

      return checkOptimisticLock(users, userId);
    } catch (SQLException e) {
      throw new FatalException("Modify User Password", e, FatalExceptionType.DATABASE);
    }
  }

  /**
   * Exécute la requête et récupère les données.
   *
   * @param ps             le PreparedStatement à exécuter.
   * @param withInternship true s'il faut placer si l'étudiant a un stage, false sinon.
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  private List<UserDTO> getData(PreparedStatement ps, boolean withInternship) {
    try (ResultSet rs = ps.executeQuery()) {

      List<UserDTO> users = new ArrayList<>();
      while (rs.next()) {
        final int id = rs.getInt("id");
        final String firstName = rs.getString("first_name");
        final String lastName = rs.getString("last_name");
        final String password = rs.getString("password");
        final String phoneNumber = rs.getString("phone_number");
        final String email = rs.getString("email");
        final String dateRegistration = rs.getString("registration_date");
        final String roleRS = rs.getString("role");
        final String academicYear = rs.getString("academic_year");
        final int version = rs.getInt("version");

        UserDTO userDTO = myFactory.getUserDTO();
        userDTO.setFirstName(firstName);
        userDTO.setId(id);
        userDTO.setLastName(lastName);
        userDTO.setPassword(password);
        userDTO.setPhoneNumber(phoneNumber);
        userDTO.setEmail(email);
        userDTO.setRegistrationDate(dateRegistration);
        Role role = Role.valueOf(roleRS);
        userDTO.setRole(role);
        userDTO.setAcademicYear(academicYear);
        userDTO.setVersion(version);

        boolean hasInternship = false;
        if (withInternship) {
          final int internshipId = rs.getInt("internship");
          if (internshipId != 0) {
            hasInternship = true;
          }
        }
        userDTO.setHasInternship(hasInternship);

        users.add(userDTO);
      }

      return users;
    } catch (SQLException e) {
      throw new FatalException("Set User Fields", e, FatalExceptionType.DATABASE);
    }
  }

  /**
   * Vérifie l'Optimistic Lock pour une liste d'utilisateurs et un ID spécifié.
   *
   * @param users une liste de UserDTO à vérifier.
   * @param id    l'ID de l'utilisateur à vérifier.
   * @return true si la liste des utilisateurs n'est pas vide, false si l'utilisateur n'existe plus
   * @throws OptimisticLockException si un utilisateur est trouvé avec l'ID spécifié, mais que la
   *                                 liste est vide.
   */
  private boolean checkOptimisticLock(List<UserDTO> users, int id) {
    if (users.isEmpty()) {
      UserDTO userDTOTOCheck = getOne(id);
      if (userDTOTOCheck != null) {
        throw new OptimisticLockException();
      }
      return false;
    }
    return true;
  }
}
