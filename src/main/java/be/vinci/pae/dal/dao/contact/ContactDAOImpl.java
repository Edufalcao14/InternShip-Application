package be.vinci.pae.dal.dao.contact;

import be.vinci.pae.business.Factory;
import be.vinci.pae.business.domain.contact.Place;
import be.vinci.pae.business.domain.contact.State;
import be.vinci.pae.business.dto.ContactDTO;
import be.vinci.pae.business.dto.UserDTO;
import be.vinci.pae.dal.DALServiceDAO;
import be.vinci.pae.dal.dao.company.CompanyDAO;
import be.vinci.pae.dal.dao.user.UserDAO;
import be.vinci.pae.utils.Utils;
import be.vinci.pae.utils.exceptions.FatalException;
import be.vinci.pae.utils.exceptions.FatalExceptionType;
import be.vinci.pae.utils.exceptions.OptimisticLockException;
import jakarta.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation de l'interface ContactDAO.
 *
 * @see ContactDAO
 */
public class ContactDAOImpl implements ContactDAO {

  @Inject
  private CompanyDAO myCompanyDAO;
  @Inject
  private Factory myFactory;
  @Inject
  private UserDAO myUserDAO;
  @Inject
  private DALServiceDAO myDalService;

  /**
   * {@inheritDoc}
   *
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  @Override
  public ContactDTO getOne(int idContact, int version) {

    try (PreparedStatement ps = myDalService.getPs(
        "SELECT * FROM pae.contacts WHERE id=? AND version=?;")) {

      ps.setInt(1, idContact);
      ps.setInt(2, version);

      List<ContactDTO> contactDTOList = getData(ps);
      if (!checkOptimisticLock(contactDTOList, idContact)) {
        return null;
      }
      return contactDTOList.get(0);

    } catch (SQLException e) {
      throw new FatalException("Get One Contact (ID, version)", e, FatalExceptionType.DATABASE);
    }

  }

  /**
   * {@inheritDoc}
   *
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  @Override
  public ContactDTO getOne(int idContact) {

    try (PreparedStatement ps = myDalService.getPs("SELECT * FROM pae.contacts WHERE id=?;")) {

      ps.setInt(1, idContact);

      List<ContactDTO> contactDTOList = getData(ps);
      if (!checkOptimisticLock(contactDTOList, idContact)) {
        return null;
      }
      return contactDTOList.get(0);

    } catch (SQLException e) {
      throw new FatalException("Get One Contact (ID)", e, FatalExceptionType.DATABASE);
    }

  }

  /**
   * {@inheritDoc}
   *
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  @Override
  public boolean getOne(int company, int student, String academicYear) {
    try (PreparedStatement ps = myDalService.getPs(
        "SELECT * FROM  pae.contacts WHERE company = ? AND student = ? AND academic_year = ?;")) {

      ps.setInt(1, company);
      ps.setInt(2, student);
      ps.setString(3, academicYear);

      List<ContactDTO> contactDTOList = getData(ps);
      return contactDTOList.isEmpty();
    } catch (SQLException e) {
      throw new FatalException("Get One Contact (company, student, academic year)", e,
          FatalExceptionType.DATABASE);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  @Override
  public boolean getOneAcceptedContact(int idUser) {
    try (PreparedStatement ps = myDalService.getPs(
        "SELECT * FROM  pae.contacts WHERE student = ? AND state = ?;")) {

      ps.setInt(1, idUser);
      ps.setString(2, State.ACCEPTE.toString());

      List<ContactDTO> contactDTOList = getData(ps);
      return !contactDTOList.isEmpty();
    } catch (SQLException e) {
      throw new FatalException("Get One Accepted Contact", e, FatalExceptionType.DATABASE);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  @Override
  public List<ContactDTO> getAllByUser(int userId, boolean isFollowed) {
    String sql = "SELECT * FROM pae.contacts WHERE student = ?;";
    if (isFollowed) {
      sql = "SELECT * FROM pae.contacts WHERE student = ? AND state != ?;";
    }
    try (PreparedStatement ps = myDalService.getPs(sql)) {
      ps.setInt(1, userId);
      if (isFollowed) {
        ps.setString(2, State.NON_SUIVI.toString());
      }

      return getData(ps);
    } catch (SQLException e) {
      throw new FatalException("Get Contacts By User (userId, isFollowed)", e,
          FatalExceptionType.DATABASE);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  @Override
  public List<ContactDTO> getAllByCompany(int companyId) {
    try (PreparedStatement ps = myDalService.getPs(
        "SELECT * FROM pae.contacts WHERE company = ?;")) {

      ps.setInt(1, companyId);

      return getData(ps);
    } catch (SQLException e) {
      throw new FatalException("Get Contacts By Company (companyId)", e,
          FatalExceptionType.DATABASE);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  @Override
  public List<ContactDTO> getAll() {
    try (PreparedStatement ps = myDalService.getPs("SELECT * FROM pae.contacts;")) {
      return getData(ps);
    } catch (SQLException e) {
      throw new FatalException("Get All Contacts", e, FatalExceptionType.DATABASE);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  @Override
  public ContactDTO insert(int company, int student) {
    try (PreparedStatement ps = myDalService.getPs(
        "INSERT INTO pae.contacts " + "(company, student, state, academic_year,version)"
            + "VALUES (?, ?, ?, ?, ?) RETURNING *;")) {

      ps.setInt(1, company);
      ps.setInt(2, student);
      ps.setString(3, State.INITIE.toString());
      ps.setString(4, Utils.calculateAcademicYear());
      ps.setInt(5, 1);

      List<ContactDTO> contactDTOList = getData(ps);
      if (contactDTOList.isEmpty()) {
        return null;
      }
      return contactDTOList.get(0);
    } catch (SQLException e) {
      throw new FatalException("Add One Contact", e, FatalExceptionType.DATABASE);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  @Override
  public ContactDTO update(ContactDTO contactDTO) {
    try (PreparedStatement ps = myDalService.getPs(
        "UPDATE pae.contacts " + "SET version = version + 1,  "
            + "state = ?, refusal_reason = ?, meeting_place = ?"
            + "WHERE id = ? AND version = ? RETURNING *;")) {

      ps.setString(1, contactDTO.getState().toString());
      ps.setString(2, contactDTO.getRefusalReason());
      if (contactDTO.getMeetingPlace() != null) {
        ps.setString(3, contactDTO.getMeetingPlace().toString());
      } else {
        ps.setString(3, null);
      }
      ps.setInt(4, contactDTO.getId());
      ps.setInt(5, contactDTO.getVersion());

      List<ContactDTO> contactDTOList = getData(ps);
      if (!checkOptimisticLock(contactDTOList, contactDTO.getId())) {
        return null;
      }
      return contactDTOList.get(0);
    } catch (SQLException e) {
      throw new FatalException("Update One Contact (contactDTO)", e, FatalExceptionType.DATABASE);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  public void suspendAllContactsOfStudent(int idStudent) {
    try (PreparedStatement ps = myDalService.getPs(
        "UPDATE pae.contacts SET state = ?, version = version + 1 "
            + "WHERE student = ? AND (state = ? OR state = ?) RETURNING *;")) {

      ps.setString(1, State.SUSPENDU.toString());
      ps.setInt(2, idStudent);
      ps.setString(3, State.PRIS.toString());
      ps.setString(4, State.INITIE.toString());

      getData(ps);
    } catch (SQLException e) {
      throw new FatalException("Suspend All Contact", e, FatalExceptionType.DATABASE);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  public void blacklistAllContactsOfCompany(int idCompany) {
    try (PreparedStatement ps = myDalService.getPs(
        "UPDATE pae.contacts SET state = ?, version = version + 1 "
            + "WHERE company = ? AND (state = ? OR state = ?) RETURNING *;")) {

      ps.setString(1, State.BLACKLISTE.toString());
      ps.setInt(2, idCompany);
      ps.setString(3, State.PRIS.toString());
      ps.setString(4, State.INITIE.toString());

      getData(ps);
    } catch (SQLException e) {
      throw new FatalException("Blacklist All Contacts", e, FatalExceptionType.DATABASE);
    }
  }

  /**
   * Exécute la requête et récupère les données.
   *
   * @param ps le PreparedStatement à exécuter.
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  private List<ContactDTO> getData(PreparedStatement ps) {
    try (ResultSet rs = ps.executeQuery()) {
      List<ContactDTO> contactDTOList = new ArrayList<>();

      while (rs.next()) {
        final int id = rs.getInt("id");
        final String state = rs.getString("state");
        final String meetingPlace = rs.getString("meeting_place");
        final String refusalReason = rs.getString("refusal_reason");
        final String academicYear = rs.getString("academic_year");
        final int version = rs.getInt("version");
        final int studentId = rs.getInt("student");
        final int company = rs.getInt("company");
        final UserDTO student = myUserDAO.getOne(studentId);

        ContactDTO contactDTO = myFactory.getContactDTO();
        contactDTO.setId(id);
        contactDTO.setState(State.valueOf(state));
        if (meetingPlace != null) {
          contactDTO.setMeetingPlace(Place.valueOf(meetingPlace));
        } else {
          contactDTO.setMeetingPlace(null);
        }
        contactDTO.setRefusalReason(refusalReason);
        contactDTO.setAcademicYear(academicYear);
        contactDTO.setVersion(version);
        contactDTO.setStudentId(studentId);
        contactDTO.setCompanyId(company);
        contactDTO.setCompanyDTO(myCompanyDAO.getOne(company));
        contactDTO.setStudentDTO(student);

        contactDTOList.add(contactDTO);
      }
      return contactDTOList;

    } catch (SQLException e) {
      throw new FatalException("Get Contact Data", e, FatalExceptionType.DATABASE);
    }
  }

  /**
   * Vérifie l'Optimistic Lock pour une liste de contacts et un ID spécifié.
   *
   * @param contacts une liste de ContactDTO à vérifier.
   * @param id       l'ID du contact à vérifier.
   * @return true si la liste de contacts n'est pas vide, false si le contact n'existe plus
   * @throws OptimisticLockException si une entreprise est trouvée avec l'ID spécifié, mais que la
   *                                 liste est vide.
   */
  private boolean checkOptimisticLock(List<ContactDTO> contacts, int id) {
    if (contacts.isEmpty()) {
      ContactDTO contactDTOToCheck = getOne(id);
      if (contactDTOToCheck != null) {
        throw new OptimisticLockException();
      }
      return false;
    }
    return true;
  }
}
