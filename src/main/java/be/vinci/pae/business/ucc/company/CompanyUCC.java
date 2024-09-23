package be.vinci.pae.business.ucc.company;

import be.vinci.pae.business.dto.CompanyDTO;
import be.vinci.pae.utils.exceptions.BusinessException;
import java.util.List;

/**
 * L'interface CompanyUCC (User Control Component) définit les opérations liées à la gestion des
 * entreprises.
 */
public interface CompanyUCC {

  /**
   * Récupère les informations de l'entreprise à partir de son ID.
   *
   * @param id ID de l'entreprise.
   * @return CompanyDTO contenant les informations de l'entreprise.
   * @throws BusinessException si l'entreprise est introuvable.
   */
  CompanyDTO getCompanyById(int id);

  /**
   * Récupère les informations de toutes les entreprises.
   *
   * @param academicYear l'année académique.
   * @param orderBy      l'ordre de tri.
   * @param descOrder    true si l'ordre doit être descendant, false sinon.
   * @return une liste contenant les informations de toutes les entreprises.
   * @throws BusinessException si l'année académique est invalide.
   */
  List<CompanyDTO> getAllCompanies(String academicYear, String orderBy, boolean descOrder);

  /**
   * Blackliste une entreprise en fonction de son id.
   *
   * @param companyId       ID de l'entreprise.
   * @param blacklistReason la raison du blacklist.
   * @param version         la version de l'entreprise.
   * @return CompanyDTO contenant les informations de l'entreprise modifiées.
   * @throws BusinessException si une erreur métier survient pendant la récupération de
   *                           l'entreprise.
   */
  CompanyDTO blacklistCompany(int companyId, String blacklistReason, int version);

  /**
   * Ajoute une entreprise à la base de données.
   *
   * @param companyDTO l'objet CompanyDTO représentant l'entreprise à ajouter.
   * @return l'entreprise ajoutée ou null si aucune entreprise n'est ajoutée.
   * @throws BusinessException si une erreur métier survient pendant la récupération de
   *                           l'entreprise.
   */
  CompanyDTO addCompany(CompanyDTO companyDTO);

}
