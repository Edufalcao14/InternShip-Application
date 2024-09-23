package be.vinci.pae.business.ucc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import be.vinci.pae.business.Factory;
import be.vinci.pae.business.domain.user.Role;
import be.vinci.pae.business.dto.UserDTO;
import be.vinci.pae.business.ucc.user.UserUCC;
import be.vinci.pae.business.utils.ApplicationBinderUserUCCTest;
import be.vinci.pae.dal.DALServiceUCC;
import be.vinci.pae.dal.dao.user.UserDAO;
import be.vinci.pae.utils.exceptions.BusinessException;
import be.vinci.pae.utils.exceptions.BusinessExceptionStatus;
import be.vinci.pae.utils.exceptions.FatalException;
import java.util.ArrayList;
import java.util.List;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.Mockito;

/**
 * Classe test de la classe UserUCCImpl pour définir le bon comportement des méthodes.
 */
class UserUCCImplTest {

  private static Factory factory;
  private static UserDAO userDAO;
  private static UserUCC userUCC;
  private static DALServiceUCC dalServiceUCC;

  private final String password = "schtroumpf1";
  private final int userId = 1;
  private final Role roleEtudiant = Role.ETUDIANT;

  /**
   * Méthode de configuration pour initialiser les ressources avant d'exécuter les tests. méthode
   * lie les services en utilisant ServiceLocator et initialise les dépendances requises.
   */
  @BeforeAll
  public static void setUp() {
    ServiceLocator locator = ServiceLocatorUtilities.bind("UserTestLocator",
        new ApplicationBinderUserUCCTest());
    factory = locator.getService(Factory.class);
    dalServiceUCC = locator.getService(DALServiceUCC.class);
    userDAO = locator.getService(UserDAO.class);
    userUCC = locator.getService(UserUCC.class);
  }

  @BeforeEach
  public void reset() {
    Mockito.reset(userDAO);
  }

  private UserDTO initUser() {

    String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
    UserDTO userDTO = factory.getUserDTO();
    int idStudent = 1;
    userDTO.setId(idStudent);
    String userFirstName = "josiane";
    userDTO.setFirstName(userFirstName);
    String userLastName = "banane";
    userDTO.setLastName(userLastName);
    String etudiantEmail = "josiane.banane@student.vinci.be";
    userDTO.setEmail(etudiantEmail);
    userDTO.setRole(roleEtudiant);
    userDTO.setVersion(1);
    userDTO.setPassword(hashedPassword);
    userDTO.setPhoneNumber("0123456789");
    userDTO.setHasInternship(false);
    userDTO.setAcademicYear("2021-2022");
    return userDTO;
  }

  @Test
  @DisplayName("User Recovery By ID - Successful")
  public void testSuccesfulGetUserById() {
    UserDTO userDTO = factory.getUserDTO();
    userDTO.setId(this.userId);

    doNothing().when(dalServiceUCC).start();
    when(userDAO.getOne(this.userId)).thenReturn(userDTO);
    doNothing().when(dalServiceUCC).commit();

    UserDTO userTest = userUCC.getUserById(this.userId);

    assertEquals(userDTO, userTest);
  }

  @Test
  @DisplayName("User Recovery By ID - Unsuccessful ")
  public void testUnsuccesfulGetUserById() {
    doNothing().when(dalServiceUCC).start();
    when(userDAO.getOne(this.userId)).thenReturn(null);
    doNothing().when(dalServiceUCC).commit();

    assertThrows(BusinessException.class, () -> userUCC.getUserById(this.userId));
  }

  @Test
  @DisplayName("User Recovery By ID - Unsuccessful : Start Error")
  public void testStartErrorGetUserById() {
    doThrow(FatalException.class).when(dalServiceUCC).start();
    when(userDAO.getOne(this.userId)).thenReturn(null);
    doNothing().when(dalServiceUCC).rollBack();
    doNothing().when(dalServiceUCC).commit();

    assertThrows(FatalException.class, () -> userUCC.getUserById(this.userId));
  }

  @Test
  @DisplayName("All Users Recovery - Successful")
  void testGetAllUsersSuccessful() {
    List<UserDTO> list = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      UserDTO user = factory.getUserDTO();
      list.add(user);
    }

    doNothing().when(dalServiceUCC).start();
    when(userDAO.getAll()).thenReturn(list);
    doNothing().when(dalServiceUCC).commit();

    List<UserDTO> listTests = userUCC.getAllUsers();

    assertEquals(list, listTests);
  }

  @Test
  @DisplayName("All Users Recovery - Unsuccessful")
  void testGetAllUsersUnsuccessful() {

    doNothing().when(dalServiceUCC).start();
    doThrow(FatalException.class).when(userDAO).getAll();
    doNothing().when(dalServiceUCC).commit();
    doNothing().when(dalServiceUCC).rollBack();

    assertThrows(FatalException.class, () -> userUCC.getAllUsers());
  }

  // ------------------------------------ Modify FirstName Test
  @Test
  @DisplayName("Modify Firstname: Successful")
  public void testModifyFirstNameSuccessful() {
    // Setup
    UserDTO modifiedUserDTO = initUser();

    // Mocking behaviors

    doNothing().when(dalServiceUCC).commit();
    doNothing().when(dalServiceUCC).start();

    when(userDAO.getOne(userId)).thenReturn(modifiedUserDTO);

    modifiedUserDTO.setFirstName("Caroline");

    when(userDAO.modifyUser(modifiedUserDTO, "firstName")).thenReturn(modifiedUserDTO);

    // Call the method under test
    UserDTO result = userUCC.modifyUserField(modifiedUserDTO, "firstName");

    // Assertions
    assertNotNull(result);
    assertEquals(modifiedUserDTO, result);
    assertEquals("Caroline", result.getFirstName());
  }

  @Test
  @DisplayName("Modify First Name : Unsuccessful - DAO NULL")
  public void testModifyFirstNameUnsuccessful() {
    // Mocking behaviors
    doNothing().when(dalServiceUCC).commit();
    doNothing().when(dalServiceUCC).start();

    // Setup
    UserDTO modifiedUserDTO = initUser();

    modifiedUserDTO.setFirstName("Caroline");

    when(userDAO.modifyUser(modifiedUserDTO, "firstName")).thenReturn(null);

    // Call the method
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      userUCC.modifyUserField(modifiedUserDTO, "firstName");
    });

    // Assertions
    assertEquals(BusinessExceptionStatus.NOT_FOUND, exception.getStatus());
  }

  // ------------ LastName tests
  @Test
  @DisplayName("Modify LastName: Successful")
  public void testModifyLastNameSuccessful() {
    // Mocking behaviors
    doNothing().when(dalServiceUCC).commit();
    doNothing().when(dalServiceUCC).start();

    // Setup
    UserDTO modifiedUserDTO = initUser();

    modifiedUserDTO.setLastName("Lina");

    when(userDAO.modifyUser(modifiedUserDTO, "lastName")).thenReturn(modifiedUserDTO);

    // Call the method under test
    UserDTO result = userUCC.modifyUserField(modifiedUserDTO, "lastName");

    // Assertions
    assertEquals(modifiedUserDTO, result);
  }

  @Test
  @DisplayName("Modify Last Name : Unsuccessful - DAO NULL")
  public void testModifyLastNameUnsuccessful() {
    // Mocking behaviors
    doNothing().when(dalServiceUCC).commit();
    doNothing().when(dalServiceUCC).start();

    // Setup
    UserDTO modifiedUserDTO = initUser();

    modifiedUserDTO.setLastName("Line");

    when(userDAO.modifyUser(modifiedUserDTO, "lastName")).thenReturn(null);

    // Call the method
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      userUCC.modifyUserField(modifiedUserDTO, "lastName");
    });

    // Assertions
    assertEquals(BusinessExceptionStatus.NOT_FOUND, exception.getStatus());
  }

  // ---------------------- EMail Tests
  @Test
  @DisplayName("Modify email: Successful")
  public void testModifyEmailSuccessful() {
    // Mocking behaviors
    doNothing().when(dalServiceUCC).commit();
    doNothing().when(dalServiceUCC).start();

    // Setup
    UserDTO modifiedUserDTO = initUser();

    modifiedUserDTO.setEmail("Caroline.line@student.vinci.be");

    when(userDAO.modifyUser(modifiedUserDTO, "email")).thenReturn(modifiedUserDTO);

    // Call the method under test
    UserDTO result = userUCC.modifyUserField(modifiedUserDTO, "email");

    // Assertions
    assertEquals(modifiedUserDTO, result);
  }

  @Test
  @DisplayName("Modify Email : Unsuccessful - DAO NULL")
  public void testModifyEmailUnsuccessful() {
    // Mocking behaviors
    doNothing().when(dalServiceUCC).commit();
    doNothing().when(dalServiceUCC).start();

    // Setup
    UserDTO modifiedUserDTO = initUser();

    modifiedUserDTO.setEmail("caroline.line@student.vinci.be");

    when(userDAO.modifyUser(modifiedUserDTO, "email")).thenReturn(null);

    // Call the method
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      userUCC.modifyUserField(modifiedUserDTO, "email");
    });

    // Assertions
    assertEquals(BusinessExceptionStatus.NOT_FOUND, exception.getStatus());
  }

  @Test
  @DisplayName("Modify Email : Unsuccessful - Invalid email Type")
  public void testModifyEmailUnsuccessfulInvalidEmailType() {
    // Mocking behaviors
    doNothing().when(dalServiceUCC).commit();
    doNothing().when(dalServiceUCC).start();

    // Setup
    UserDTO modifiedUserDTO = initUser();

    modifiedUserDTO.setEmail("caroline.line@gmail.vinci.be");

    when(userDAO.modifyUser(modifiedUserDTO, "email")).thenReturn(modifiedUserDTO);

    // Call the method
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      userUCC.modifyUserField(modifiedUserDTO, "email");
    });

    // Assertions
    assertEquals(BusinessExceptionStatus.REQUEST_ERROR, exception.getStatus());
  }

  @Test
  @DisplayName("Modify Email : Unsuccessful - Invalid email Role")
  public void testModifyEmailUnsuccessfulInvalidEmailRole() {
    // Mocking behaviors
    doNothing().when(dalServiceUCC).commit();
    doNothing().when(dalServiceUCC).start();

    // Setup
    UserDTO modifiedUserDTO = initUser();

    modifiedUserDTO.setEmail("caroline.line@vinci.be");

    when(userDAO.modifyUser(modifiedUserDTO, "email")).thenReturn(modifiedUserDTO);

    // Call the method
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      userUCC.modifyUserField(modifiedUserDTO, "email");
    });

    // Assertions
    assertEquals(BusinessExceptionStatus.UNAUTHORIZED, exception.getStatus());
  }

  // ------------------------- Phone Number tests
  @Test
  @DisplayName("Modify Phone Number : Successful")
  public void testModifyPhoneNumberSuccessful() {
    // Mocking behaviors
    doNothing().when(dalServiceUCC).commit();
    doNothing().when(dalServiceUCC).start();

    // Setup
    UserDTO modifiedUserDTO = initUser();

    modifiedUserDTO.setPhoneNumber("0465256614");

    when(userDAO.modifyUser(modifiedUserDTO, "phoneNumber")).thenReturn(modifiedUserDTO);

    // Call the method under test
    UserDTO result = userUCC.modifyUserField(modifiedUserDTO, "phoneNumber");

    // Assertions
    assertEquals(modifiedUserDTO, result);
  }

  @Test
  @DisplayName("Modify Phone Number : Unsuccessful - DAO NULL")
  public void testModifyPhoneNumberUnsuccessful() {
    // Mocking behaviors
    doNothing().when(dalServiceUCC).commit();
    doNothing().when(dalServiceUCC).start();

    // Setup
    UserDTO modifiedUserDTO = initUser();

    modifiedUserDTO.setPhoneNumber("0465256614");

    when(userDAO.modifyUser(modifiedUserDTO, "phoneNumber")).thenReturn(null);

    // Call the method
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      userUCC.modifyUserField(modifiedUserDTO, "phoneNumber");
    });

    // Assertions
    assertEquals(BusinessExceptionStatus.NOT_FOUND, exception.getStatus());
  }

  @Test
  @DisplayName("Modify Phone Number : Unsuccessful - Invalid Phone (More than 10 numbers)")
  public void testModifyPhoneNumberUnsuccessfulInvalidPhoneMore() {
    // Mocking behaviors
    doNothing().when(dalServiceUCC).commit();
    doNothing().when(dalServiceUCC).start();

    // Setup
    UserDTO modifiedUserDTO = initUser();

    modifiedUserDTO.setPhoneNumber("046525661434564");

    when(userDAO.modifyUser(modifiedUserDTO, "phoneNumber")).thenReturn(null);

    // Call the method
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      userUCC.modifyUserField(modifiedUserDTO, "phoneNumber");
    });

    // Assertions
    assertEquals(BusinessExceptionStatus.REQUEST_ERROR, exception.getStatus());
  }

  @Test
  @DisplayName("Modify Phone Number : Unsuccessful - Invalid Phone (Less than 10 numbers)")
  public void testModifyPhoneNumberUnsuccessfulInvalidPhoneLess() {
    // Mocking behaviors
    doNothing().when(dalServiceUCC).commit();
    doNothing().when(dalServiceUCC).start();

    // Setup
    UserDTO modifiedUserDTO = initUser();

    modifiedUserDTO.setPhoneNumber("0465");

    when(userDAO.modifyUser(modifiedUserDTO, "phoneNumber")).thenReturn(null);

    // Call the method
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      userUCC.modifyUserField(modifiedUserDTO, "phoneNumber");
    });

    // Assertions
    assertEquals(BusinessExceptionStatus.REQUEST_ERROR, exception.getStatus());
  }

  @Test
  @DisplayName("Modify Phone Number : Unsuccessful - Invalid FieldType")
  public void testModifyPhoneNumberUnsuccessfulInvalidFieldType() {
    // Mocking behaviors
    doNothing().when(dalServiceUCC).commit();
    doNothing().when(dalServiceUCC).start();

    // Setup
    UserDTO modifiedUserDTO = initUser();

    modifiedUserDTO.setPhoneNumber("0465256614");

    when(userDAO.modifyUser(modifiedUserDTO, "phoneNumber")).thenReturn(null);

    // Call the method
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      userUCC.modifyUserField(modifiedUserDTO, "phone");
    });

    // Assertions
    assertEquals(BusinessExceptionStatus.REQUEST_ERROR, exception.getStatus());
  }

  // ------------------------------------ Modify Password Test
  @Test
  @DisplayName("Modify password - Successful")
  public void testModifyPasswordSuccessful() {
    // Initialize test data
    UserDTO userDTO = initUser();
    String password = "oldPassword123";
    int userId = userDTO.getId();

    // Mocking behavior
    when(userDAO.getOne(userId)).thenReturn(userDTO);
    when(userDAO.modifyPassword(eq(userId), anyString(), anyInt())).thenReturn(true);

    // Action
    String newPassword = "newPassword7";
    String newPasswordHashed = BCrypt.hashpw(newPassword, BCrypt.gensalt());

    boolean result = userUCC.modifyUserPassword(userId, this.password, newPasswordHashed,
        userDTO.getVersion());

    // Assertion
    assertTrue(result); // Ensure that password modification is successful

  }

  @Test
  @DisplayName("Modify password - DTO Null (getOneUser) : Unsuccessful")
  public void testModifyPasswordUserNotFound() {

    // Mocking behaviors
    doNothing().when(dalServiceUCC).start();
    when(userDAO.getOne(userId)).thenReturn(null);
    doNothing().when(dalServiceUCC).commit();

    String password = "oldPassword";
    String newPassword = "newPassword";
    // Call the method under test and assert
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      userUCC.modifyUserPassword(userId, password, newPassword, 1);
    });

    // Assert the exception message and status
    assertEquals(BusinessExceptionStatus.NOT_FOUND, exception.getStatus());
  }


  @Test
  @DisplayName("Modify password -Modify Password False : Unsuccessful")
  public void testModifyPasswordUserNotFoundDTO() {
    // Setup
    UserDTO userDTO = initUser();

    // Mocking behaviors
    doNothing().when(dalServiceUCC).start();
    doNothing().when(dalServiceUCC).commit();
    when(userDAO.getOne(userId)).thenReturn(userDTO);

    String newPassword = "newPassword1";
    String newPasswordHashed = BCrypt.hashpw(newPassword, BCrypt.gensalt());
    int newVersion = userDTO.getVersion();

    when(userDAO.modifyPassword(userId, newPasswordHashed, newVersion)).thenReturn(Boolean.FALSE);

    // Call the method under test and assert
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      userUCC.modifyUserPassword(userDTO.getId(), this.password, newPassword, userDTO.getVersion());
    });

    // Assert the exception message and status
    assertEquals(BusinessExceptionStatus.NOT_FOUND, exception.getStatus());
  }

  @Test
  @DisplayName("Modify password - Wrong Password : Unsuccessful")
  public void testModifyPasswordWrongPassword() {
    // Setup
    UserDTO userDTO = initUser();

    // Mocking behaviors
    doNothing().when(dalServiceUCC).start();
    when(userDAO.getOne(userId)).thenReturn(userDTO);
    doNothing().when(dalServiceUCC).commit();

    String password = "oldPassword1";
    String newPassword = "newPassword1";
    // Call the method under test and assert
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      userUCC.modifyUserPassword(userDTO.getId(), password, newPassword, userDTO.getVersion());
    });

    // Assert the exception message and status
    assertEquals(BusinessExceptionStatus.UNAUTHORIZED, exception.getStatus());
  }

  @Test
  @DisplayName("Modify password - Invalid New Password : Unsuccessful")
  public void testModifyPasswordInvalidNewPassword() {
    // Setup
    UserDTO userDTO = initUser();

    // Mocking behaviors
    doNothing().when(dalServiceUCC).start();
    when(userDAO.getOne(userId)).thenReturn(userDTO);
    doNothing().when(dalServiceUCC).commit();

    String newPassword = "new";

    // Call the method under test and assert
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      userUCC.modifyUserPassword(userDTO.getId(), this.password, newPassword, userDTO.getVersion());
    });

    // Assert the exception message and status
    assertEquals(BusinessExceptionStatus.REQUEST_ERROR, exception.getStatus());
  }

  @Test
  @DisplayName("Modify Password -DTO False (modifyPassword) : Unsuccessful")
  void testModifyPasswordDTOModifyPasswordFalse() {
    // Setup
    UserDTO userDTO = initUser();

    // Mocking behaviors
    doNothing().when(dalServiceUCC).start();
    when(userDAO.getOne(userId)).thenReturn(userDTO);
    doNothing().when(dalServiceUCC).commit();

    String newPassword = "azerty123";
    String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

    when(userDAO.modifyPassword(userId, hashedPassword, userDTO.getVersion())).thenReturn(
        Boolean.FALSE);

    // Call the method under test and assert
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      userUCC.modifyUserPassword(userDTO.getId(), this.password, newPassword, userDTO.getVersion());
    });

    // Assert the exception message and status
    assertEquals(BusinessExceptionStatus.NOT_FOUND, exception.getStatus());
  }

}
