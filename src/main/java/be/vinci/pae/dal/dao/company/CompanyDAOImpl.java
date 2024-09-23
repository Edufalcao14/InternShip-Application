package be.vinci.pae.dal.dao.company;

import be.vinci.pae.business.Factory;
import be.vinci.pae.business.dto.CompanyDTO;
import be.vinci.pae.dal.DALServiceDAO;
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
 * Implémentation de l'interface CompanyDAO.
 *
 * @see CompanyDAO
 */
public class CompanyDAOImpl implements CompanyDAO {

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
  public CompanyDTO getOne(String email) {
    try (PreparedStatement psByEmail = myDalService.getPs(
        "SELECT * FROM pae.companies WHERE companies.email = ?;")) {

      psByEmail.setString(1, email);

      List<CompanyDTO> companyDTOList = getData(psByEmail, false);

      if (companyDTOList.isEmpty()) {
        return null;
      }

      return companyDTOList.get(0);
    } catch (SQLException e) {
      throw new FatalException("Get One Company (email)", e, FatalExceptionType.DATABASE);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  @Override
  public CompanyDTO getOne(int id) {
    try (PreparedStatement psById = myDalService.getPs(
        "SELECT * FROM pae.companies WHERE id = ?;")) {

      psById.setInt(1, id);

      List<CompanyDTO> companyDTOList = getData(psById, false);

      if (companyDTOList.isEmpty()) {
        return null;
      }

      return companyDTOList.get(0);
    } catch (SQLException e) {
      throw new FatalException("Get One Company (ID)", e, FatalExceptionType.DATABASE);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  @Override
  public CompanyDTO getOne(int companyId, int version) {
    try (PreparedStatement psById = myDalService.getPs(
        "SELECT * FROM pae.companies WHERE id = ? AND version = ?;")) {

      psById.setInt(1, companyId);
      psById.setInt(2, version);

      List<CompanyDTO> companyDTOList = getData(psById, false);

      if (companyDTOList.isEmpty()) {
        return null;
      }

      return companyDTOList.get(0);
    } catch (SQLException e) {
      throw new FatalException("Get One Company (ID, version)", e, FatalExceptionType.DATABASE);
    }
  }


  /**
   * {@inheritDoc}
   *
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  @Override
  public CompanyDTO getOne(String name, String designation) {
    try (PreparedStatement ps = myDalService.getPs(
        "SELECT * FROM pae.companies WHERE companies.name = ? AND companies.designation = ?;")) {

      ps.setString(1, name);
      ps.setString(2, designation);

      List<CompanyDTO> companyDTOList = getData(ps, false);

      if (companyDTOList.isEmpty()) {
        return null;
      }

      return companyDTOList.get(0);
    } catch (SQLException e) {
      throw new FatalException("Get One Company (name, designation)", e,
          FatalExceptionType.DATABASE);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  @Override
  public List<CompanyDTO> getAll(String orderBy, boolean descOrder) {
    orderBy = orderBy == null ? ";" : " ORDER BY " + orderBy;
    orderBy += descOrder ? " DESC;" : "";
    String sql = "SELECT c.*, COUNT(i.id) AS students_nbr " + "FROM pae.companies c "
        + "LEFT OUTER JOIN pae.supervisors s ON c.id = s.company "
        + "LEFT OUTER JOIN pae.internships i ON s.id = i.supervisor "
        + "GROUP BY c.id, c.name, c.designation, c.address, c.email, c.phone_number,"
        + "c.is_blacklisted, c.blacklist_reason, c.version";
    sql += orderBy;

    try (PreparedStatement ps = myDalService.getPs(sql)) {

      return getData(ps, true);
    } catch (SQLException e) {
      throw new FatalException("Get All Companies", e, FatalExceptionType.DATABASE);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  @Override
  public List<CompanyDTO> getAll(String academicYear, String orderBy, boolean descOrder) {
    orderBy = orderBy == null ? ";" : " ORDER BY " + orderBy;
    orderBy += descOrder ? " DESC;" : "";
    String sql = "SELECT c.*, COUNT(i.id) AS students_nbr " + "FROM pae.companies c "
        + "LEFT OUTER JOIN pae.supervisors s ON c.id = s.company "
        + "LEFT OUTER JOIN pae.internships i ON s.id = i.supervisor AND i.academic_year = ? "
        + "WHERE i.academic_year IS NULL OR i.academic_year = ? "
        + "GROUP BY c.id, c.name, c.designation, c.address, c.email, c.phone_number, "
        + "c.is_blacklisted, c.blacklist_reason, c.version";
    sql += orderBy;

    try (PreparedStatement ps = myDalService.getPs(sql)) {

      ps.setString(1, academicYear);
      ps.setString(2, academicYear);

      return getData(ps, true);
    } catch (SQLException e) {
      throw new FatalException("Get All Companies (academic year)", e, FatalExceptionType.DATABASE);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  @Override
  public CompanyDTO blacklistOneCompany(CompanyDTO companyDTO, String blacklistReason) {
    try (PreparedStatement ps = myDalService.getPs(
        "UPDATE pae.companies SET is_blacklisted = ?, blacklist_reason = ?, version = ? "
            + "WHERE id = ? AND version = ? RETURNING *;")) {

      ps.setBoolean(1, true);
      ps.setString(2, blacklistReason);
      ps.setInt(3, companyDTO.getVersion() + 1);
      ps.setInt(4, companyDTO.getId());
      ps.setInt(5, companyDTO.getVersion());

      List<CompanyDTO> companyDTOList = getData(ps, false);

      if (!checkOptimisticLock(companyDTOList, companyDTO.getId())) {
        return null;
      }

      return companyDTOList.get(0);
    } catch (SQLException e) {
      throw new FatalException("Blacklist One Company", e, FatalExceptionType.DATABASE);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  @Override
  public CompanyDTO insert(CompanyDTO companyDTO) {
    try (PreparedStatement ps = myDalService.getPs(
        "INSERT INTO pae.companies (name, designation, address, email, phone_number, "
            + "is_blacklisted, blacklist_reason, version) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
            + "RETURNING *;")) {

      ps.setString(1, companyDTO.getName());
      if (!companyDTO.getDesignation().isBlank()) {
        ps.setString(2, companyDTO.getDesignation());
      } else {
        ps.setString(2, null);
      }
      ps.setString(3, companyDTO.getAddress());
      ps.setString(4, companyDTO.getEmail());
      ps.setString(5, companyDTO.getPhoneNumber());
      ps.setBoolean(6, companyDTO.isBlacklisted());
      ps.setString(7, companyDTO.getBlacklistReason());
      ps.setInt(8, 1);

      List<CompanyDTO> companyDTOList = getData(ps, false);
      if (companyDTOList.isEmpty()) {
        return null;
      }
      return companyDTOList.get(0);

    } catch (SQLException e) {
      throw new FatalException("Add One Company", e, FatalExceptionType.DATABASE);
    }
  }


  /**
   * Exécute la requête et récupère les données.
   *
   * @param ps                 le PreparedStatement à exécuter.
   * @param withStudentsNumber true s'il faut placer le nombre d'étudiants ayant un stage dans
   *                           l'entreprise, false sinon.
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  private List<CompanyDTO> getData(PreparedStatement ps, boolean withStudentsNumber) {
    try (ResultSet rs = ps.executeQuery()) {
      List<CompanyDTO> list = new ArrayList<>();

      while (rs.next()) {
        final int id = rs.getInt("id");
        final String name = rs.getString("name");
        final String designation = rs.getString("designation");
        final String address = rs.getString("address");
        final String email = rs.getString("email");
        final String phoneNumber = rs.getString("phone_number");
        final boolean isBlacklisted = rs.getBoolean("is_blacklisted");
        final String blacklistReason = rs.getString("blacklist_reason");
        final int version = rs.getInt("version");

        CompanyDTO companyDTO = myFactory.getCompanyDTO();
        companyDTO.setId(id);
        companyDTO.setName(name);
        companyDTO.setDesignation(designation);
        companyDTO.setAddress(address);
        companyDTO.setEmail(email);
        companyDTO.setPhoneNumber(phoneNumber);
        companyDTO.setBlacklisted(isBlacklisted);
        companyDTO.setBlacklistReason(blacklistReason);
        companyDTO.setVersion(version);

        if (withStudentsNumber) {
          final int studentsNumber = rs.getInt("students_nbr");
          companyDTO.setStudentsNumber(studentsNumber);
        }
        list.add(companyDTO);
      }
      return list;
    } catch (SQLException e) {
      throw new FatalException("Get Company Data", e, FatalExceptionType.DATABASE);
    }
  }

  /**
   * Vérifie l'Optimistic Lock pour une liste d'entreprises et un ID spécifié.
   *
   * @param companies une liste de CompanyDTO à vérifier.
   * @param id        l'ID de l'entreprise à vérifier.
   * @return true si la liste des entreprises n'est pas vide, false si l'entreprise n'existe plus
   * @throws OptimisticLockException si une entreprise est trouvée avec l'ID spécifié, mais que la
   *                                 liste est vide.
   */
  private boolean checkOptimisticLock(List<CompanyDTO> companies, int id) {
    if (companies.isEmpty()) {
      CompanyDTO companyDTOToCheck = getOne(id);
      if (companyDTOToCheck != null) {
        throw new OptimisticLockException();
      }
      return false;
    }
    return true;
  }

}
