package be.vinci.pae.business.ucc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import be.vinci.pae.business.Factory;
import be.vinci.pae.business.dto.CompanyDTO;
import be.vinci.pae.business.ucc.company.CompanyUCC;
import be.vinci.pae.business.utils.ApplicationBinderCompanyUCCTest;
import be.vinci.pae.dal.DALServiceUCC;
import be.vinci.pae.dal.dao.company.CompanyDAO;
import be.vinci.pae.dal.dao.contact.ContactDAO;
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
 * Classe test de la classe CompanyUCCImpl pour définir le bon comportement des méthodes.
 */
public class CompanyUCCImplTest {

  private static final int id = 1;
  private static CompanyDAO companyDAO;
  private static ContactDAO contactDAO;
  private static DALServiceUCC dalServiceUCC;
  private static CompanyUCC companyUCC;
  private static Factory factory;
  private final int idCompany = 5;
  private final String name = "CiDev Industries";
  private final String designation = "Brussels";
  private final String address = "Rue Vogler 78, 1030 Schaerbeek";
  private final String email = "Cidev@gmail.com";
  private final String phoneNumber = "0484536135";
  private final boolean blacklisted = false;
  private final String blacklistReason = "L'entreprise n'est pas apte a donné des stages.";
  private final int nbrStudent = 2;
  private final int version = 1;

  /**
   * Méthode de configuration pour initialiser les ressources avant d'exécuter les tests. méthode
   * lie les services en utilisant ServiceLocator et initialise les dépendances requises.
   */
  @BeforeAll
  public static void setUp() {
    ServiceLocator locator = ServiceLocatorUtilities.bind("CompanyTestLocator",
        new ApplicationBinderCompanyUCCTest());
    companyDAO = locator.getService(CompanyDAO.class);
    dalServiceUCC = locator.getService(DALServiceUCC.class);
    companyUCC = locator.getService(CompanyUCC.class);
    factory = locator.getService(Factory.class);
  }

  @BeforeEach
  public void reset() {
    Mockito.reset(companyDAO);
  }

  private CompanyDTO initCompany() {
    CompanyDTO companyDTO = factory.getCompanyDTO();
    companyDTO.setId(idCompany);
    companyDTO.setName(name);
    companyDTO.setDesignation(designation);
    companyDTO.setAddress(address);
    companyDTO.setEmail(email);
    companyDTO.setPhoneNumber(phoneNumber);
    companyDTO.setBlacklisted(blacklisted);
    companyDTO.setBlacklistReason(blacklistReason);
    companyDTO.setVersion(version);
    companyDTO.setStudentsNumber(nbrStudent);
    return companyDTO;
  }

  @Test
  @DisplayName("Company Recovery By ID - Successful")
  public void testSuccessfulGetCompanyById() {
    CompanyDTO companyDTO = factory.getCompanyDTO();
    companyDTO.setId(id);

    doNothing().when(dalServiceUCC).start();
    when(companyDAO.getOne(id)).thenReturn(companyDTO);
    doNothing().when(dalServiceUCC).commit();

    CompanyDTO companyTest = companyUCC.getCompanyById(id);

    assertEquals(companyDTO, companyTest);
  }

  @Test
  @DisplayName("Company Recovery By ID - Unsuccessful ")
  public void testUnsuccessfulGetCompanyById() {
    doNothing().when(dalServiceUCC).start();
    when(companyDAO.getOne(id)).thenReturn(null);
    doNothing().when(dalServiceUCC).rollBack();
    doNothing().when(dalServiceUCC).commit();

    assertThrows(BusinessException.class, () -> companyUCC.getCompanyById(id));
  }

  @Test
  @DisplayName("All Companies Recovery - Successful ")
  public void testSuccessfulGetAllCompanies() {
    List<CompanyDTO> companies = new ArrayList<>();
    CompanyDTO companyDTO = initCompany();
    companies.add(companyDTO);

    doNothing().when(dalServiceUCC).start();
    when(companyDAO.getAll(null, false)).thenReturn(companies);
    doNothing().when(dalServiceUCC).commit();

    List<CompanyDTO> companiesTest = companyUCC.getAllCompanies(null, null, false);

    assertEquals(companies, companiesTest);
  }

  @Test
  @DisplayName("All Companies Recovery by year - Successful ")
  public void testUnsuccessfulGetAllCompaniesByYear() {
    List<CompanyDTO> companies = new ArrayList<>();
    CompanyDTO companyDTO = initCompany();
    companies.add(companyDTO);

    doNothing().when(dalServiceUCC).start();
    when(companyDAO.getAll("2021-2022", null, false)).thenReturn(companies);
    doNothing().when(dalServiceUCC).commit();

    List<CompanyDTO> companiesTest = companyUCC.getAllCompanies("2021-2022", null, false);

    assertEquals(companies, companiesTest);
  }

  @Test
  @DisplayName("All Companies Recovery - Unsuccessful ")
  public void testUnsuccessfulGetAllCompanies() {
    doNothing().when(dalServiceUCC).start();
    doThrow(BusinessException.class).when(companyDAO).getAll("", "", false);
    doNothing().when(dalServiceUCC).rollBack();
    doNothing().when(dalServiceUCC).commit();

    assertThrows(BusinessException.class, () -> companyUCC.getAllCompanies("", "", false));
  }

  @Test
  @DisplayName("Add Company - Successful ")
  public void testSuccessfulAddCompany() {
    CompanyDTO companyDTO = initCompany();
    doNothing().when(dalServiceUCC).start();

    when(companyDAO.getOne(name, designation)).thenReturn(null);
    when(companyDAO.insert(companyDTO)).thenReturn(companyDTO);

    doNothing().when(dalServiceUCC).commit();

    CompanyDTO companyDTOAdded = companyUCC.addCompany(companyDTO);

    assertEquals(companyDTO, companyDTOAdded);
  }

  @Test
  @DisplayName("Add Company with a failure on function getOne - Unsuccessful 1")
  public void testUnsuccessfulAddCompany1() {
    CompanyDTO companyDTO = initCompany();
    doNothing().when(dalServiceUCC).start();

    when(companyDAO.getOne(name, designation)).thenReturn(companyDTO);

    doNothing().when(dalServiceUCC).rollBack();
    doNothing().when(dalServiceUCC).commit();

    assertThrows(BusinessException.class, () -> companyUCC.addCompany(companyDTO));
  }

  @Test
  @DisplayName("Add Company with a failure on function addOneCompany - Unsuccessful 2")
  public void testUnsuccessfulAddCompany2() {
    CompanyDTO companyDTO = initCompany();
    doNothing().when(dalServiceUCC).start();

    when(companyDAO.getOne(name, designation)).thenReturn(null);
    when(companyDAO.insert(companyDTO)).thenReturn(null);

    doNothing().when(dalServiceUCC).rollBack();
    doNothing().when(dalServiceUCC).commit();

    assertNull(companyUCC.addCompany(companyDTO));
  }

  @Test
  @DisplayName("Add Company with a failure on Start Error - Unsuccessful 3")
  public void testUnsuccessfulAddCompany3() {

    doThrow(FatalException.class).when(dalServiceUCC).start();

    doNothing().when(dalServiceUCC).rollBack();
    doNothing().when(dalServiceUCC).commit();

    CompanyDTO companyDTO = initCompany();

    assertThrows(FatalException.class, () -> companyUCC.addCompany(companyDTO));
  }

  @Test
  @DisplayName("Add Company with a failure on email - Unsuccessful 4")
  public void testUnsuccessfulAddCompany4() {
    CompanyDTO companyDTO = initCompany();
    companyDTO.setEmail("akd@gmail");

    doNothing().when(dalServiceUCC).start();

    when(companyDAO.getOne(name, designation)).thenReturn(null);
    when(companyDAO.insert(companyDTO)).thenReturn(companyDTO);

    doNothing().when(dalServiceUCC).commit();

    assertThrows(BusinessException.class, () -> companyUCC.addCompany(companyDTO));
  }

  @Test
  @DisplayName("Add Company with a failure on phone number - Unsuccessful 5")
  public void testUnsuccessfulAddCompany5() {
    final CompanyDTO companyDTO = initCompany();
    companyDTO.setPhoneNumber("04845361");

    doNothing().when(dalServiceUCC).start();

    when(companyDAO.getOne(name, designation)).thenReturn(null);
    when(companyDAO.insert(companyDTO)).thenReturn(companyDTO);

    doNothing().when(dalServiceUCC).commit();

    assertThrows(BusinessException.class, () -> companyUCC.addCompany(companyDTO));
  }

  @Test
  @DisplayName("Add Company - Unsuccessful 6")
  public void testUnsuccessfulAddCompany6() {
    CompanyDTO companyDTO = initCompany();
    companyDTO.setPhoneNumber(null);

    doNothing().when(dalServiceUCC).start();

    when(companyDAO.getOne(name, designation)).thenReturn(null);
    when(companyDAO.insert(companyDTO)).thenReturn(companyDTO);

    doNothing().when(dalServiceUCC).commit();

    CompanyDTO companyTest = companyUCC.addCompany(companyDTO);
    assertEquals(companyDTO, companyTest);
  }

  @Test
  @DisplayName("Add Company - Unsuccessful 7")
  public void testUnsuccessfulAddCompany7() {
    CompanyDTO companyDTO = initCompany();
    companyDTO.setEmail(null);

    doNothing().when(dalServiceUCC).start();

    when(companyDAO.getOne(name, designation)).thenReturn(null);
    when(companyDAO.insert(companyDTO)).thenReturn(companyDTO);

    doNothing().when(dalServiceUCC).commit();

    CompanyDTO companyTest = companyUCC.addCompany(companyDTO);
    assertEquals(companyDTO, companyTest);
  }

  @Test
  @DisplayName("Blacklist company by ID - Successful ")
  public void testSuccessfulBlacklistCompany() {
    doNothing().when(dalServiceUCC).start();
    doNothing().when(dalServiceUCC).commit();
    doNothing().when(dalServiceUCC).rollBack();
    CompanyDTO companyDTO = initCompany();

    when(companyDAO.getOne(id, version)).thenReturn(companyDTO);
    when(companyDAO.blacklistOneCompany(companyDTO, blacklistReason)).thenReturn(companyDTO);

    CompanyDTO blacklistedCompany = companyUCC.blacklistCompany(id, blacklistReason, version);
    companyDTO.setBlacklisted(true);
    companyDTO.setBlacklistReason(blacklistReason);

    assertNotNull(blacklistedCompany);
    assertTrue(blacklistedCompany.isBlacklisted());
    assertEquals(blacklistReason, blacklistedCompany.getBlacklistReason());
  }


  @Test
  @DisplayName("Blacklist company by ID - Company not found ")
  public void testBlacklistCompanyNotFound() {
    final CompanyDTO companyDTO = initCompany();
    doNothing().when(dalServiceUCC).start();
    doNothing().when(dalServiceUCC).commit();

    when(companyDAO.getOne(id)).thenReturn(null);

    String blacklistReason = "Fraudulent activity";

    assertNull(companyDAO.getOne(id));
    assertThrows(BusinessException.class, () -> {
      companyUCC.blacklistCompany(id, blacklistReason, companyDTO.getVersion());
    });
  }

  @Test
  @DisplayName("Blacklist company by ID - Company already blacklisted ")
  public void testBlacklistCompanyAlreadyBlacklisted() {
    doNothing().when(dalServiceUCC).start();
    doNothing().when(dalServiceUCC).commit();

    CompanyDTO companyDTO = initCompany();
    companyDTO.setBlacklisted(true);

    when(companyDAO.getOne(id, version)).thenReturn(companyDTO);

    String blacklistReason = "Fraudulent activity";

    assertThrows(BusinessException.class, () -> {
      companyUCC.blacklistCompany(id, blacklistReason, version);
    });
  }


}
