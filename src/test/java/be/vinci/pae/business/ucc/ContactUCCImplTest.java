package be.vinci.pae.business.ucc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import be.vinci.pae.business.Factory;
import be.vinci.pae.business.domain.contact.Place;
import be.vinci.pae.business.domain.contact.State;
import be.vinci.pae.business.domain.user.Role;
import be.vinci.pae.business.dto.CompanyDTO;
import be.vinci.pae.business.dto.ContactDTO;
import be.vinci.pae.business.dto.InternshipDTO;
import be.vinci.pae.business.dto.UserDTO;
import be.vinci.pae.business.ucc.contact.ContactUCC;
import be.vinci.pae.business.ucc.internship.InternshipUCC;
import be.vinci.pae.business.utils.ApplicationBinderContactUCCTest;
import be.vinci.pae.dal.DALServiceUCC;
import be.vinci.pae.dal.dao.company.CompanyDAO;
import be.vinci.pae.dal.dao.contact.ContactDAO;
import be.vinci.pae.dal.dao.user.UserDAO;
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
 * Classe test de la classe ContactUCCImpl pour définir le bon comportement des méthodes.
 */
public class ContactUCCImplTest {

  private static Factory factory;
  private static CompanyDAO companyDAO;
  private static ContactDAO contactDAO;
  private static ContactUCC contactUCC;
  private static InternshipUCC internshipUCC;
  private static UserDAO userDAO;
  private static DALServiceUCC dalServiceUCC;
  private final int idContact = 1;
  private final int company = 3;
  private final int student = 5;
  private final State state = State.PRIS;
  private final String academicYear = "2023-2024";
  private final int version = 1;

  private final Role role = Role.ETUDIANT;

  /**
   * Méthode de configuration pour initialiser les ressources avant d'exécuter les tests. méthode
   * lie les services en utilisant ServiceLocator et initialise les dépendances requises.
   */
  @BeforeAll
  public static void setUp() {
    ServiceLocator locator = ServiceLocatorUtilities.bind("ContactTestLocator",
        new ApplicationBinderContactUCCTest());
    factory = locator.getService(Factory.class);
    contactDAO = locator.getService(ContactDAO.class);
    companyDAO = locator.getService(CompanyDAO.class);
    userDAO = locator.getService(UserDAO.class);
    contactUCC = locator.getService(ContactUCC.class);
    dalServiceUCC = locator.getService(DALServiceUCC.class);
    internshipUCC = locator.getService(InternshipUCC.class);
  }

  @BeforeEach
  public void reset() {
    Mockito.reset(contactDAO, companyDAO, userDAO, internshipUCC);
  }

  private ContactDTO initContact() {
    ContactDTO contactDTO = factory.getContactDTO();
    contactDTO.setId(idContact);
    contactDTO.setCompanyId(company);
    contactDTO.setStudentId(student);
    contactDTO.setAcademicYear(academicYear);
    contactDTO.setState(state);
    return contactDTO;
  }

  private UserDTO initUser() {
    UserDTO userDTO = factory.getUserDTO();
    userDTO.setId(student);
    String email = "Caroline.line@student.vinci.be";
    userDTO.setEmail(email);
    userDTO.setRole(role);
    return userDTO;
  }

  private CompanyDTO initCompany() {
    CompanyDTO companyDTO = factory.getCompanyDTO();
    companyDTO.setId(company);
    companyDTO.setBlacklisted(false);
    return companyDTO;
  }

  private InternshipDTO initInternship() {
    InternshipDTO internshipDTO = factory.getInternshipDTO();
    internshipDTO.setId(1);
    internshipDTO.setContactId(idContact);
    return internshipDTO;
  }

  @Test
  @DisplayName("add contact - succeful 1")
  public void testSuccessfulAddContact1() {

    ContactDTO contactDTO = initContact();
    doNothing().when(dalServiceUCC).start();

    when(contactDAO.getOneAcceptedContact(student)).thenReturn(false);
    when(contactDAO.getOne(company, student, academicYear)).thenReturn(true);
    when(contactDAO.insert(company, student)).thenReturn(contactDTO);

    doNothing().when(dalServiceUCC).commit();

    ContactDTO contactDTOAdded = contactUCC.addContact(company, student);

    assertEquals(contactDTO, contactDTOAdded);

  }

  @Test
  @DisplayName("add contact with a failure on function getOneAcceptedContact-unsuccessful1")
  public void testUnsuccessfulAddContact1() {

    doNothing().when(dalServiceUCC).start();

    when(contactDAO.getOneAcceptedContact(student)).thenReturn(true);

    doNothing().when(dalServiceUCC).rollBack();
    doNothing().when(dalServiceUCC).commit();

    assertThrows(BusinessException.class,
        () -> contactUCC.addContact(company, student));

  }

  @Test
  @DisplayName("add contact with a failure on function notAlreadyExist - unsuccessful 2 ")
  public void testUnsuccessfulAddContact2() {

    doNothing().when(dalServiceUCC).start();

    when(contactDAO.getOneAcceptedContact(student)).thenReturn(false);
    when(contactDAO.getOne(company, student, academicYear)).thenReturn(false);

    doNothing().when(dalServiceUCC).rollBack();
    doNothing().when(dalServiceUCC).commit();

    assertThrows(BusinessException.class,
        () -> contactUCC.addContact(company, student));

  }

  @Test
  @DisplayName("add contact with a failure on Start Error - unsuccessful 3")
  public void testUnsuccessfulAddContact3() {

    doThrow(FatalException.class).when(dalServiceUCC).start();

    doNothing().when(dalServiceUCC).rollBack();
    doNothing().when(dalServiceUCC).commit();

    assertThrows(FatalException.class, () -> contactUCC.addContact(company, student));

  }

  @Test
  @DisplayName("unfollow contact - successful 1")
  public void testSucessfulUnfollowContact1() {

    ContactDTO contactDTO = initContact();

    doNothing().when(dalServiceUCC).start();

    when(contactDAO.getOne(idContact, version)).thenReturn(contactDTO);
    when(contactDAO.update(contactDTO)).thenReturn(contactDTO);

    doNothing().when(dalServiceUCC).commit();

    ContactDTO unfollowedContact = contactUCC.unfollowContact(idContact, version);

    assertEquals(contactDTO, unfollowedContact);
  }

  @Test
  @DisplayName("unfollow contact with a failure on function getOneById - unsuccessful 1")
  public void testUnsuccessfulUnfollowContact1() {

    doNothing().when(dalServiceUCC).start();

    when(contactDAO.getOne(idContact, version)).thenReturn(null);

    doNothing().when(dalServiceUCC).rollBack();
    doNothing().when(dalServiceUCC).commit();

    assertThrows(BusinessException.class, () -> contactUCC.unfollowContact(idContact, version));
  }


  @Test
  @DisplayName("unfollow contact with a failure on checkBusiness- unsuccessful 2")
  public void testUnsuccessfulUnfollowContact2() {

    ContactDTO contactDTO = initContact();
    contactDTO.setState(State.ACCEPTE);

    doNothing().when(dalServiceUCC).start();

    when(contactDAO.getOne(idContact, version)).thenReturn(contactDTO);

    doNothing().when(dalServiceUCC).rollBack();
    doNothing().when(dalServiceUCC).commit();

    assertThrows(BusinessException.class, () -> contactUCC.unfollowContact(idContact, version));
  }

  @Test
  @DisplayName("unfollow contact with a failure on Start Error - unsuccessful 3")
  public void testUnsuccessfulUnfollowContact3() {

    doThrow(FatalException.class).when(dalServiceUCC).start();

    doNothing().when(dalServiceUCC).rollBack();
    doNothing().when(dalServiceUCC).commit();

    assertThrows(FatalException.class, () -> contactUCC.unfollowContact(idContact, version));

  }

  @Test
  @DisplayName("unfollow contact with a failure on function unfollowContact - unsuccessful 4")
  public void testUnsuccessfulUnfollowContact4() {

    ContactDTO contactDTO = initContact();

    doNothing().when(dalServiceUCC).start();

    when(contactDAO.getOne(idContact, version)).thenReturn(contactDTO);
    when(contactDAO.update(contactDTO)).thenReturn(null);

    doNothing().when(dalServiceUCC).commit();

    assertThrows(BusinessException.class, () -> contactUCC.unfollowContact(idContact, version));
  }

  @Test
  @DisplayName("getAll contact used by the authentifacted Student - successful 1")
  public void testSuccessGetContactByUserId1() {

    List<ContactDTO> contactDTOListOfTheStudentAuthentificated = new ArrayList<>();
    ContactDTO contactDTO1 = initContact();
    ContactDTO contactDTO2 = initContact();
    contactDTOListOfTheStudentAuthentificated.add(contactDTO1);
    contactDTOListOfTheStudentAuthentificated.add(contactDTO2);

    UserDTO authentificatedUser = initUser();

    doNothing().when(dalServiceUCC).start();

    when(contactDAO.getAllByUser(authentificatedUser.getId(), true)).thenReturn(
        contactDTOListOfTheStudentAuthentificated);

    doNothing().when(dalServiceUCC).commit();

    List<ContactDTO> listOfContacts = contactUCC.getContactsByUserId(authentificatedUser,
        authentificatedUser.getId(), true);

    assertEquals(contactDTOListOfTheStudentAuthentificated, listOfContacts);
  }

  @Test
  @DisplayName("getAll contact used by administration and teachers - successful 2")
  public void testSuccessGetContactByUserId2() {

    List<ContactDTO> contactDTOListOfTheStudent = new ArrayList<>();
    ContactDTO contactDTO1 = initContact();
    ContactDTO contactDTO2 = initContact();
    contactDTOListOfTheStudent.add(contactDTO1);
    contactDTOListOfTheStudent.add(contactDTO2);

    UserDTO authentificatedTeacher = initUser();
    authentificatedTeacher.setRole(Role.PROFESSEUR);

    UserDTO studentDTO = initUser();

    int idStudent = 5;

    doNothing().when(dalServiceUCC).start();

    when(userDAO.getOne(idStudent)).thenReturn(studentDTO);
    when(contactDAO.getAllByUser(idStudent, true)).thenReturn(
        contactDTOListOfTheStudent);

    doNothing().when(dalServiceUCC).commit();

    List<ContactDTO> listOfContacts = contactUCC.getContactsByUserId(authentificatedTeacher,
        idStudent, true);

    assertEquals(contactDTOListOfTheStudent, listOfContacts);
  }

  @Test
  @DisplayName("get all contact with a failure on Start Error - unsuccessful 3")
  public void testUnsuccessfulGetAllContactByUserId() {
    doThrow(FatalException.class).when(dalServiceUCC).start();

    doNothing().when(dalServiceUCC).rollBack();
    doNothing().when(dalServiceUCC).commit();

    UserDTO authentificatedUser = initUser();
    assertThrows(FatalException.class,
        () -> contactUCC.getContactsByUserId(authentificatedUser, student, true));
  }

  @Test
  @DisplayName("getAll contact with a failure on function getOne - unsuccessful 2")
  public void testUnsuccessfulGetContactByUserId2() {

    UserDTO authentificatedTeacher = initUser();
    authentificatedTeacher.setRole(Role.PROFESSEUR);
    int idStudent = 5;

    doNothing().when(dalServiceUCC).start();

    when(userDAO.getOne(idStudent)).thenReturn(null);

    assertThrows(BusinessException.class,
        () -> contactUCC.getContactsByUserId(authentificatedTeacher,
            idStudent, true));
  }

  @Test
  @DisplayName("deny contact - successful 1")
  public void testSuccessDenyContact1() {

    ContactDTO contactDTO = initContact();

    doNothing().when(dalServiceUCC).start();

    when(contactDAO.getOne(idContact, version)).thenReturn(contactDTO);
    when(contactDAO.update(contactDTO)).thenReturn(
        contactDTO);

    doNothing().when(dalServiceUCC).commit();

    ContactDTO deniedContact = contactUCC.denyContact(idContact, "il est trop nul", version);

    assertEquals(contactDTO, deniedContact);
  }

  @Test
  @DisplayName("deny contact with a failure on function getOneById - unsuccessful 1")
  public void testUnsuccessfulDenyContact1() {

    doNothing().when(dalServiceUCC).start();

    when(contactDAO.getOne(idContact, version)).thenReturn(null);

    doNothing().when(dalServiceUCC).rollBack();
    doNothing().when(dalServiceUCC).commit();

    assertThrows(
        BusinessException.class,
        () -> contactUCC.denyContact(idContact, "il est trop nul", version));
  }

  @Test
  @DisplayName("deny contact with a failure on check business - unsuccessful 2")
  public void testUnsuccessfulDenyContact2() {

    ContactDTO contactDTO = initContact();
    contactDTO.setState(State.ACCEPTE);

    doNothing().when(dalServiceUCC).start();

    when(contactDAO.getOne(idContact, version)).thenReturn(contactDTO);

    doNothing().when(dalServiceUCC).rollBack();
    doNothing().when(dalServiceUCC).commit();

    assertThrows(
        BusinessException.class,
        () -> contactUCC.denyContact(idContact, "il est trop nul", version));
  }

  @Test
  @DisplayName("deny contact with a failure start error- unsuccessful 3")
  public void testUnsuccessfulDenyContact3() {

    doThrow(FatalException.class).when(dalServiceUCC).start();

    doNothing().when(dalServiceUCC).rollBack();
    doNothing().when(dalServiceUCC).commit();

    assertThrows(
        FatalException.class,
        () -> contactUCC.denyContact(idContact, "il est trop nul", version));
  }

  @Test
  @DisplayName("deny contact with a failure on function denyOneContact - unsuccessful 4")
  public void testUnsuccessfulDenyContact4() {

    ContactDTO contactDTO = initContact();

    doNothing().when(dalServiceUCC).start();

    when(contactDAO.getOne(idContact, version)).thenReturn(contactDTO);
    when(contactDAO.update(contactDTO)).thenReturn(
        null);

    doNothing().when(dalServiceUCC).commit();

    assertThrows(
        BusinessException.class,
        () -> contactUCC.denyContact(idContact, "il est trop nul", version));
  }

  @Test
  @DisplayName("take contact - successful 1")
  public void testSuccessTakeContact1() {

    ContactDTO contactDTO = initContact();
    contactDTO.setState(State.INITIE);

    doNothing().when(dalServiceUCC).start();

    when(contactDAO.getOne(idContact, version)).thenReturn(contactDTO);
    when(contactDAO.update(contactDTO)).thenReturn(
        contactDTO);

    doNothing().when(dalServiceUCC).commit();

    ContactDTO deniedContact = contactUCC.takeContact(idContact, "presentiel", version);

    assertEquals(contactDTO, deniedContact);
  }

  @Test
  @DisplayName("take contact with a failure on getOneById - unsuccessful 1")
  public void testUnsuccessTakeContact1() {

    ContactDTO contactDTO = initContact();
    contactDTO.setState(State.INITIE);

    doNothing().when(dalServiceUCC).start();

    when(contactDAO.getOne(idContact, version)).thenReturn(null);

    doNothing().when(dalServiceUCC).commit();

    assertThrows(
        BusinessException.class,
        () -> contactUCC.takeContact(idContact, "presentiel", version));
  }

  @Test
  @DisplayName("take contact with a failure on check business- unsuccessful 2")
  public void testUnsuccessTakeContact2() {

    ContactDTO contactDTO = initContact();
    contactDTO.setState(State.INITIE);

    doNothing().when(dalServiceUCC).start();

    when(contactDAO.getOne(idContact, version)).thenReturn(contactDTO);

    doNothing().when(dalServiceUCC).commit();

    assertThrows(
        BusinessException.class,
        () -> contactUCC.takeContact(idContact, "zfzef", version));
  }

  @Test
  @DisplayName("take contact with a failure on check business- unsuccessful 3")
  public void testUnsuccessTakeContact3() {

    ContactDTO contactDTO = initContact();

    doNothing().when(dalServiceUCC).start();

    when(contactDAO.getOne(idContact, version)).thenReturn(contactDTO);

    doNothing().when(dalServiceUCC).commit();

    assertThrows(
        BusinessException.class,
        () -> contactUCC.takeContact(idContact, "presentiel", version));
  }


  @Test
  @DisplayName("take contact with a failure start error- unsuccessful 4")
  public void testUnsuccessfulTakeContact4() {

    doThrow(FatalException.class).when(dalServiceUCC).start();

    doNothing().when(dalServiceUCC).rollBack();
    doNothing().when(dalServiceUCC).commit();

    assertThrows(
        FatalException.class,
        () -> contactUCC.takeContact(idContact, "presentiel", version));
  }

  @Test
  @DisplayName("take contact with a failure on function takeOneContact - unsuccessful 5")
  public void testUnsuccessfulTakeContact5() {

    ContactDTO contactDTO = initContact();
    contactDTO.setState(State.INITIE);

    doNothing().when(dalServiceUCC).start();

    when(contactDAO.getOne(idContact, version)).thenReturn(contactDTO);
    when(contactDAO.update(contactDTO)).thenReturn(
        null);

    doNothing().when(dalServiceUCC).commit();

    assertThrows(BusinessException.class,
        () -> contactUCC.takeContact(idContact, "presentiel", version));
  }

  @Test
  @DisplayName("Test d'exception lorsque le contact n'est pas dans l'état requis pour être pris")
  void testTakeContactInvalidState() {
    ContactDTO contactDTO = initContact();
    contactDTO.setState(State.REFUSE);

    when(contactDAO.getOne(idContact, version)).thenReturn(contactDTO);

    assertThrows(BusinessException.class, () -> contactUCC.takeContact(idContact,
            String.valueOf(Place.PRESENTIEL), version),
        "Le contact doit être initié pour pouvoir être pris.");
  }

  @Test
  @DisplayName("Recover All Contacts - Successful")
  void getAllContactsSuccessful() {
    List<ContactDTO> list = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      ContactDTO contactDTO = factory.getContactDTO();
      list.add(contactDTO);
    }

    doNothing().when(dalServiceUCC).start();
    when(contactDAO.getAll()).thenReturn(list);
    doNothing().when(dalServiceUCC).commit();

    List<ContactDTO> listTest = contactUCC.getAllContacts();
    assertEquals(list, listTest);
  }


  @Test
  @DisplayName("Test recover all contacts for a company - Successful")
  void testGetContactsByCompanyId() {
    int idCompany = 1;
    List<ContactDTO> expectedContacts = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      ContactDTO contactDTO = factory.getContactDTO();
      expectedContacts.add(contactDTO);
    }
    when(companyDAO.getOne(idCompany)).thenReturn(factory.getCompanyDTO());
    when(contactDAO.getAllByCompany(idCompany)).thenReturn(expectedContacts);
    List<ContactDTO> actualContacts = contactUCC.getContactsByCompanyId(idCompany);

    assertEquals(expectedContacts, actualContacts);
  }

  @Test
  @DisplayName("Test pour gérer une entreprise introuvable - Unsuccessful")
  void testCompanyNotFound() {
    int idCompany = 1;

    when(companyDAO.getOne(idCompany)).thenReturn(null);

    assertThrows(BusinessException.class,
        () -> contactUCC.getContactsByCompanyId(idCompany));
  }

  @Test
  @DisplayName("Accept Contact - Successful")
  public void testAcceptContactSuccessful() {
    doNothing().when(dalServiceUCC).start();
    doNothing().when(dalServiceUCC).commit();

    ContactDTO contactDTO = initContact();
    when(contactDAO.getOne(idContact, version)).thenReturn(contactDTO);

    CompanyDTO companyDTO = initCompany();
    when(companyDAO.getOne(company)).thenReturn(companyDTO);

    when(contactDAO.getOneAcceptedContact(student)).thenReturn(false);

    when(contactDAO.update(contactDTO)).thenReturn(contactDTO);

    InternshipDTO internshipDTO = initInternship();

    when(internshipUCC.addInternship(internshipDTO)).thenReturn(null);

    ContactDTO contactTest = contactUCC.acceptContact(initInternship(), version);

    assertEquals(contactDTO, contactTest);
  }

  @Test
  @DisplayName("Accept Contact - Unsuccessful : Contact Not Found")
  public void testAcceptContactUnsuccessfulContactNotFound() {
    doNothing().when(dalServiceUCC).start();
    doNothing().when(dalServiceUCC).commit();
    doNothing().when(dalServiceUCC).rollBack();

    when(contactDAO.getOne(idContact, version)).thenReturn(null);

    assertThrows(BusinessException.class,
        () -> contactUCC.acceptContact(initInternship(), version));
  }

  @Test
  @DisplayName("Accept Contact - Unsuccessful : Company Not Found")
  public void testAcceptContactUnsuccessfulCompanyNotFound() {
    doNothing().when(dalServiceUCC).start();
    doNothing().when(dalServiceUCC).commit();
    doNothing().when(dalServiceUCC).rollBack();

    ContactDTO contactDTO = initContact();

    when(contactDAO.getOne(idContact, version)).thenReturn(contactDTO);

    when(companyDAO.getOne(company)).thenReturn(null);

    assertThrows(BusinessException.class,
        () -> contactUCC.acceptContact(initInternship(), version));
  }

  @Test
  @DisplayName("Accept Contact - Unsuccessful : Blacklisted Company")
  public void testAcceptContactUnsuccessfulBlacklistedCompany() {
    doNothing().when(dalServiceUCC).start();
    doNothing().when(dalServiceUCC).commit();
    doNothing().when(dalServiceUCC).rollBack();

    ContactDTO contactDTO = initContact();

    when(contactDAO.getOne(idContact, version)).thenReturn(contactDTO);

    CompanyDTO companyDTO = initCompany();
    companyDTO.setBlacklisted(true);

    when(companyDAO.getOne(company)).thenReturn(companyDTO);

    assertThrows(BusinessException.class,
        () -> contactUCC.acceptContact(initInternship(), version));
  }

  @Test
  @DisplayName("Accept Contact - Unsuccessful : Incorrect State")
  public void testAcceptContactUnsuccessfulIncorrectState() {
    doNothing().when(dalServiceUCC).start();
    doNothing().when(dalServiceUCC).commit();
    doNothing().when(dalServiceUCC).rollBack();

    ContactDTO contactDTO = initContact();
    contactDTO.setState(State.INITIE);

    when(contactDAO.getOne(idContact, version)).thenReturn(contactDTO);

    CompanyDTO companyDTO = initCompany();

    when(companyDAO.getOne(company)).thenReturn(companyDTO);

    assertThrows(BusinessException.class,
        () -> contactUCC.acceptContact(initInternship(), version));
  }

  @Test
  @DisplayName("Accept Contact - Unsuccessful : Already Accepted Internship")
  public void testAcceptContactUnsuccessfulAlreadyAcceptedInternship() {
    doNothing().when(dalServiceUCC).start();
    doNothing().when(dalServiceUCC).commit();
    doNothing().when(dalServiceUCC).rollBack();

    ContactDTO contactDTO = initContact();

    when(contactDAO.getOne(idContact, version)).thenReturn(contactDTO);

    CompanyDTO companyDTO = initCompany();
    companyDTO.setBlacklisted(false);

    when(companyDAO.getOne(company)).thenReturn(companyDTO);

    when(contactDAO.getOneAcceptedContact(student)).thenReturn(Boolean.TRUE);

    assertThrows(BusinessException.class,
        () -> contactUCC.acceptContact(initInternship(), version));
  }

  @Test
  @DisplayName("Accept Contact - Unsuccessful : Deleted Contact")
  public void testAcceptContactUnsuccessfulDeletedContact() {
    doNothing().when(dalServiceUCC).start();
    doNothing().when(dalServiceUCC).commit();
    doNothing().when(dalServiceUCC).rollBack();

    InternshipDTO internshipDTO = initInternship();
    ContactDTO contactDTO = initContact();

    when(contactDAO.getOne(idContact, version)).thenReturn(contactDTO);

    CompanyDTO companyDTO = initCompany();

    when(companyDAO.getOne(company)).thenReturn(companyDTO);

    when(contactDAO.getOneAcceptedContact(internshipDTO.getStudentId())).thenReturn(true);

    assertThrows(BusinessException.class, () -> contactUCC.acceptContact(internshipDTO, version));
  }

}