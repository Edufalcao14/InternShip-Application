package be.vinci.pae.business.ucc.supervisor;

import be.vinci.pae.business.domain.supervisor.Supervisor;
import be.vinci.pae.business.dto.SupervisorDTO;
import be.vinci.pae.dal.DALServiceUCC;
import be.vinci.pae.dal.dao.company.CompanyDAO;
import be.vinci.pae.dal.dao.supervisor.SupervisorDAO;
import be.vinci.pae.utils.exceptions.BusinessException;
import be.vinci.pae.utils.exceptions.BusinessExceptionStatus;
import jakarta.inject.Inject;
import java.util.List;

/**
 * Implémentation de l'interface SupervisorUCC fournissant des opérations liées à la gestion des
 * responsables de stage.
 */
public class SupervisorUCCImpl implements SupervisorUCC {

  @Inject
  private CompanyDAO myCompanyDAO;
  @Inject
  private DALServiceUCC myDALService;
  @Inject
  private SupervisorDAO mySupervisorDAO;

  /**
   * {@inheritDoc}
   */
  @Override
  public List<SupervisorDTO> getAllSupervisors() {
    List<SupervisorDTO> supervisors;

    try {
      myDALService.start();

      supervisors = mySupervisorDAO.getAll();
    } catch (Exception e) {
      myDALService.rollBack();
      throw e;
    } finally {
      myDALService.commit();
    }

    return supervisors;
  }

  /**
   * {@inheritDoc}
   *
   * @throws BusinessException si l'entreprise est introuvable.
   */
  @Override
  public List<SupervisorDTO> getCompanySupervisors(int companyId) {
    List<SupervisorDTO> supervisors;
    try {
      myDALService.start();

      if (myCompanyDAO.getOne(companyId) == null) {
        throw new BusinessException("Entreprise introuvable.", BusinessExceptionStatus.NOT_FOUND);
      }
      supervisors = mySupervisorDAO.getAllByCompanyId(companyId);
    } catch (Exception e) {
      myDALService.rollBack();
      throw e;
    } finally {
      myDALService.commit();
    }

    return supervisors;
  }

  /**
   * {@inheritDoc}
   *
   * @throws BusinessException si une erreur métier survient.
   */
  @Override
  public SupervisorDTO addSupervisor(SupervisorDTO supervisorDTO, int companyId) {
    SupervisorDTO addedSupervisor;
    try {
      myDALService.start();

      if (myCompanyDAO.getOne(companyId) == null) {
        throw new BusinessException("Entreprise introuvable.", BusinessExceptionStatus.NOT_FOUND);
      }

      Supervisor supervisor = (Supervisor) supervisorDTO;

      if (!supervisor.checkPhoneNumberValidity(supervisorDTO.getPhoneNumber())) {
        throw new BusinessException("Numéro de téléphone invalide.",
            BusinessExceptionStatus.REQUEST_ERROR);
      }

      supervisor.setCompanyId(companyId);

      if (mySupervisorDAO.getOneByCompany(supervisorDTO) != null) {
        throw new BusinessException(
            "Un responsable existe déjà avec ce numéro de téléphone pour cette entreprise.",
            BusinessExceptionStatus.ALREADY_EXISTS);
      }

      if (supervisorDTO.getEmail() != null && !supervisor.checkEmailValidity(
          supervisorDTO.getEmail())) {
        throw new BusinessException("Email invalide.", BusinessExceptionStatus.REQUEST_ERROR);
      }

      addedSupervisor = mySupervisorDAO.insert(supervisorDTO);
    } catch (Exception e) {
      myDALService.rollBack();
      throw e;
    } finally {
      myDALService.commit();
    }
    return addedSupervisor;
  }
}
