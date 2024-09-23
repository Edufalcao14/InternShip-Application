package be.vinci.pae.business.ucc.supervisor;

import be.vinci.pae.business.dto.SupervisorDTO;
import be.vinci.pae.utils.exceptions.BusinessException;
import java.util.List;

/**
 * L'interface SupervisorUCC (User Control Component) définit les opérations liées à la gestion des
 * responsables de stage.
 */
public interface SupervisorUCC {

  /**
   * Récupère la liste de tous les responsables de stage.
   *
   * @return Liste de tous les responsables de stage.
   */
  List<SupervisorDTO> getAllSupervisors();

  /**
   * Ajoute un nouveau responsable.
   *
   * @param supervisorDTO le responsable à ajouter.
   * @param companyId     l'ID de l'entreprise.
   * @return le responsable ajouté.
   * @throws BusinessException si une erreur métier survient.
   */
  SupervisorDTO addSupervisor(SupervisorDTO supervisorDTO, int companyId);

  /**
   * Récupère les responsables de l'entreprise passée en paramètre.
   *
   * @param companyId l'ID de l'entreprise.
   * @return liste des responsables d'une entreprise.
   * @throws BusinessException si l'entreprise est introuvable.
   */
  List<SupervisorDTO> getCompanySupervisors(int companyId);
}
