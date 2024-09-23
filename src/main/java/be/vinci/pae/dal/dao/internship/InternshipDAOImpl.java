package be.vinci.pae.dal.dao.internship;

import be.vinci.pae.business.Factory;
import be.vinci.pae.business.dto.InternshipDTO;
import be.vinci.pae.dal.DALServiceDAO;
import be.vinci.pae.dal.dao.contact.ContactDAO;
import be.vinci.pae.dal.dao.supervisor.SupervisorDAO;
import be.vinci.pae.dal.dao.user.UserDAO;
import be.vinci.pae.utils.Utils;
import be.vinci.pae.utils.exceptions.FatalException;
import be.vinci.pae.utils.exceptions.FatalExceptionType;
import be.vinci.pae.utils.exceptions.OptimisticLockException;
import jakarta.inject.Inject;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation de l'interface InternshipDAO.
 *
 * @see InternshipDAO
 */
public class InternshipDAOImpl implements InternshipDAO {

  @Inject
  DALServiceDAO myDalServiceDAO;
  @Inject
  Factory factory;
  @Inject
  private UserDAO myUserDAO;
  @Inject
  private SupervisorDAO mySupervisorDAO;
  @Inject
  private ContactDAO myContactDAO;

  /**
   * {@inheritDoc}
   *
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  @Override
  public List<InternshipDTO> getAll() {
    try (PreparedStatement ps = myDalServiceDAO.getPs("SELECT * FROM pae.internships;")) {
      return getData(ps);
    } catch (SQLException e) {
      throw new FatalException("Get All Internships", e, FatalExceptionType.DATABASE);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  public InternshipDTO getOne(int id) {
    try (PreparedStatement psById = myDalServiceDAO.getPs(
        "SELECT * FROM pae.internships WHERE id = ?;")) {

      psById.setInt(1, id);

      List<InternshipDTO> internShips = getData(psById);

      if (internShips.isEmpty()) {
        return null;
      }
      return internShips.get(0);

    } catch (SQLException e) {
      throw new FatalException("Get One Internship (ID)", e, FatalExceptionType.DATABASE);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  @Override
  public InternshipDTO getOneByUserId(int studentId) {
    try (PreparedStatement ps = myDalServiceDAO.getPs(
        "SELECT * FROM pae.internships WHERE student = ?;")) {
      ps.setInt(1, studentId);
      List<InternshipDTO> internships = getData(ps);
      if (internships.isEmpty()) {
        return null;
      }
      return internships.get(0);
    } catch (SQLException e) {
      throw new FatalException("Get One Internship By User ID", e, FatalExceptionType.DATABASE);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws FatalException {@link FatalExceptionType#DATABASE} - si une erreur de DB se produit.
   */
  @Override
  public InternshipDTO insert(InternshipDTO internshipDTO) {
    String sqlWithSubject = "INSERT INTO pae.internships "
        + "(student, supervisor, contact,signature_date,academic_year,version,subject)"
        + "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING *;";
    String sqlWithoutSubject = "INSERT INTO pae.internships "
        + "(student, supervisor, contact, signature_date,academic_year,version)"
        + "VALUES (?, ?, ?, ?, ?, ?) RETURNING *;";
    String sqlChoice;

    if (internshipDTO.getSubject() == null) {
      sqlChoice = sqlWithoutSubject;
    } else {
      sqlChoice = sqlWithSubject;
    }
    try (PreparedStatement ps = myDalServiceDAO.getPs(sqlChoice)) {

      ps.setInt(1, internshipDTO.getStudentId());
      ps.setInt(2, internshipDTO.getSupervisorId());
      ps.setInt(3, internshipDTO.getContactId());
      ps.setDate(4, Date.valueOf(internshipDTO.getSignatureDate()));
      ps.setString(5, Utils.calculateAcademicYear());
      ps.setInt(6, 1);

      if (internshipDTO.getSubject() != null) {
        ps.setString(7, internshipDTO.getSubject());
      }

      List<InternshipDTO> internships = getData(ps);
      if (internships.isEmpty()) {
        throw new FatalException("Échec de l'ajout du stage.", FatalExceptionType.DATABASE);
      }
      return internships.get(0);
    } catch (SQLException e) {
      throw new FatalException("Add One Internship", e, FatalExceptionType.DATABASE);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  public int getCountOfAllInternships(String year) {
    try (PreparedStatement ps = myDalServiceDAO.getPs(
        "SELECT COUNT(id) FROM pae.internships WHERE academic_year = ?;")) {
      ps.setString(1, year);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return rs.getInt(1);
        } else {
          return 0;
        }
      }
    } catch (SQLException e) {
      throw new FatalException("Count All Internships", e, FatalExceptionType.DATABASE);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  public int getCountOfStudentsWithoutInternship(String year) {
    try (PreparedStatement ps = myDalServiceDAO.getPs("""
        SELECT COUNT(DISTINCT student)
                                    FROM pae.contacts
                                      WHERE academic_year = ?
                                        AND student NOT IN (SELECT DISTINCT student
                                                            FROM pae.internships
                                                            WHERE academic_year = ?);""")) {
      ps.setString(1, year);
      ps.setString(2, year);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return rs.getInt(1);
        } else {
          return 0;
        }
      }
    } catch (SQLException e) {
      throw new FatalException("Count Students Without Internship", e, FatalExceptionType.DATABASE);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws FatalException {@link FatalExceptionType#DATABASE} - si une erreur de DB se produit.
   */
  @Override
  public InternshipDTO modifyOneInternshipSubject(int id, String subject, int version) {
    try (PreparedStatement ps = myDalServiceDAO.getPs("UPDATE pae.internships SET subject = ? ,"
        + " version= ? WHERE id = ? AND version = ? RETURNING *;")) {

      ps.setString(1, subject);
      ps.setInt(2, version + 1);
      ps.setInt(3, id);
      ps.setInt(4, version);

      List<InternshipDTO> internships = getData(ps);

      if (!checkOptimisticLock(internships, id)) {
        return null;
      }

      return internships.get(0);

    } catch (SQLException e) {
      throw new FatalException("Modify Internship Subject", e, FatalExceptionType.DATABASE);
    }
  }

  /**
   * Exécute la requête et récupère les données.
   *
   * @param ps le PreparedStatement à exécuter.
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  private List<InternshipDTO> getData(PreparedStatement ps) {
    try (ResultSet rs = ps.executeQuery()) {
      List<InternshipDTO> internships = new ArrayList<>();
      while (rs.next()) {
        int id = rs.getInt("id");
        int studentId = rs.getInt("student");
        int supervisorId = rs.getInt("supervisor");
        int contactId = rs.getInt("contact");
        String subject = rs.getString("subject");
        String signatureDate = rs.getString("signature_date");
        String academicYear = rs.getString("academic_year");
        int version = rs.getInt("version");

        InternshipDTO internshipDTO = factory.getInternshipDTO();
        internshipDTO.setId(id);
        internshipDTO.setStudentId(studentId);
        internshipDTO.setStudentDTO(myUserDAO.getOne(studentId));
        internshipDTO.setSupervisorId(supervisorId);
        internshipDTO.setSupervisorDTO(mySupervisorDAO.getOne(supervisorId));
        internshipDTO.setContactId(contactId);
        internshipDTO.setContactDTO(myContactDAO.getOne(contactId));
        internshipDTO.setSubject(subject);
        internshipDTO.setSignatureDate(signatureDate);
        internshipDTO.setAcademicYear(academicYear);
        internshipDTO.setVersion(version);

        internships.add(internshipDTO);

      }
      return internships;
    } catch (SQLException e) {
      throw new FatalException("Get Internships Data", e, FatalExceptionType.DATABASE);
    }
  }

  /**
   * Vérifie l'Optimistic Lock pour une liste de stages et un ID spécifié.
   *
   * @param internships une liste de InternshipDTO à vérifier.
   * @param id          l'ID du stage à vérifier.
   * @return true si la liste des stages n'est pas vide, false si le stage n'existe plus
   * @throws OptimisticLockException si un stage est trouvé avec l'ID spécifié, mais que la liste
   *                                 est vide.
   */
  private boolean checkOptimisticLock(List<InternshipDTO> internships, int id) {
    if (internships.isEmpty()) {
      InternshipDTO internshipDTO = getOne(id);
      if (internshipDTO != null) {
        throw new OptimisticLockException();
      }
      return false;
    }
    return true;
  }

}
