package be.vinci.pae.business;

import be.vinci.pae.business.dto.CompanyDTO;
import be.vinci.pae.business.dto.ContactDTO;
import be.vinci.pae.business.dto.InternshipDTO;
import be.vinci.pae.business.dto.SupervisorDTO;
import be.vinci.pae.business.dto.UserDTO;

/**
 * L'interface Factory fournit des méthodes pour créer des instances d'objets du domaine métier.
 */
public interface Factory {

  /**
   * Crée et renvoie une nouvelle instance de l'interface UserDTO.
   *
   * @return une nouvelle instance de UserDTO
   */
  UserDTO getUserDTO();

  /**
   * Crée et renvoie une nouvelle instance de l'interface CompanyDTO.
   *
   * @return une nouvelle instance de CompanyDTO
   */
  CompanyDTO getCompanyDTO();

  /**
   * Crée et renvoie une nouvelle instance de l'interface ContactDTO.
   *
   * @return une nouvelle instance de ContactDTO
   */
  ContactDTO getContactDTO();

  /**
   * Crée et renvoie une nouvelle instance de l'interface SupervisorDTO.
   *
   * @return une nouvelle instance de SupervisorDTO
   */
  SupervisorDTO getSupervisorDTO();

  /**
   * Crée et renvoie une nouvelle instance de l'interface InternshipDTO.
   *
   * @return une nouvelle instance de InternshipDTO
   */
  InternshipDTO getInternshipDTO();
}
