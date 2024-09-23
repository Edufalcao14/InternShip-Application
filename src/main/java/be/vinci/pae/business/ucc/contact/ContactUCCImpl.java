package be.vinci.pae.business.ucc.contact;

import be.vinci.pae.business.domain.contact.Contact;
import be.vinci.pae.business.domain.contact.Place;
import be.vinci.pae.business.domain.contact.State;
import be.vinci.pae.business.domain.user.Role;
import be.vinci.pae.business.dto.CompanyDTO;
import be.vinci.pae.business.dto.ContactDTO;
import be.vinci.pae.business.dto.InternshipDTO;
import be.vinci.pae.business.dto.UserDTO;
import be.vinci.pae.business.ucc.internship.InternshipUCC;
import be.vinci.pae.dal.DALServiceUCC;
import be.vinci.pae.dal.dao.company.CompanyDAO;
import be.vinci.pae.dal.dao.contact.ContactDAO;
import be.vinci.pae.dal.dao.user.UserDAO;
import be.vinci.pae.utils.Utils;
import be.vinci.pae.utils.exceptions.BusinessException;
import be.vinci.pae.utils.exceptions.BusinessExceptionStatus;
import jakarta.inject.Inject;
import java.util.List;

/**
 * Implémentation de l'interface ContactUCC fournissant des opérations liées à la gestion des
 * contacts.
 */
public class ContactUCCImpl implements ContactUCC {

  @Inject
  private DALServiceUCC myDalServiceUCC;
  @Inject
  private InternshipUCC myInternshipUCC;
  @Inject
  private ContactDAO myContactDAO;
  @Inject
  private UserDAO myUserDAO;
  @Inject
  private CompanyDAO myCompanyDAO;

  /**
   * {@inheritDoc}
   *
   * @throws BusinessException si une erreur métier survient pendant la création du contact.
   */
  @Override
  public ContactDTO addContact(int company, int student) {
    ContactDTO addedContact;
    try {
      myDalServiceUCC.start();
      // Vérification si le user a déjà un contact accepté
      if (myContactDAO.getOneAcceptedContact(student)) {
        throw new BusinessException("L'étudiant a déjà un contact accepté.",
            BusinessExceptionStatus.ALREADY_EXISTS);
      }
      // Vérification si le user a déjà un contact avec l'entreprise pour l'année académique voulue
      if (!myContactDAO.getOne(company, student, Utils.calculateAcademicYear())) {
        throw new BusinessException(
            "L'étudiant a déjà un contact avec cette entreprise pour cette année académique.",
            BusinessExceptionStatus.ALREADY_EXISTS);
      }

      // Si le contact n'existe pas, on peut l'ajouter
      addedContact = myContactDAO.insert(company, student);
    } catch (Exception e) {
      myDalServiceUCC.rollBack();
      throw e;
    } finally {
      myDalServiceUCC.commit();
    }
    return addedContact;
  }

  /**
   * {@inheritDoc}
   *
   * @throws BusinessException si une erreur métier survient.
   */
  @Override
  public ContactDTO acceptContact(InternshipDTO internshipDTO, int version) {
    ContactDTO acceptedContact;
    try {
      myDalServiceUCC.start();

      ContactDTO contactDTO = myContactDAO.getOne(internshipDTO.getContactId(), version);
      if (contactDTO == null) {
        throw new BusinessException("Contact introuvable.", BusinessExceptionStatus.NOT_FOUND);
      }

      CompanyDTO companyDTO = myCompanyDAO.getOne(contactDTO.getCompanyId());
      if (companyDTO == null) {
        throw new BusinessException("Entreprise introuvable.", BusinessExceptionStatus.NOT_FOUND);
      }

      if (companyDTO.isBlacklisted()) {
        throw new BusinessException("L'entreprise est blacklistée.",
            BusinessExceptionStatus.ACCESS_DENIED);
      }

      Contact contact = (Contact) contactDTO;
      if (!contact.checkPreviousState(State.ACCEPTE)) {
        throw new BusinessException(
            "Le contact doit être pris pour pouvoir être accepté.",
            BusinessExceptionStatus.REQUEST_ERROR);
      }
      boolean result = myContactDAO.getOneAcceptedContact(internshipDTO.getStudentId());
      if (result) {
        throw new BusinessException("L'étudiant a déjà un contact accepté.",
            BusinessExceptionStatus.ALREADY_EXISTS);
      }

      contact.setState(State.ACCEPTE);
      internshipDTO.setContactDTO(contactDTO);
      acceptedContact = myContactDAO.update(contactDTO);
      if (acceptedContact == null) {
        throw new BusinessException("Le contact n'existe plus.", BusinessExceptionStatus.NOT_FOUND);
      }
      myContactDAO.suspendAllContactsOfStudent(internshipDTO.getStudentId());
      myInternshipUCC.addInternship(internshipDTO);


    } catch (Exception e) {
      myDalServiceUCC.rollBack();
      throw e;
    } finally {
      myDalServiceUCC.commit();
    }
    return acceptedContact;
  }


  /**
   * {@inheritDoc}
   *
   * @throws BusinessException si une erreur métier survient.
   */
  @Override
  public ContactDTO denyContact(int idContact, String refusalReason, int version) {
    ContactDTO deniedContact;
    try {
      myDalServiceUCC.start();
      ContactDTO contactDTO = myContactDAO.getOne(idContact, version);
      if (contactDTO == null) {
        throw new BusinessException("Contact introuvable.", BusinessExceptionStatus.NOT_FOUND);
      }
      Contact contact = (Contact) contactDTO;
      if (!contact.checkPreviousState(State.REFUSE)) {
        throw new BusinessException("Le contact doit être pris pour pouvoir être refusé.",
            BusinessExceptionStatus.REQUEST_ERROR);
      }

      contactDTO.setRefusalReason(refusalReason);
      contact.setState(State.REFUSE);
      deniedContact = myContactDAO.update(contactDTO);

      if (deniedContact == null) {
        throw new BusinessException("Le contact n'existe plus.", BusinessExceptionStatus.NOT_FOUND);
      }

    } catch (Exception e) {
      myDalServiceUCC.rollBack();
      throw e;
    } finally {
      myDalServiceUCC.commit();
    }
    return deniedContact;
  }

  /**
   * {@inheritDoc}
   *
   * @throws BusinessException si une erreur métier survient.
   */
  @Override
  public ContactDTO takeContact(int idContact, String meetingPlace, int version) {
    ContactDTO takeContact;
    try {
      myDalServiceUCC.start();

      ContactDTO contactDTO = myContactDAO.getOne(idContact, version);
      if (contactDTO == null) {
        throw new BusinessException("Contact introuvable.", BusinessExceptionStatus.NOT_FOUND);
      }
      Contact contact = (Contact) contactDTO;

      if (!contact.checkPlaceValidity(meetingPlace.toUpperCase())) {
        throw new BusinessException("Lieu de rencontre invalide.",
            BusinessExceptionStatus.REQUEST_ERROR);
      }
      Place place = Place.valueOf(meetingPlace.toUpperCase());

      if (!contact.checkPreviousState(State.PRIS)) {
        throw new BusinessException("Le contact doit être initié pour pouvoir être pris.",
            BusinessExceptionStatus.REQUEST_ERROR);
      }

      contactDTO.setMeetingPlace(place);
      contactDTO.setState(State.PRIS);
      takeContact = myContactDAO.update(contactDTO);

      if (takeContact == null) {
        throw new BusinessException("Le contact n'existe plus.", BusinessExceptionStatus.NOT_FOUND);
      }

    } catch (Exception e) {
      myDalServiceUCC.rollBack();
      throw e;
    } finally {
      myDalServiceUCC.commit();
    }
    return takeContact;
  }


  /**
   * {@inheritDoc}
   *
   * @throws BusinessException si une erreur métier survient.
   */
  @Override
  public ContactDTO unfollowContact(int idContact, int version) {
    ContactDTO unfollowedContact;
    try {
      myDalServiceUCC.start();
      ContactDTO contactDTO = myContactDAO.getOne(idContact, version);
      if (contactDTO == null) {
        throw new BusinessException("Contact introuvable.", BusinessExceptionStatus.NOT_FOUND);
      }
      Contact contact = (Contact) contactDTO;
      if (!contact.checkPreviousState(State.NON_SUIVI)) {
        throw new BusinessException("Le contact doit être initié ou pris pour ne plus le suivre.",
            BusinessExceptionStatus.REQUEST_ERROR);
      }

      contact.setState(State.NON_SUIVI);
      unfollowedContact = myContactDAO.update(contactDTO);

      if (unfollowedContact == null) {
        throw new BusinessException("Le contact n'existe plus.", BusinessExceptionStatus.NOT_FOUND);
      }

    } catch (Exception e) {
      myDalServiceUCC.rollBack();
      throw e;
    } finally {
      myDalServiceUCC.commit();
    }
    return unfollowedContact;

  }

  /**
   * {@inheritDoc}
   *
   * @throws BusinessException si une erreur métier survient.
   */
  @Override
  public List<ContactDTO> getContactsByUserId(UserDTO authenticatedUser, int idUser,
      boolean isFollowed) {
    List<ContactDTO> contactDTOList;

    try {

      myDalServiceUCC.start();

      if (authenticatedUser.getRole().equals(Role.ETUDIANT)) {
        contactDTOList = myContactDAO.getAllByUser(authenticatedUser.getId(), isFollowed);
      } else {
        UserDTO userDTO = myUserDAO.getOne(idUser);
        if (userDTO == null) {
          throw new BusinessException("Aucun utilisateur à cet ID.",
              BusinessExceptionStatus.NOT_FOUND);
        }
        contactDTOList = myContactDAO.getAllByUser(userDTO.getId(), isFollowed);
      }
    } catch (Exception e) {
      myDalServiceUCC.rollBack();
      throw e;
    } finally {
      myDalServiceUCC.commit();
    }

    return contactDTOList;

  }

  /**
   * {@inheritDoc}
   *
   * @throws BusinessException si une erreur métier survient.
   */
  @Override
  public List<ContactDTO> getContactsByCompanyId(int idCompany) {
    List<ContactDTO> contactDTOList;

    try {

      myDalServiceUCC.start();

      CompanyDTO companyDTO = myCompanyDAO.getOne(idCompany);
      if (companyDTO == null) {
        throw new BusinessException("Aucune entreprise à cet ID.",
            BusinessExceptionStatus.NOT_FOUND);
      }
      contactDTOList = myContactDAO.getAllByCompany(idCompany);
    } catch (Exception e) {
      myDalServiceUCC.rollBack();
      throw e;
    } finally {
      myDalServiceUCC.commit();
    }

    return contactDTOList;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<ContactDTO> getAllContacts() {
    List<ContactDTO> contacts;

    myDalServiceUCC.start();

    contacts = myContactDAO.getAll();

    myDalServiceUCC.commit();

    return contacts;
  }

}
