package be.vinci.pae.business.dto;

import be.vinci.pae.business.domain.company.CompanyImpl;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * L'interface CompanyDTO (Data Transfer Object) définit la structure des données liées à une
 * entreprise dans le contexte de transfert de données.
 */
@JsonDeserialize(as = CompanyImpl.class)
public interface CompanyDTO {

  /**
   * Renvoie l'ID de l'entreprise.
   *
   * @return l'ID de l'entreprise.
   */
  int getId();

  /**
   * Modifie l'ID de l'entreprise.
   *
   * @param id le nouvel ID de l'entreprise.
   */
  void setId(int id);

  /**
   * Renvoie le nom de l'entreprise.
   *
   * @return le nom de l'entreprise.
   */
  String getName();

  /**
   * Modifie le nom de l'entreprise.
   *
   * @param name le nouveau nom de l'entreprise.
   */
  void setName(String name);

  /**
   * Renvoie l'appellation de l'entreprise.
   *
   * @return l'appellation de l'entreprise.
   */
  String getDesignation();

  /**
   * Modifie l'appellation de l'entreprise.
   *
   * @param designation la nouvelle appellation de l'entreprise.
   */
  void setDesignation(String designation);

  /**
   * Renvoie l'adresse de l'entreprise.
   *
   * @return l'adresse de l'entreprise.
   */
  String getAddress();

  /**
   * Modifie l'adresse de l'entreprise.
   *
   * @param address la nouvelle adresse de l'entreprise.
   */
  void setAddress(String address);

  /**
   * Renvoie l'email de l'entreprise.
   *
   * @return l'email de l'entreprise.
   */
  String getEmail();

  /**
   * Modifie l'email de l'entreprise.
   *
   * @param email le nouvel email de l'entreprise.
   */
  void setEmail(String email);

  /**
   * Renvoie le numéro de téléphone de l'entreprise.
   *
   * @return le numéro de téléphone de l'entreprise.
   */
  String getPhoneNumber();

  /**
   * Modifie le numéro de téléphone de l'entreprise.
   *
   * @param phoneNumber le nouveau numéro de téléphone de l'entreprise.
   */
  void setPhoneNumber(String phoneNumber);

  /**
   * Renvoie la raison de blacklistage de l'entreprise.
   *
   * @return la raison de blacklistage de l'entreprise.
   */
  String getBlacklistReason();

  /**
   * Modifie la raison de blacklistage de l'entreprise.
   *
   * @param blacklistReason la nouvelle raison de blacklistage de l'entreprise.
   */
  void setBlacklistReason(String blacklistReason);

  /**
   * Renvoie true si l'entreprise est blacklistée, false sinon.
   *
   * @return true si l'entreprise est blacklistée, false sinon.
   */
  boolean isBlacklisted();

  /**
   * Modifie le statut de blacklistage de l'entreprise.
   *
   * @param isBlacklisted le nouveau statut de blacklistage de l'entreprise.
   */
  void setBlacklisted(boolean isBlacklisted);

  /**
   * Renvoie la version de l'entreprise.
   *
   * @return la version de l'entreprise.
   */
  int getVersion();

  /**
   * Modifie la version de l'entreprise.
   *
   * @param version la nouvelle version de l'entreprise.
   */
  void setVersion(int version);

  /**
   * Renvoie le nombre d'étudiants qui ont un stage dans cette entreprise.
   *
   * @return le nombre d'étudiants qui ont un stage dans cette entreprise.
   */
  int getStudentsNumber();

  /**
   * Modifie le nombre d'étudiants qui ont un stage dans cette entreprise.
   *
   * @param number le nouveau nombre d'étudiants qui ont un stage dans cette entreprise.
   */
  void setStudentsNumber(int number);
}
