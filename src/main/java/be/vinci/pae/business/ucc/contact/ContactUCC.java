package be.vinci.pae.business.ucc.contact;

import be.vinci.pae.business.dto.ContactDTO;
import be.vinci.pae.business.dto.InternshipDTO;
import be.vinci.pae.business.dto.UserDTO;
import be.vinci.pae.utils.exceptions.BusinessException;
import java.util.List;

/**
 * L'interface ContactUCC (User Control Component) définit les opérations liées à la gestion des
 * contacts.
 */
public interface ContactUCC {

  /**
   * Ajoute un nouveau contact.
   *
   * @param company l'ID de l'entreprise.
   * @param student l'ID de l'étudiant.
   * @return le contact ajouté.
   * @throws BusinessException si une erreur métier survient pendant la création du contact.
   */
  ContactDTO addContact(int company, int student);

  /**
   * Ne suit plus un contact spécifié.
   *
   * @param idContact l'ID du contact à ne plus suivre.
   * @param version   la version du contact.
   * @return le contact non suivi.
   * @throws BusinessException si une erreur métier survient.
   */
  ContactDTO unfollowContact(int idContact, int version);

  /**
   * Obtient la liste des contacts associés à un étudiant spécifié.
   *
   * @param authenticatedUser l'étudiant authentifié.
   * @param idUser            l'ID de l'étudiant pour lequel obtenir les contacts.
   * @param isFollowed        true si uniquement les contacts suivis, false si tous.
   * @return la liste des contacts de l'étudiant spécifié.
   * @throws BusinessException si une erreur métier survient.
   */
  List<ContactDTO> getContactsByUserId(UserDTO authenticatedUser, int idUser, boolean isFollowed);

  /**
   * Obtient la liste des contacts associés à une entreprise spécifiée.
   *
   * @param idCompany l'ID de l'entreprise pour laquelle obtenir les contacts.
   * @return la liste des contacts de l'entreprise spécifiée.
   * @throws BusinessException si une erreur métier survient.
   */
  List<ContactDTO> getContactsByCompanyId(int idCompany);

  /**
   * Récupère la liste de tous les contacts.
   *
   * @return Liste de tous les contacts.
   */
  List<ContactDTO> getAllContacts();

  /**
   * Accepte un contact avec les détails fournis.
   *
   * @param internshipDTO le stage à ajouter.
   * @param version       la version du contact.
   * @return le contact accepté.
   * @throws BusinessException si une erreur métier survient.
   */
  ContactDTO acceptContact(InternshipDTO internshipDTO, int version);


  /**
   * Refuse un contact spécifié avec une raison de refus spécifiée.
   *
   * @param idContact     l'ID du contact à refuser.
   * @param refusalReason la raison du refus.
   * @param version       la version du contact.
   * @return le contact refusé.
   * @throws BusinessException si une erreur métier survient.
   */
  ContactDTO denyContact(int idContact, String refusalReason, int version);

  /**
   * Prend contact en spécifiant un lieu de rencontre.
   *
   * @param idContact    l'ID du contact à prendre.
   * @param meetingPlace le lieu de rencontre.
   * @param version      la version du contact.
   * @return le contact pris.
   * @throws BusinessException si une erreur métier survient.
   */
  ContactDTO takeContact(int idContact, String meetingPlace, int version);
}
