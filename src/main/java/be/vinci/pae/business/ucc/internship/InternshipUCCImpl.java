package be.vinci.pae.business.ucc.internship;

import be.vinci.pae.business.domain.internship.Internship;
import be.vinci.pae.business.domain.supervisor.Supervisor;
import be.vinci.pae.business.dto.InternshipDTO;
import be.vinci.pae.business.dto.SupervisorDTO;
import be.vinci.pae.dal.DALServiceUCC;
import be.vinci.pae.dal.dao.internship.InternshipDAO;
import be.vinci.pae.dal.dao.supervisor.SupervisorDAO;
import be.vinci.pae.utils.Utils;
import be.vinci.pae.utils.exceptions.BusinessException;
import be.vinci.pae.utils.exceptions.BusinessExceptionStatus;
import jakarta.inject.Inject;
import java.util.List;
import org.junit.platform.commons.util.StringUtils;

/**
 * Implémentation de l'interface InternshipUCC fournissant des opérations liées à la gestion des
 * stages.
 */
public class InternshipUCCImpl implements InternshipUCC {

  @Inject
  private DALServiceUCC myDALServiceUCC;
  @Inject
  private InternshipDAO myInternshipDAO;
  @Inject
  private SupervisorDAO mySupervisorDAO;

  /**
   * {@inheritDoc}
   */
  @Override
  public List<InternshipDTO> getAllInternships() {
    List<InternshipDTO> internships;

    try {
      myDALServiceUCC.start();
      internships = myInternshipDAO.getAll();
    } catch (Exception e) {
      myDALServiceUCC.rollBack();
      throw e;
    } finally {
      myDALServiceUCC.commit();
    }

    return internships;
  }

  /**
   * {@inheritDoc}
   *
   * @throws BusinessException si le stage est introuvable.
   */
  @Override
  public InternshipDTO getInternshipByUserId(int id) {
    InternshipDTO internship;

    try {
      myDALServiceUCC.start();
      internship = myInternshipDAO.getOneByUserId(id);

      if (internship == null) {
        throw new BusinessException("Stage introuvable.", BusinessExceptionStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      myDALServiceUCC.rollBack();
      throw e;
    } finally {
      myDALServiceUCC.commit();
    }

    return internship;
  }

  /**
   * {@inheritDoc}
   *
   * @throws BusinessException si une erreur métier survient.
   */
  @Override
  public InternshipDTO addInternship(InternshipDTO internshipDTO) {
    InternshipDTO internshipDTOAdded;
    try {
      myDALServiceUCC.start();

      SupervisorDTO supervisorDTO = mySupervisorDAO.getOne(internshipDTO.getSupervisorId());
      if (supervisorDTO == null) {
        throw new BusinessException("Responsable de stage introuvable.",
            BusinessExceptionStatus.NOT_FOUND);
      }

      Supervisor supervisor = (Supervisor) supervisorDTO;
      if (!supervisor.isASupervisorOfTheCompany(internshipDTO.getContactDTO().getCompanyId())) {
        throw new BusinessException("Le responsable ne fait pas partie de l'entreprise du stage.",
            BusinessExceptionStatus.NOT_FOUND);
      }

      Internship internship = (Internship) internshipDTO;
      if (!internship.checkSignatureDateValidity(internshipDTO.getSignatureDate())) {
        throw new BusinessException("Date de signature invalide.",
            BusinessExceptionStatus.REQUEST_ERROR);
      }

      internshipDTOAdded = myInternshipDAO.insert(internshipDTO);

    } catch (Exception e) {
      myDALServiceUCC.rollBack();
      throw e;
    } finally {
      myDALServiceUCC.commit();
    }
    return internshipDTOAdded;
  }

  /**
   * {@inheritDoc}
   *
   * @throws BusinessException si une erreur métier survient.
   */
  @Override
  public InternshipDTO modifyInternshipSubject(int id, String subject, int version) {
    try {
      myDALServiceUCC.start();

      if (id < 0 || version < 0) {
        throw new BusinessException("ID et version du stage invalides.",
            BusinessExceptionStatus.REQUEST_ERROR);
      }

      if (StringUtils.isBlank(subject) || subject.length() < 3) {
        throw new BusinessException(
            "Sujet de stage invalide ou manquant.",
            BusinessExceptionStatus.REQUEST_ERROR);
      }
      InternshipDTO internshipDTO = myInternshipDAO.modifyOneInternshipSubject(id, subject,
          version);

      if (internshipDTO == null) {
        throw new BusinessException("Stage introuvable.", BusinessExceptionStatus.NOT_FOUND);
      }

      return internshipDTO;
    } catch (Exception e) {
      myDALServiceUCC.rollBack();
      throw e;
    } finally {
      myDALServiceUCC.commit();
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws BusinessException si l'année académique est invalide.
   */
  @Override
  public int nbrOfStudentsWithInternship(String year) {
    int countOfStudentsWithInternship;

    try {
      myDALServiceUCC.start();

      if (!Utils.validateAcademicYear(year)) {
        throw new BusinessException("Année académique invalide.",
            BusinessExceptionStatus.REQUEST_ERROR);
      }
      countOfStudentsWithInternship = myInternshipDAO.getCountOfAllInternships(year);
    } catch (Exception e) {
      myDALServiceUCC.rollBack();
      throw e;
    } finally {
      myDALServiceUCC.commit();
    }

    return countOfStudentsWithInternship;
  }

  /**
   * {@inheritDoc}
   *
   * @throws BusinessException si l'année académique est invalide.
   */
  public int nbrOfStudentsWithoutInternship(String year) {
    int countOfStudentsWithoutInternship;

    try {
      myDALServiceUCC.start();

      if (!Utils.validateAcademicYear(year)) {
        throw new BusinessException("Année académique invalide.",
            BusinessExceptionStatus.REQUEST_ERROR);
      }
      countOfStudentsWithoutInternship = myInternshipDAO.getCountOfStudentsWithoutInternship(year);
    } catch (Exception e) {
      myDALServiceUCC.rollBack();
      throw e;
    } finally {
      myDALServiceUCC.commit();
    }

    return countOfStudentsWithoutInternship;
  }

}

