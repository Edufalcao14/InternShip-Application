package be.vinci.pae.dal.dao.contact;

import be.vinci.pae.business.dto.ContactDTO;
import be.vinci.pae.utils.exceptions.FatalException;
import be.vinci.pae.utils.exceptions.FatalExceptionType;
import java.util.List;

/**
 * L'interface ContactDAO définit les méthodes pour accéder et manipuler les données des contacts
 * dans la base de données.
 */
public interface ContactDAO {

  /**
   * Récupère un objet ContactDTO correspondant au contact ayant l'ID spécifié.
   *
   * @param idContact l'ID du contact à récupérer.
   * @param version   la version du contact.
   * @return un objet ContactDTO correspondant à l'ID spécifié, null si aucun n'est trouvé
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  ContactDTO getOne(int idContact, int version);

  /**
   * Récupère un objet ContactDTO correspondant au contact ayant l'ID spécifié.
   *
   * @param idContact l'ID du contact à récupérer
   * @return un objet ContactDTO correspondant à l'ID spécifié, null si aucun n'est trouvé
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  ContactDTO getOne(int idContact);

  /**
   * Vérifie si un contact entre une société et un étudiant pour une année académique donnée
   * n'existe pas déjà.
   *
   * @param company      l'ID de l'entreprise.
   * @param student      l'ID de l'étudiant.
   * @param academicYear l'année académique.
   * @return true si le contact n'existe pas déjà, false sinon
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  boolean getOne(int company, int student, String academicYear);

  /**
   * Vérifie si l'utilisateur a déjà un contact accepté.
   *
   * @param idUser l'ID de l'utilisateur.
   * @return true si l'utilisateur a déjà un contact accepté, false sinon.
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  boolean getOneAcceptedContact(int idUser);

  /**
   * Obtient la liste des contacts associés à un utilisateur spécifié.
   *
   * @param userId     l'ID de l'utilisateur pour lequel obtenir les contacts.
   * @param isFollowed true si les contacts suivis uniquement, false sinon.
   * @return la liste des contacts de l'utilisateur spécifié, null si aucun n'est trouvé.
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  List<ContactDTO> getAllByUser(int userId, boolean isFollowed);

  /**
   * Obtient la liste des contacts associés à une entreprise spécifiée.
   *
   * @param companyId l'ID de l'entreprise pour laquelle obtenir les contacts.
   * @return la liste des contacts de l'entreprise spécifiée, null si aucun n'est trouvé.
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  List<ContactDTO> getAllByCompany(int companyId);

  /**
   * Récupère la liste de tous les contacts.
   *
   * @return Liste de tous les contacts.
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  List<ContactDTO> getAll();

  /**
   * Ajoute un nouveau contact entre une société et un étudiant pour une année académique donnée.
   *
   * @param company l'ID de l'entreprise.
   * @param student l'ID de l'étudiant
   * @return le contact ajouté, null si l'opération a échoué
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  ContactDTO insert(int company, int student);

  /**
   * Mets à jour un contact en réalisant un PUT.
   *
   * @param contactDTO le contact contenant toutes les informations.
   * @return le contact mis à jour.
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  ContactDTO update(ContactDTO contactDTO);

  /**
   * Suspend tous les contacts d'un étudiant spécifié.
   *
   * @param idStudent l'ID de l'étudiant dont les contacts doivent être suspendus.
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  void suspendAllContactsOfStudent(int idStudent);

  /**
   * Blackliste tous les contacts d'une entreprise spécifiée.
   *
   * @param idCompany l'ID de l'entreprise dont les contacts doivent être blacklistés.
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  void blacklistAllContactsOfCompany(int idCompany);
}
