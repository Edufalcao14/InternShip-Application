function blacklistName(name, blacklisted) {
  if (blacklisted) {
    return `${name} ⚠️`;
  }
  return name;
}

function transformState2String(state) {
  switch (state) {
    case 'INITIE':
      return 'Initié';
    case 'PRIS':
      return 'Pris';
    case 'ACCEPTE':
      return 'Accepté';
    case 'SUSPENDU':
      return 'Suspendu';
    case 'REFUSE':
      return 'Refusé';
    case 'BLACKLISTE':
      return 'Blacklisté';
    case 'NON_SUIVI':
      return 'Non Suivi';
    default:
      return '/';
  }
}

function transformRole2String(role) {
  switch (role) {
    case 'ETUDIANT':
      return 'Étudiant';
    case 'ADMINISTRATIF':
      return 'Administratif';
    case 'PROFESSEUR':
      return 'Professeur';
    default:
      return '/';
  }
}

function transformPlace2String(place) {
  switch (place) {
    case 'DISTANCIEL':
      return 'Distanciel';
    case 'PRESENTIEL':
      return 'Présentiel';
    default:
      return '/';
  }
}

function formatDate(date) {
  // Création d'un objet Date à partir de la chaîne de date fournie
  const dateObj = new Date(date);

  // Récupération du jour, du mois et de l'année
  const jour = dateObj.getDate();
  // Les mois en JavaScript sont indexés à partir de 0, donc on ajoute 1 pour obtenir le mois correct
  const mois = dateObj.getMonth() + 1;
  const année = dateObj.getFullYear();

  // Création de la chaîne de date formatée
  const dateFormatee = `${jour} / ${mois} / ${année}`;

  return dateFormatee;
}

export {
  blacklistName,
  transformState2String,
  transformRole2String,
  transformPlace2String,
  formatDate
};