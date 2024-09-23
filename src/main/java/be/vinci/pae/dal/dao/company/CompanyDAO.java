package be.vinci.pae.dal.dao.company;

import be.vinci.pae.business.dto.CompanyDTO;
import be.vinci.pae.utils.exceptions.FatalException;
import be.vinci.pae.utils.exceptions.FatalExceptionType;
import java.util.List;

/**
 * L'interface CompanyDAO définit les méthodes pour accéder et manipuler les données des entreprises
 * dans la base de données.
 */
public interface CompanyDAO {

  /**
   * Récupère un objet CompanyDTO correspondant à l'entreprise ayant l'email spécifié.
   *
   * @param email l'email de l'entreprise.
   * @return un objet CompanyDTO représentant l'entreprise, ou null si aucune entreprise trouvée.
   * @throws FatalException si une erreur de DB se produit.
   */
  CompanyDTO getOne(String email);

  /**
   * Récupère un objet CompanyDTO correspondant à l'entreprise ayant l'ID spécifié.
   *
   * @param id l'ID de l'entreprise.
   * @return un objet CompanyDTO représentant l'entreprise, ou null si aucune entreprise trouvée.
   * @throws FatalException si une erreur de DB se produit.
   */
  CompanyDTO getOne(int id);

  /**
   * Récupère un objet CompanyDTO correspondant à l'entreprise ayant l'ID spécifié.
   *
   * @param companyId l'ID de l'entreprise.
   * @param version   la version de l'entreprise.
   * @return un objet CompanyDTO représentant l'entreprise, ou null si aucune entreprise trouvée.
   * @throws FatalException si une erreur de DB se produit.
   */
  CompanyDTO getOne(int companyId, int version);

  /**
   * Récupère un objet CompanyDTO correspondant à l'entreprise ayant le nom et l'appellation
   * spécifiés.
   *
   * @param name        le nom de l'entreprise
   * @param designation l'appellation de l'entreprise
   * @return un objet CompanyDTO représentant l'entreprise, ou null si aucune entreprise trouvée.
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  CompanyDTO getOne(String name, String designation);

  /**
   * Récupère la liste de tous les objets CompanyDTO avec le nombre d'étudiants qui ont un stage au
   * sein de l'entreprise (toutes années confondues) triée selon les paramètres fournis, si
   * fournis.
   *
   * @param orderBy   l'ordre de tri.
   * @param descOrder true si l'ordre de tri doit être descendant, false sinon.
   * @return la liste de tous les objets CompanyDTO.
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  List<CompanyDTO> getAll(String orderBy, boolean descOrder);

  /**
   * Récupère la liste de tous les objets CompanyDTO avec le nombre d'étudiants qui ont un stage au
   * sein de l'entreprise triée selon le paramètre fourni et filtrée selon l'année académique
   * fournie.
   *
   * @param academicYear l'année académique pour laquelle il faut le nombre d'étudiants.
   * @param orderBy      l'ordre de tri
   * @param descOrder    true si l'ordre de tri doit être descendant, false sinon.
   * @return la liste de tous les objets CompanyDTO.
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  List<CompanyDTO> getAll(String academicYear, String orderBy, boolean descOrder);

  /**
   * Blackliste une entreprise selon son ID.
   *
   * @param companyDTO    l'entreprise à blacklister.
   * @param refusalReason la raison du blacklist.
   * @return le CompanyDTO de l'entreprise blacklistée.
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  CompanyDTO blacklistOneCompany(CompanyDTO companyDTO, String refusalReason);

  /**
   * Ajoute une entreprise à la base de données.
   *
   * @param companyDTO l'objet CompanyDTO représentant l'entreprise à ajouter.
   * @return l'entreprise ajoutée ou null si aucune entreprise n'est ajoutée.
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  CompanyDTO insert(CompanyDTO companyDTO);

}

