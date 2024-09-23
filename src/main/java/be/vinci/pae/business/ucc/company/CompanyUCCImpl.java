package be.vinci.pae.business.ucc.company;

import be.vinci.pae.business.domain.company.Company;
import be.vinci.pae.business.dto.CompanyDTO;
import be.vinci.pae.dal.DALServiceUCC;
import be.vinci.pae.dal.dao.company.CompanyDAO;
import be.vinci.pae.dal.dao.contact.ContactDAO;
import be.vinci.pae.utils.Utils;
import be.vinci.pae.utils.exceptions.BusinessException;
import be.vinci.pae.utils.exceptions.BusinessExceptionStatus;
import jakarta.inject.Inject;
import java.util.List;

/**
 * Implémentation de l'interface CompanyUCC fournissant des opérations liées à la gestion des
 * entreprises.
 */
public class CompanyUCCImpl implements CompanyUCC {

  @Inject
  private CompanyDAO myCompanyDAO;
  @Inject
  private ContactDAO myContactDAO;
  @Inject
  private DALServiceUCC myDALService;

  /**
   * {@inheritDoc}
   *
   * @throws BusinessException si l'entreprise est introuvable.
   */
  @Override
  public CompanyDTO getCompanyById(int id) {
    CompanyDTO companyDTO;

    try {
      myDALService.start();
      companyDTO = myCompanyDAO.getOne(id);
      if (companyDTO == null) {
        throw new BusinessException("Entreprise introuvable avec cet ID.",
            BusinessExceptionStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      myDALService.rollBack();
      throw e;
    } finally {
      myDALService.commit();

    }

    return companyDTO;
  }

  /**
   * {@inheritDoc}
   *
   * @throws BusinessException si l'année académique est invalide.
   */
  @Override
  public List<CompanyDTO> getAllCompanies(String academicYear, String orderBy, boolean descOrder) {
    List<CompanyDTO> companies;

    try {
      myDALService.start();

      if (academicYear != null) {
        if (!Utils.validateAcademicYear(academicYear)) {
          throw new BusinessException("Année académique invalide.",
              BusinessExceptionStatus.REQUEST_ERROR);
        }

        companies = myCompanyDAO.getAll(academicYear, orderBy, descOrder);
      } else {
        companies = myCompanyDAO.getAll(orderBy, descOrder);
      }
    } catch (Exception e) {
      myDALService.rollBack();
      throw e;
    } finally {
      myDALService.commit();
    }

    return companies;
  }

  /**
   * {@inheritDoc}
   *
   * @throws BusinessException si une erreur métier survient pendant la récupération de
   *                           l'entreprise.
   */
  public CompanyDTO blacklistCompany(int companyId, String blacklistReason, int version) {
    CompanyDTO blacklistedCompany;
    try {
      myDALService.start();
      CompanyDTO companyDTO = myCompanyDAO.getOne(companyId, version);
      if (companyDTO == null) {
        throw new BusinessException("Entreprise introuvable.", BusinessExceptionStatus.NOT_FOUND);
      }
      if (companyDTO.isBlacklisted()) {
        throw new BusinessException("L'entreprise est déjà blacklistée.",
            BusinessExceptionStatus.REQUEST_ERROR);
      }
      myContactDAO.blacklistAllContactsOfCompany(companyId);
      blacklistedCompany = myCompanyDAO.blacklistOneCompany(companyDTO, blacklistReason);
    } catch (Exception e) {
      myDALService.rollBack();
      throw e;
    } finally {
      myDALService.commit();
    }
    return blacklistedCompany;
  }

  /**
   * {@inheritDoc}
   *
   * @throws BusinessException si une erreur métier survient pendant la récupération de
   *                           l'entreprise.
   */
  @Override
  public CompanyDTO addCompany(CompanyDTO companyDTO) {
    CompanyDTO addedCompany;

    try {
      myDALService.start();

      CompanyDTO existingCompany = myCompanyDAO.getOne(companyDTO.getName(),
          companyDTO.getDesignation());
      String email = companyDTO.getEmail();
      String phoneNumber = companyDTO.getPhoneNumber();

      if (existingCompany != null) {
        throw new BusinessException("L'entreprise existe déjà.",
            BusinessExceptionStatus.ALREADY_EXISTS);
      }

      Company company = (Company) companyDTO;

      if (email != null && !company.checkEmailValidity(email)) {
        throw new BusinessException("Email invalide.", BusinessExceptionStatus.REQUEST_ERROR);
      }

      if (phoneNumber != null && !company.checkPhoneNumberValidity(phoneNumber)) {
        throw new BusinessException("Numéro de téléphone invalide.",
            BusinessExceptionStatus.REQUEST_ERROR);
      }

      addedCompany = myCompanyDAO.insert(companyDTO);

    } catch (Exception e) {
      myDALService.rollBack();
      throw e;
    } finally {
      myDALService.commit();
    }

    return addedCompany;
  }
}
