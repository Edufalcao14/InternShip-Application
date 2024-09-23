package be.vinci.pae.business.domain.internship;

import be.vinci.pae.business.dto.InternshipDTO;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * L'interface Internship représente un stage dans le domaine métier.
 */
@JsonDeserialize(as = InternshipImpl.class)
public interface Internship extends InternshipDTO {

  /**
   * Vérifie la validité d'une date de signature. La date doit être au format "année-mois-jour"
   * (yyyy-MM-dd) et être cohérente.
   *
   * @param signatureDate La date de signature à vérifier.
   * @return true si la date est valide et au bon format, false sinon.
   */
  boolean checkSignatureDateValidity(String signatureDate);
}
