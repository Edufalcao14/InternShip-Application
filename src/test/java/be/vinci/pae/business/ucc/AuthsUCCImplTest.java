package be.vinci.pae.business.ucc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import be.vinci.pae.business.Factory;
import be.vinci.pae.business.dto.UserDTO;
import be.vinci.pae.business.ucc.auths.AuthsUCC;
import be.vinci.pae.business.utils.ApplicationBinderAuthsUCCTest;
import be.vinci.pae.dal.DALServiceUCC;
import be.vinci.pae.dal.dao.user.UserDAO;
import be.vinci.pae.utils.exceptions.BusinessException;
import be.vinci.pae.utils.exceptions.FatalException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.Mockito;

class AuthsUCCImplTest {

  private static Factory factory;
  private static UserDAO userDAO;
  private static AuthsUCC authsUCC;
  private static DALServiceUCC dalServiceUCC;
  private final String email = "gargamel.sorcier@vinci.be";
  private final String password = "schtroumpf1";
  private final String studentRole = "ETUDIANT";
  private final String professorRole = "PROFESSEUR";
  private final String adminRole = "ADMINISTRATIF";
  private final String phoneNumber = "0123456789";

  /**
   * Méthode de configuration pour initialiser les ressources avant d'exécuter les tests. méthode
   * lie les services en utilisant ServiceLocator et initialise les dépendances requises.
   */
  @BeforeAll
  public static void setUp() {
    ServiceLocator locator = ServiceLocatorUtilities.bind("AuthsTestLocator",
        new ApplicationBinderAuthsUCCTest());
    factory = locator.getService(Factory.class);
    userDAO = locator.getService(UserDAO.class);
    authsUCC = locator.getService(AuthsUCC.class);
    dalServiceUCC = locator.getService(DALServiceUCC.class);
  }

  @BeforeEach
  public void reset() {
    Mockito.reset(userDAO);
  }

  private UserDTO initUser() {
    String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
    UserDTO userDTO = factory.getUserDTO();
    userDTO.setEmail(email);
    userDTO.setPassword(hashedPassword);
    userDTO.setPhoneNumber(phoneNumber);
    return userDTO;
  }

  @Test
  @DisplayName("Login - Successful")
  public void testSuccessfulLogin() {
    UserDTO userDTO = initUser();

    doNothing().when(dalServiceUCC).start();
    when(userDAO.getOne(email)).thenReturn(userDTO);
    doNothing().when(dalServiceUCC).commit();

    UserDTO userTest = authsUCC.login(email, password);

    assertEquals(userDTO, userTest);
  }

  @Test
  @DisplayName("Login - Unsuccessful : Incorrect or Unknown Email")
  public void testUnknownOrIncorrectEmail() {
    String email = "azraël@vinci.be";

    doNothing().when(dalServiceUCC).start();
    when(userDAO.getOne(email)).thenReturn(null);
    doNothing().when(dalServiceUCC).commit();

    assertThrows(BusinessException.class, () -> authsUCC.login(email, password));
  }

  @Test
  @DisplayName("Login - Unsuccessful : Incorrect Password")
  public void testIncorrectPasswordLogin() {
    UserDTO userDTO = initUser();

    doNothing().when(dalServiceUCC).start();
    when(userDAO.getOne(email)).thenReturn(userDTO);
    doNothing().when(dalServiceUCC).commit();

    assertThrows(BusinessException.class, () -> authsUCC.login(email, "azraël"));
  }

  @Test
  @DisplayName("Login - Unsuccessful : Start Error")
  public void testStartErrorLogin() {
    doThrow(FatalException.class).when(dalServiceUCC).start();
    doNothing().when(dalServiceUCC).rollBack();
    doNothing().when(dalServiceUCC).commit();

    assertThrows(FatalException.class, () -> authsUCC.login(email, password));
  }

  @Test
  @DisplayName("Register - Successful : Professor User")
  public void testSuccessfulProfessorRegister() {
    UserDTO userDTO = initUser();

    doNothing().when(dalServiceUCC).start();
    when(userDAO.getOne(email)).thenReturn(null);
    when(userDAO.insert(userDTO)).thenReturn(userDTO);
    doNothing().when(dalServiceUCC).commit();

    UserDTO userTest = authsUCC.register(userDTO, password, professorRole);

    assertEquals(userDTO, userTest);
  }

  @Test
  @DisplayName("Register - Successful : Administrative User")
  public void testSuccessfulAdministrativeRegister() {
    UserDTO userDTO = initUser();

    doNothing().when(dalServiceUCC).start();
    when(userDAO.getOne(email)).thenReturn(null);
    when(userDAO.insert(userDTO)).thenReturn(userDTO);
    doNothing().when(dalServiceUCC).commit();

    UserDTO userTest = authsUCC.register(userDTO, password, adminRole);

    assertEquals(userDTO, userTest);
  }

  @Test
  @DisplayName("Register - Successful : Student User")
  public void testSuccessfulStudentRegister() {
    UserDTO userDTO = initUser();
    userDTO.setEmail("azraël@student.vinci.be");

    doNothing().when(dalServiceUCC).start();
    when(userDAO.getOne(email)).thenReturn(null);
    when(userDAO.insert(userDTO)).thenReturn(userDTO);
    doNothing().when(dalServiceUCC).commit();

    UserDTO userTest = authsUCC.register(userDTO, password, studentRole);

    assertEquals(userDTO, userTest);
  }

  @Test
  @DisplayName("Register - Unsuccessful : Already Registered User")
  public void testAlreadyRegisteredUserRegister() {
    UserDTO userDTO = initUser();

    doNothing().when(dalServiceUCC).start();
    when(userDAO.getOne(email)).thenReturn(userDTO);
    doNothing().when(dalServiceUCC).commit();

    assertThrows(BusinessException.class, () -> authsUCC.register(userDTO, password, studentRole));
  }

  @Test
  @DisplayName("Register - Unsuccessful : Nonconforming Email")
  public void testNonconformingEmailRegister() {
    UserDTO userDTO = factory.getUserDTO();
    userDTO.setEmail("azraël@schtroupfmail.com");

    doNothing().when(dalServiceUCC).start();
    when(userDAO.getOne(email)).thenReturn(null);
    doNothing().when(dalServiceUCC).commit();

    assertThrows(BusinessException.class, () -> authsUCC.register(userDTO, password, studentRole));
  }

  @Test
  @DisplayName("Register - Unsuccessful : Nonconforming Phone Number (length)")
  public void testNonconformingPhoneNumberARegister() {
    UserDTO userDTO = factory.getUserDTO();
    userDTO.setEmail(email);
    userDTO.setPhoneNumber("000");

    doNothing().when(dalServiceUCC).start();
    when(userDAO.getOne(email)).thenReturn(null);
    doNothing().when(dalServiceUCC).commit();

    assertThrows(BusinessException.class, () -> authsUCC.register(userDTO, password, studentRole));
  }

  @Test
  @DisplayName("Register - Unsuccessful : Nonconforming Phone Number (letter)")
  public void testNonconformingPhoneNumberBRegister() {
    UserDTO userDTO = factory.getUserDTO();
    userDTO.setEmail(email);
    userDTO.setPhoneNumber("a123456789");

    doNothing().when(dalServiceUCC).start();
    when(userDAO.getOne(email)).thenReturn(null);
    doNothing().when(dalServiceUCC).commit();

    assertThrows(BusinessException.class, () -> authsUCC.register(userDTO, password, studentRole));
  }

  @Test
  @DisplayName("Register - Unsuccessful : Nonconforming Password (number)")
  public void testNonconformingPasswordARegister() {
    doNothing().when(dalServiceUCC).start();
    when(userDAO.getOne(email)).thenReturn(null);
    doNothing().when(dalServiceUCC).commit();

    UserDTO userDTO = initUser();

    assertThrows(BusinessException.class, () -> authsUCC.register(userDTO, "azraël",
        studentRole));
  }

  @Test
  @DisplayName("Register - Unsuccessful : Nonconforming Password (length)")
  public void testNonconformingPasswordBRegister() {
    doNothing().when(dalServiceUCC).start();
    when(userDAO.getOne(email)).thenReturn(null);
    doNothing().when(dalServiceUCC).commit();

    UserDTO userDTO = initUser();

    assertThrows(BusinessException.class, () -> authsUCC.register(userDTO, "mal9",
        studentRole));

  }

  @Test
  @DisplayName("Register - Unsuccessful : Incorrect Role")
  public void testIncorrectRoleRegister() {
    doNothing().when(dalServiceUCC).start();
    when(userDAO.getOne(email)).thenReturn(null);
    doNothing().when(dalServiceUCC).commit();

    UserDTO userDTO = initUser();

    assertThrows(BusinessException.class, () -> authsUCC.register(userDTO, password,
        "Schtroumpf"));
  }

  @Test
  @DisplayName("Register - Unsuccessful : Professor Role for Student Email")
  public void testIncorrectRoleForStudentEmailARegister() {
    UserDTO userDTO = factory.getUserDTO();
    userDTO.setEmail("azraël@student.vinci.be");
    userDTO.setPhoneNumber(phoneNumber);

    doNothing().when(dalServiceUCC).start();
    when(userDAO.getOne(email)).thenReturn(null);
    doNothing().when(dalServiceUCC).commit();

    assertThrows(BusinessException.class, () -> authsUCC.register(userDTO, password,
        professorRole));
  }

  @Test
  @DisplayName("Register - Unsuccessful : Administrative Role for Student Email")
  public void testIncorrectRoleForStudentEmailBRegister() {
    UserDTO userDTO = factory.getUserDTO();
    userDTO.setEmail("azraël@student.vinci.be");
    userDTO.setPhoneNumber(phoneNumber);

    doNothing().when(dalServiceUCC).start();
    when(userDAO.getOne(email)).thenReturn(null);
    doNothing().when(dalServiceUCC).commit();

    assertThrows(BusinessException.class, () -> authsUCC.register(userDTO, password,
        adminRole));
  }

  @Test
  @DisplayName("Register - Unsuccessful : Student Role for Not Student Email")
  public void testIncorrectRoleForNotStudentEmailRegister() {
    doNothing().when(dalServiceUCC).start();
    when(userDAO.getOne(email)).thenReturn(null);
    doNothing().when(dalServiceUCC).commit();

    UserDTO userDTO = initUser();

    assertThrows(BusinessException.class, () -> authsUCC.register(userDTO, password,
        studentRole));
  }

  @Test
  @DisplayName("Register - Unsuccessful : Start Error")
  public void testStartErrorRegister() {
    doThrow(FatalException.class).when(dalServiceUCC).start();
    doNothing().when(dalServiceUCC).rollBack();
    doNothing().when(dalServiceUCC).commit();

    UserDTO userDTO = initUser();

    assertThrows(FatalException.class, () -> authsUCC.register(userDTO, password,
        studentRole));
  }

}
