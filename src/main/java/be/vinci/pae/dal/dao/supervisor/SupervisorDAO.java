package be.vinci.pae.dal.dao.supervisor;

import be.vinci.pae.business.dto.SupervisorDTO;
import be.vinci.pae.utils.exceptions.FatalException;
import be.vinci.pae.utils.exceptions.FatalExceptionType;
import java.util.List;

/**
 * L'interface SupervisorDAO définit les méthodes pour accéder et manipuler les données des
 * responsables de stage dans la base de données.
 */
public interface SupervisorDAO {

  /**
   * Récupère la liste de tous les objets SupervisorDTO.
   *
   * @return la liste de tous les objets SupervisorDTO
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  List<SupervisorDTO> getAll();

  /**
   * Récupère la liste des objets SupervisorDTO d'une entreprise.
   *
   * @param companyId l'ID de l'entreprise.
   * @return la liste des objets SupervisorDTO d'une entreprise.
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  List<SupervisorDTO> getAllByCompanyId(int companyId);

  /**
   * Récupère le responsable avec l'ID donné.
   *
   * @param id l'ID du responsable.
   * @return le SupervisorDTO trouvé.
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  SupervisorDTO getOne(int id);

  /**
   * Ajoute en DB le responsable.
   *
   * @param supervisorDTO le responsable à ajouter.
   * @return le responsable ajouté.
   */
  SupervisorDTO insert(SupervisorDTO supervisorDTO);

  /**
   * Récupère un responsable grâce au numéro de téléphone et l'ID de l'entreprise.
   *
   * @param supervisorDTO le responsable contentant les informations nécessaires.
   * @return le responsable.
   * @throws FatalException {@link FatalExceptionType#DATABASE} si une erreur de DB se produit.
   */
  SupervisorDTO getOneByCompany(SupervisorDTO supervisorDTO);
}
