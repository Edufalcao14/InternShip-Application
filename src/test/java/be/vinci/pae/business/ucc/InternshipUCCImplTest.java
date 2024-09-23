package be.vinci.pae.business.ucc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import be.vinci.pae.business.Factory;
import be.vinci.pae.business.dto.ContactDTO;
import be.vinci.pae.business.dto.InternshipDTO;
import be.vinci.pae.business.dto.SupervisorDTO;
import be.vinci.pae.business.ucc.internship.InternshipUCC;
import be.vinci.pae.business.utils.ApplicationBinderInternshipUCCTest;
import be.vinci.pae.dal.DALServiceUCC;
import be.vinci.pae.dal.dao.internship.InternshipDAO;
import be.vinci.pae.dal.dao.supervisor.SupervisorDAO;
import be.vinci.pae.utils.exceptions.BusinessException;
import be.vinci.pae.utils.exceptions.BusinessExceptionStatus;
import java.util.ArrayList;
import java.util.List;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Classe test de la classe InternshipUCCImpl pour définir le bon comportement des méthodes.
 */
public class InternshipUCCImplTest {

  private static Factory factory;
  private static InternshipDAO internshipDAO;
  private static SupervisorDAO supervisorDAO;
  private static InternshipUCC internshipUCC;
  private static DALServiceUCC dalServiceUCC;
  private final int userId = 1;
  private final int companyId = 1;
  private final int supervisorId = 1;
  private final String year = "2023-2024";


  /**
   * Méthode de configuration pour initialiser les ressources avant d'exécuter les tests. La méthode
   * lie les services en utilisant ServiceLocator et initialise les dépendances requises.
   */
  @BeforeAll
  public static void setUp() {
    ServiceLocator serviceLocator = ServiceLocatorUtilities.bind("InternshipTestLocator",
        new ApplicationBinderInternshipUCCTest());
    factory = serviceLocator.getService(Factory.class);
    dalServiceUCC = serviceLocator.getService(DALServiceUCC.class);
    internshipDAO = serviceLocator.getService(InternshipDAO.class);
    internshipUCC = serviceLocator.getService(InternshipUCC.class);
    supervisorDAO = serviceLocator.getService(SupervisorDAO.class);
  }

  @BeforeEach
  public void reset() {
    Mockito.reset(supervisorDAO, internshipDAO);
  }

  /**
   * Méthode pour initier un Mock de InternshipDTO.
   *
   * @return un Mock de InternshipDTO.
   */
  private InternshipDTO initInternship() {
    InternshipDTO internshipDTO = factory.getInternshipDTO();
    internshipDTO.setId(1);
    internshipDTO.setStudentId(userId);
    internshipDTO.setSupervisorId(supervisorId);
    String signatureDate = "1999-12-31";
    internshipDTO.setSignatureDate(signatureDate);
    ContactDTO contactDTO = factory.getContactDTO();
    contactDTO.setCompanyId(companyId);
    internshipDTO.setContactDTO(contactDTO);
    return internshipDTO;
  }

  /**
   * Méthode pour initier un Mock de SupervisorDTO.
   *
   * @return un Mock de SupervisorDTO.
   */
  private SupervisorDTO initSupervisor() {
    SupervisorDTO supervisorDTO = factory.getSupervisorDTO();
    supervisorDTO.setId(supervisorId);
    supervisorDTO.setCompanyId(companyId);
    return supervisorDTO;
  }

  @Test
  @DisplayName("Get All Internships - Successful")
  public void testSuccessfulGetAllInternships() {
    doNothing().when(dalServiceUCC).start();
    List<InternshipDTO> internships = new ArrayList<>();
    internships.add(initInternship());
    when(internshipDAO.getAll()).thenReturn(internships);
    doNothing().when(dalServiceUCC).commit();

    List<InternshipDTO> result = internshipUCC.getAllInternships();

    assertEquals(internships, result);
  }

  @Test
  @DisplayName("Get All Internships - Unsuccessful - Start Exception")
  public void testGetAllInternshipsException() {
    doThrow(RuntimeException.class).when(dalServiceUCC).start();
    doNothing().when(dalServiceUCC).commit();
    doNothing().when(dalServiceUCC).rollBack();

    assertThrows(RuntimeException.class, () -> internshipUCC.getAllInternships());
  }

  @Test
  @DisplayName("Get Internship By User Id - Successful")
  public void testSuccessfulGetInternshipByUserId() {
    InternshipDTO internship = initInternship();
    doNothing().when(dalServiceUCC).start();
    when(internshipDAO.getOneByUserId(userId)).thenReturn(internship);
    doNothing().when(dalServiceUCC).commit();

    InternshipDTO result = internshipUCC.getInternshipByUserId(userId);

    assertEquals(internship, result);
  }

  @Test
  @DisplayName("Get Internship By User Id - Unsuccessful - User Not Found")
  public void testGetInternshipByUserIdNotFound() {
    doNothing().when(dalServiceUCC).start();
    when(internshipDAO.getOneByUserId(userId)).thenReturn(null);
    doNothing().when(dalServiceUCC).commit();
    doNothing().when(dalServiceUCC).rollBack();

    assertThrows(RuntimeException.class, () -> internshipUCC.getInternshipByUserId(userId));
  }

  @Test
  @DisplayName("Get Internship By User Id - Unsuccessful - Start Exception")
  public void testGetInternshipByUserIdException() {
    doThrow(RuntimeException.class).when(dalServiceUCC).start();

    assertThrows(RuntimeException.class, () -> internshipUCC.getInternshipByUserId(userId));
  }

  @Test
  @DisplayName("Add Internship - Successful")
  public void testSuccessfulAddInternship() {
    doNothing().when(dalServiceUCC).start();
    doNothing().when(dalServiceUCC).commit();

    SupervisorDTO supervisorDTO = initSupervisor();
    InternshipDTO internshipDTO = initInternship();

    when(supervisorDAO.getOne(supervisorId)).thenReturn(supervisorDTO);
    when(internshipDAO.insert(internshipDTO)).thenReturn(internshipDTO);

    InternshipDTO internshipTest = internshipUCC.addInternship(internshipDTO);

    assertEquals(internshipDTO, internshipTest);
  }

  @Test
  @DisplayName("Add Internship - Unsuccessful : Supervisor Not Found")
  public void testUnsuccessfulAddInternshipSupervisorNotFound() {
    doNothing().when(dalServiceUCC).start();
    doNothing().when(dalServiceUCC).commit();
    doNothing().when(dalServiceUCC).rollBack();

    when(supervisorDAO.getOne(supervisorId)).thenReturn(null);

    assertThrows(BusinessException.class, () -> internshipUCC.addInternship(initInternship()));
  }

  @Test
  @DisplayName("Add Internship - Unsuccessful : Unrelated Supervisor with Company")
  public void testUnsuccessfulAddInternshipUnrelatedSupervisor() {
    doNothing().when(dalServiceUCC).start();
    doNothing().when(dalServiceUCC).commit();
    doNothing().when(dalServiceUCC).rollBack();

    SupervisorDTO supervisorDTO = initSupervisor();
    supervisorDTO.setCompanyId(999);

    when(supervisorDAO.getOne(supervisorId)).thenReturn(supervisorDTO);

    assertThrows(BusinessException.class, () -> internshipUCC.addInternship(initInternship()));
  }

  @Test
  @DisplayName("Add Internship - Unsuccessful : Nonconforming Signature Date")
  public void testUnsuccessfulAddInternshipNonconformingSignatureDate() {
    doNothing().when(dalServiceUCC).start();
    doNothing().when(dalServiceUCC).commit();
    doNothing().when(dalServiceUCC).rollBack();

    SupervisorDTO supervisorDTO = initSupervisor();

    when(supervisorDAO.getOne(supervisorId)).thenReturn(supervisorDTO);

    InternshipDTO internshipDTO = initInternship();
    internshipDTO.setSignatureDate("9999");

    assertThrows(BusinessException.class, () -> internshipUCC.addInternship(internshipDTO));
  }


  @Test
  @DisplayName("Modify Internship Subject Internship - Successful")
  public void testMmodifyInternshipSubjectSuccessful() {
    doNothing().when(dalServiceUCC).start();
    doNothing().when(dalServiceUCC).commit();
    doNothing().when(dalServiceUCC).rollBack();

    InternshipDTO internshipDTO = initInternship();
    String newSubject = "Stage Java";
    internshipDTO.setSubject(newSubject);
    when(internshipDAO.modifyOneInternshipSubject(internshipDTO.getId(), newSubject,
        internshipDTO.getVersion())).thenReturn(internshipDTO);

    assertEquals(internshipDTO,
        internshipUCC.modifyInternshipSubject(internshipDTO.getId(), newSubject,
            internshipDTO.getVersion()));
  }

  @Test
  @DisplayName("Modify Internship Subject Internship - Unsuccessful : DTO NULL")
  public void testMmodifyInternshipSubjectUnsuccessfulDTONull() {
    doNothing().when(dalServiceUCC).start();
    doNothing().when(dalServiceUCC).commit();
    doNothing().when(dalServiceUCC).rollBack();

    InternshipDTO internshipDTO = initInternship();
    String newSubject = "Stage Java";
    internshipDTO.setSubject(newSubject);
    when(internshipDAO.modifyOneInternshipSubject(internshipDTO.getId(), newSubject,
        internshipDTO.getVersion())).thenReturn(null);

    BusinessException exception = assertThrows(BusinessException.class, () -> {
      internshipUCC.modifyInternshipSubject(internshipDTO.getId(), newSubject,
          internshipDTO.getVersion());
    });

    // Assert the exception message and status
    assertEquals(BusinessExceptionStatus.NOT_FOUND, exception.getStatus());
  }

  @Test
  @DisplayName("Modify Internship Subject Internship - Unsuccessful : Invalid ID")
  public void testMmodifyInternshipSubjectUnsuccessfulInvalidId() {
    doNothing().when(dalServiceUCC).start();
    doNothing().when(dalServiceUCC).commit();
    doNothing().when(dalServiceUCC).rollBack();

    InternshipDTO internshipDTO = initInternship();
    String newSubject = "Stage Java";
    internshipDTO.setSubject(newSubject);
    when(internshipDAO.modifyOneInternshipSubject(-1, newSubject,
        internshipDTO.getVersion())).thenReturn(null);

    BusinessException exception = assertThrows(BusinessException.class, () -> {
      internshipUCC.modifyInternshipSubject(-1, newSubject,
          internshipDTO.getVersion());
    });

    // Assert the exception message and status
    assertEquals(BusinessExceptionStatus.REQUEST_ERROR, exception.getStatus());
  }

  @Test
  @DisplayName("Modify Internship Subject Internship - Unsuccessful : Invalid Version")
  public void testMmodifyInternshipSubjectUnsuccessfulInvalidVersion() {
    doNothing().when(dalServiceUCC).start();
    doNothing().when(dalServiceUCC).commit();
    doNothing().when(dalServiceUCC).rollBack();

    InternshipDTO internshipDTO = initInternship();
    String newSubject = "Stage Java";
    internshipDTO.setSubject(newSubject);
    when(
        internshipDAO.modifyOneInternshipSubject(internshipDTO.getId(), newSubject, -1)).thenReturn(
        null);

    BusinessException exception = assertThrows(BusinessException.class, () -> {
      internshipUCC.modifyInternshipSubject(internshipDTO.getId(), newSubject, -1);
    });

    // Assert the exception message and status
    assertEquals(BusinessExceptionStatus.REQUEST_ERROR, exception.getStatus());
  }

  @Test
  @DisplayName("Modify Internship Subject Internship - Unsuccessful : Empty Subject")
  public void testMmodifyInternshipSubjectUnsuccessfulEmptySubject() {
    doNothing().when(dalServiceUCC).start();
    doNothing().when(dalServiceUCC).commit();
    doNothing().when(dalServiceUCC).rollBack();

    InternshipDTO internshipDTO = initInternship();
    String newSubject = " ";
    internshipDTO.setSubject(newSubject);
    when(
        internshipDAO.modifyOneInternshipSubject(internshipDTO.getId(), newSubject,
            internshipDTO.getVersion())).thenReturn(
        null);

    BusinessException exception = assertThrows(BusinessException.class, () -> {
      internshipUCC.modifyInternshipSubject(internshipDTO.getId(), newSubject,
          internshipDTO.getVersion());
    });

    // Assert the exception message and status
    assertEquals(BusinessExceptionStatus.REQUEST_ERROR, exception.getStatus());
  }

  @Test
  @DisplayName("Modify Internship Subject Internship - Unsuccessful : Invalid Subject")
  public void testMmodifyInternshipSubjectUnsuccessfulInvalidSubject() {
    doNothing().when(dalServiceUCC).start();
    doNothing().when(dalServiceUCC).commit();
    doNothing().when(dalServiceUCC).rollBack();

    InternshipDTO internshipDTO = initInternship();
    String newSubject = "ae";
    internshipDTO.setSubject(newSubject);
    when(
        internshipDAO.modifyOneInternshipSubject(internshipDTO.getId(), newSubject,
            internshipDTO.getVersion())).thenReturn(
        null);

    BusinessException exception = assertThrows(BusinessException.class, () -> {
      internshipUCC.modifyInternshipSubject(internshipDTO.getId(), newSubject,
          internshipDTO.getVersion());
    });

    // Assert the exception message and status
    assertEquals(BusinessExceptionStatus.REQUEST_ERROR, exception.getStatus());
  }

  @Test
  @DisplayName("Modify Internship Subject Internship - Unsuccessful : Invalid Subject")
  public void testMmodifyInternshipSubjectUnsuccessfulSmallSubject() {
    doNothing().when(dalServiceUCC).start();
    doNothing().when(dalServiceUCC).commit();
    doNothing().when(dalServiceUCC).rollBack();

    InternshipDTO internshipDTO = initInternship();
    String newSubject = "a";
    internshipDTO.setSubject(newSubject);
    when(
        internshipDAO.modifyOneInternshipSubject(internshipDTO.getId(), newSubject,
            internshipDTO.getVersion())).thenReturn(
        null);

    BusinessException exception = assertThrows(BusinessException.class, () -> {
      internshipUCC.modifyInternshipSubject(internshipDTO.getId(), newSubject,
          internshipDTO.getVersion());
    });

    // Assert the exception message and status
    assertEquals(BusinessExceptionStatus.REQUEST_ERROR, exception.getStatus());
  }

  @Test
  @DisplayName("Modify Internship Subject Internship - Unsuccessful : Null Subject")
  public void testMmodifyInternshipSubjectUnsuccessfulNullSubject() {
    doNothing().when(dalServiceUCC).start();
    doNothing().when(dalServiceUCC).commit();
    doNothing().when(dalServiceUCC).rollBack();

    InternshipDTO internshipDTO = initInternship();
    String newSubject = null;
    internshipDTO.setSubject(newSubject);
    when(
        internshipDAO.modifyOneInternshipSubject(internshipDTO.getId(), newSubject,
            internshipDTO.getVersion())).thenReturn(
        null);

    BusinessException exception = assertThrows(BusinessException.class, () -> {
      internshipUCC.modifyInternshipSubject(internshipDTO.getId(), newSubject,
          internshipDTO.getVersion());
    });

    // Assert the exception message and status
    assertEquals(BusinessExceptionStatus.REQUEST_ERROR, exception.getStatus());
  }

  @Test
  @DisplayName("test getNbrOfStudentsWithInternship - Successful")
  public void testNbrOfStudentsWithInternship() {
    doNothing().when(dalServiceUCC).start();
    doNothing().when(dalServiceUCC).commit();
    doNothing().when(dalServiceUCC).rollBack();

    when(internshipDAO.getCountOfAllInternships(year)).thenReturn(5);

    assertEquals(5, internshipUCC.nbrOfStudentsWithInternship(year));
  }

  @Test
  @DisplayName("test getNbrOfStudentsWithInternship - Unsuccessful not valid year")
  public void testNbrOfStudentsWithInternshipFail() {
    doNothing().when(dalServiceUCC).start();
    doNothing().when(dalServiceUCC).commit();
    doNothing().when(dalServiceUCC).rollBack();

    BusinessException exception = assertThrows(BusinessException.class, () -> {
      internshipUCC.nbrOfStudentsWithInternship("2023");
    });

    // Assert the exception message and status
    assertEquals(BusinessExceptionStatus.REQUEST_ERROR, exception.getStatus());
  }

  @Test
  @DisplayName("test getNbrOfStudentsWithoutInternship - Successful")
  public void testNbrOfStudentsWithoutInternship() {
    doNothing().when(dalServiceUCC).start();
    doNothing().when(dalServiceUCC).commit();
    doNothing().when(dalServiceUCC).rollBack();

    when(internshipDAO.getCountOfStudentsWithoutInternship(year)).thenReturn(5);

    assertEquals(5, internshipUCC.nbrOfStudentsWithoutInternship(year));
  }

  @Test
  @DisplayName("test getNbrOfStudentsWithoutInternship - Unsuccessful not valid year")
  public void testNbrOfStudentsWithoutInternshipFail() {
    doNothing().when(dalServiceUCC).start();
    doNothing().when(dalServiceUCC).commit();
    doNothing().when(dalServiceUCC).rollBack();

    BusinessException exception = assertThrows(BusinessException.class, () -> {
      internshipUCC.nbrOfStudentsWithoutInternship("2023");
    });

    assertEquals(BusinessExceptionStatus.REQUEST_ERROR, exception.getStatus());
  }

}
