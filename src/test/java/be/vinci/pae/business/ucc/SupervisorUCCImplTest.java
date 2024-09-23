package be.vinci.pae.business.ucc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import be.vinci.pae.business.Factory;
import be.vinci.pae.business.dto.CompanyDTO;
import be.vinci.pae.business.dto.SupervisorDTO;
import be.vinci.pae.business.ucc.supervisor.SupervisorUCC;
import be.vinci.pae.business.utils.ApplicationBinderSupervisorUCCTest;
import be.vinci.pae.dal.DALServiceUCC;
import be.vinci.pae.dal.dao.company.CompanyDAO;
import be.vinci.pae.dal.dao.supervisor.SupervisorDAO;
import be.vinci.pae.utils.exceptions.BusinessException;
import be.vinci.pae.utils.exceptions.FatalException;
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
 * Classe test de la classe SupervisorUCCImpl pour définir le bon comportement des méthodes.
 */
public class SupervisorUCCImplTest {

  private static Factory factory;
  private static SupervisorDAO supervisorDAO;
  private static SupervisorUCC supervisorUCC;
  private static CompanyDAO companyDAO;
  private static DALServiceUCC dalServiceUCC;
  private final int companyId = 1;
  private final CompanyDTO companyDTO = factory.getCompanyDTO();

  /**
   * Méthode de configuration pour initialiser les ressources avant d'exécuter les tests. La méthode
   * lie les services en utilisant ServiceLocator et initialise les dépendances requises.
   */
  @BeforeAll
  public static void setUp() {
    ServiceLocator locator = ServiceLocatorUtilities.bind("SupervisorTestLocator",
        new ApplicationBinderSupervisorUCCTest());
    factory = locator.getService(Factory.class);
    supervisorDAO = locator.getService(SupervisorDAO.class);
    companyDAO = locator.getService(CompanyDAO.class);
    supervisorUCC = locator.getService(SupervisorUCC.class);
    dalServiceUCC = locator.getService(DALServiceUCC.class);
  }

  @BeforeEach
  public void reset() {
    Mockito.reset(supervisorDAO, companyDAO);
  }

  private SupervisorDTO initSupervisor() {
    SupervisorDTO supervisorDTO = factory.getSupervisorDTO();
    supervisorDTO.setCompanyId(companyId);
    supervisorDTO.setEmail("grand.schtroumpf@gmail.com");
    supervisorDTO.setPhoneNumber("0123456789");
    return supervisorDTO;
  }

  @Test
  @DisplayName("Get All Supervisors - Successful")
  public void testSuccessfulGetAllSupervisors() {
    doNothing().when(dalServiceUCC).start();

    List<SupervisorDTO> supervisors = new ArrayList<>();
    for (int i = 0; i < 2; i++) {
      supervisors.add(factory.getSupervisorDTO());
    }
    when(supervisorDAO.getAll()).thenReturn(supervisors);

    doNothing().when(dalServiceUCC).commit();

    List<SupervisorDTO> supervisorsTest = supervisorUCC.getAllSupervisors();

    assertEquals(supervisors, supervisorsTest);
  }

  @Test
  @DisplayName("Get All Supervisors - Unsuccessful : DAL Error")
  public void testUnsuccessfulGetAllSupervisorsDAOError() {
    doNothing().when(dalServiceUCC).start();
    doThrow(FatalException.class).when(dalServiceUCC).start();
    doNothing().when(dalServiceUCC).rollBack();
    doNothing().when(dalServiceUCC).commit();

    assertThrows(FatalException.class, () -> supervisorUCC.getAllSupervisors());
  }

  @Test
  @DisplayName("Add Supervisor - Successful")
  public void testSuccessfulAddSupervisor() {
    doNothing().when(dalServiceUCC).start();
    doNothing().when(dalServiceUCC).commit();

    when(companyDAO.getOne(companyId)).thenReturn(companyDTO);

    SupervisorDTO supervisor = initSupervisor();
    when(supervisorDAO.getOneByCompany(supervisor)).thenReturn(null);
    when(supervisorDAO.insert(supervisor)).thenReturn(supervisor);

    SupervisorDTO supervisorTest = supervisorUCC.addSupervisor(supervisor, companyId);

    assertEquals(supervisor, supervisorTest);
  }

  @Test
  @DisplayName("Add Supervisor - Successful (No Email)")
  public void testSuccessfulAddSupervisorNoEmail() {
    doNothing().when(dalServiceUCC).start();
    doNothing().when(dalServiceUCC).commit();

    when(companyDAO.getOne(companyId)).thenReturn(companyDTO);

    SupervisorDTO supervisor = initSupervisor();
    supervisor.setEmail(null);
    when(supervisorDAO.getOneByCompany(supervisor)).thenReturn(null);
    when(supervisorDAO.insert(supervisor)).thenReturn(supervisor);

    SupervisorDTO supervisorTest = supervisorUCC.addSupervisor(supervisor, companyId);

    assertEquals(supervisor, supervisorTest);
  }

  @Test
  @DisplayName("Add Supervisor - Unsuccessful : Supervisor Already Exists")
  public void testUnsuccessfulAddSupervisorAlreadyExists() {
    doNothing().when(dalServiceUCC).start();
    doNothing().when(dalServiceUCC).rollBack();
    doNothing().when(dalServiceUCC).commit();

    when(companyDAO.getOne(companyId)).thenReturn(companyDTO);

    SupervisorDTO supervisor = initSupervisor();
    when(supervisorDAO.getOneByCompany(supervisor)).thenReturn(supervisor);

    assertThrows(BusinessException.class, () -> supervisorUCC.addSupervisor(supervisor, companyId));
  }

  @Test
  @DisplayName("Add Supervisor - Unsuccessful : Company Not Found")
  public void testUnsuccessfulAddSupervisorCompanyNotFound() {
    doNothing().when(dalServiceUCC).start();
    doNothing().when(dalServiceUCC).rollBack();
    doNothing().when(dalServiceUCC).commit();

    when(companyDAO.getOne(companyId)).thenReturn(null);

    assertThrows(BusinessException.class,
        () -> supervisorUCC.addSupervisor(initSupervisor(), companyId));
  }

  @Test
  @DisplayName("Add Supervisor - Unsuccessful : Nonconforming Phone Number")
  public void testUnsuccessfulAddSupervisorNonconformingPhoneNumber() {
    doNothing().when(dalServiceUCC).start();
    doNothing().when(dalServiceUCC).rollBack();
    doNothing().when(dalServiceUCC).commit();

    when(companyDAO.getOne(companyId)).thenReturn(companyDTO);

    SupervisorDTO supervisor = initSupervisor();
    supervisor.setPhoneNumber("01234");

    assertThrows(BusinessException.class, () -> supervisorUCC.addSupervisor(supervisor, companyId));
  }

  @Test
  @DisplayName("Add Supervisor - Unsuccessful : Nonconforming Email")
  public void testUnsuccessfulAddSupervisorNonconformingEmail() {
    doNothing().when(dalServiceUCC).start();
    doNothing().when(dalServiceUCC).rollBack();
    doNothing().when(dalServiceUCC).commit();

    when(companyDAO.getOne(companyId)).thenReturn(companyDTO);

    SupervisorDTO supervisor = initSupervisor();
    when(supervisorDAO.getOneByCompany(supervisor)).thenReturn(null);
    supervisor.setEmail("azraël");

    assertThrows(BusinessException.class, () -> supervisorUCC.addSupervisor(supervisor, companyId));
  }

  @Test
  @DisplayName("Get Company Supervisors - Successful")
  public void testGetCompanySupervisorsSuccessful() {
    doNothing().when(dalServiceUCC).start();
    doNothing().when(dalServiceUCC).commit();

    when(companyDAO.getOne(companyId)).thenReturn(companyDTO);

    List<SupervisorDTO> supervisors = new ArrayList<>();
    for (int i = 0; i < 2; i++) {
      supervisors.add(factory.getSupervisorDTO());
    }

    when(supervisorDAO.getAllByCompanyId(companyId)).thenReturn(supervisors);

    List<SupervisorDTO> supervisorsTest = supervisorUCC.getCompanySupervisors(companyId);

    assertEquals(supervisors, supervisorsTest);
  }

  @Test
  @DisplayName("Get Company Supervisors - Unsuccessful : Company Not Found")
  public void testGetCompanySupervisorsUnsuccessfulCompanyNotFound() {
    doNothing().when(dalServiceUCC).start();
    doNothing().when(dalServiceUCC).rollBack();
    doNothing().when(dalServiceUCC).commit();

    when(companyDAO.getOne(companyId)).thenReturn(null);

    assertThrows(BusinessException.class, () -> supervisorUCC.getCompanySupervisors(companyId));
  }
}