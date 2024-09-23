package be.vinci.pae.dal.dao.supervisor;

import be.vinci.pae.business.Factory;
import be.vinci.pae.business.dto.CompanyDTO;
import be.vinci.pae.business.dto.SupervisorDTO;
import be.vinci.pae.dal.DALServiceDAO;
import be.vinci.pae.dal.dao.company.CompanyDAO;
import be.vinci.pae.utils.exceptions.FatalException;
import be.vinci.pae.utils.exceptions.FatalExceptionType;
import jakarta.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation de l'interface SupervisorDAO.
 *
 * @see SupervisorDAO
 */
public class SupervisorDAOImpl implements SupervisorDAO {

  @Inject
  private Factory myFactory;
  @Inject
  private DALServiceDAO myDalServiceDAO;
  @Inject
  private CompanyDAO myCompanyDAO;

  /**
   * {@inheritDoc}
   *
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  @Override
  public List<SupervisorDTO> getAll() {
    try (PreparedStatement ps = myDalServiceDAO.getPs("SELECT * FROM pae.supervisors;")) {
      return getData(ps);
    } catch (SQLException e) {
      throw new FatalException("Get All Supervisors", e, FatalExceptionType.DATABASE);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  @Override
  public List<SupervisorDTO> getAllByCompanyId(int companyId) {
    try (PreparedStatement ps = myDalServiceDAO.getPs(
        "SELECT * FROM pae.supervisors WHERE company = ?;")) {
      ps.setInt(1, companyId);
      return getData(ps);
    } catch (SQLException e) {
      throw new FatalException("Get Supervisors of Company", e, FatalExceptionType.DATABASE);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  @Override
  public SupervisorDTO getOne(int id) {
    try (PreparedStatement ps = myDalServiceDAO.getPs(
        "SELECT * FROM pae.supervisors WHERE id = ?;")) {
      ps.setInt(1, id);
      List<SupervisorDTO> supervisors = getData(ps);
      if (supervisors.isEmpty()) {
        return null;
      }
      return supervisors.get(0);
    } catch (SQLException e) {
      throw new FatalException("Get All Supervisors", e, FatalExceptionType.DATABASE);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  @Override
  public SupervisorDTO insert(SupervisorDTO supervisorDTO) {
    try (PreparedStatement ps = myDalServiceDAO.getPs(
        "INSERT INTO pae.supervisors (first_name, last_name, phone_number, email, "
            + "company, version) VALUES (?, ?, ?, ?, ?, ?) RETURNING *;")) {
      ps.setString(1, supervisorDTO.getFirstName());
      ps.setString(2, supervisorDTO.getLastName());
      ps.setString(3, supervisorDTO.getPhoneNumber());
      ps.setString(4, supervisorDTO.getEmail());
      ps.setInt(5, supervisorDTO.getCompanyId());
      ps.setInt(6, 1);

      List<SupervisorDTO> result = getData(ps);

      if (result.isEmpty()) {
        return null;
      }
      return result.get(0);
    } catch (SQLException e) {
      throw new FatalException("Add One Supervisor", FatalExceptionType.DATABASE);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  @Override
  public SupervisorDTO getOneByCompany(SupervisorDTO supervisorDTO) {
    try (PreparedStatement ps = myDalServiceDAO.getPs(
        "SELECT * FROM pae.supervisors WHERE phone_number = ? AND company = ?;")) {
      ps.setString(1, supervisorDTO.getPhoneNumber());
      ps.setInt(2, supervisorDTO.getCompanyId());

      List<SupervisorDTO> result = getData(ps);

      if (result.isEmpty()) {
        return null;
      }
      return result.get(0);
    } catch (SQLException e) {
      throw new FatalException("Get Supervisor By Company", e, FatalExceptionType.DATABASE);
    }
  }

  /**
   * Extrait les objets SupervisorDTO du ResultSet d'un PreparedStatement.
   *
   * @param ps le PreparedStatement à exécuter
   * @return une liste des objets SupervisorDTO
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  private List<SupervisorDTO> getData(PreparedStatement ps) {
    try (ResultSet rs = ps.executeQuery()) {
      List<SupervisorDTO> supervisorDTOList = new ArrayList<>();
      while (rs.next()) {
        final int id = rs.getInt("id");
        final String firstName = rs.getString("first_name");
        final String lastName = rs.getString("last_name");
        final String phoneNumber = rs.getString("phone_number");
        final String email = rs.getString("email");
        final int version = rs.getInt("version");
        final int companyId = rs.getInt("company");
        final CompanyDTO companyDTO = myCompanyDAO.getOne(companyId);

        SupervisorDTO supervisorDTO = myFactory.getSupervisorDTO();
        supervisorDTO.setId(id);
        supervisorDTO.setFirstName(firstName);
        supervisorDTO.setLastName(lastName);
        supervisorDTO.setPhoneNumber(phoneNumber);
        supervisorDTO.setEmail(email);
        supervisorDTO.setVersion(version);
        supervisorDTO.setCompanyId(companyId);
        supervisorDTO.setCompanyDTO(companyDTO);

        supervisorDTOList.add(supervisorDTO);
      }
      return supervisorDTOList;
    } catch (SQLException e) {
      throw new FatalException("Get Supervisor Data", e, FatalExceptionType.DATABASE);
    }
  }
}
