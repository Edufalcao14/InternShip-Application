package be.vinci.pae.utils;

import java.time.LocalDate;

/**
 * Classe regroupant des fonctions utilitaires.
 */
public class Utils {

  /**
   * Renvoie l'année académique courante.
   *
   * @return l'année académique courante
   */
  public static String calculateAcademicYear() {
    LocalDate date = LocalDate.now();
    int month = date.getMonthValue();
    int actualYear = date.getYear();
    if (month >= 9) {
      return actualYear + "-" + actualYear + 1;
    }
    return actualYear - 1 + "-" + actualYear;
  }

  /**
   * Vérifie si les années académiques sont bien l'une à la suite de l'autre.
   *
   * @param year l'année académique à vérifier
   * @return true si les années se suivent, false sinon
   */
  public static boolean validateAcademicYear(String year) {
    if (!year.matches("\\d{4}-\\d{4}")) {
      return false;
    }
    String[] years = year.split("-");
    int startYear = Integer.parseInt(years[0]);
    int endYear = Integer.parseInt(years[1]);

    return endYear == startYear + 1;
  }


}
