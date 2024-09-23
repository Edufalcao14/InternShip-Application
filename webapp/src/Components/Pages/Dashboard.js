import Chart from 'chart.js/auto';
import Awesomplete from 'awesomplete';
import Swal from 'sweetalert2';
import {
  blacklistCompany,
  getAllCompanies,
  getOneCompany
} from '../../requests/company';
import {clearPage} from "../../utils/render";
import {
  getConnectedUser,
  isConnected,
  isStudent
} from "../../utils/connectedUser";
import Navigate from "../Router/Navigate";
import {getAllContacts, getAllContactsByCompany} from "../../requests/contact";
import {getAllUsers} from "../../requests/user";
import {
  getNbrOfStudentsWithInternship,
  getNbrOfStudentsWithoutInternship,
} from "../../requests/internship";
import {
  blacklistName,
  transformPlace2String,
  transformRole2String,
  transformState2String,
} from "../../utils/utils";
import nonAuthorizedPopUp from "../../utils/NonAuthorizedPopUp";
import Navbar from "../Navbar/Navbar";

let stageChart = undefined;
let completion = undefined;

// Fonction pour afficher les utilisateurs dans le tableau
const displayUsersInTable = async (users) => {
  const tableBody = document.getElementById('userTableBody');

  tableBody.innerHTML = '';

  for (const user of users) {
    const newRow = tableBody.insertRow();

    // Ajouter les cellules avec les valeurs de l'utilisateur
    const lastNameCell = newRow.insertCell();
    lastNameCell.textContent = user.lastName;

    const firstNameCell = newRow.insertCell();
    firstNameCell.textContent = user.firstName;

    const roleCell = newRow.insertCell();
    roleCell.textContent = transformRole2String(user.role);

    const academicYearCell = newRow.insertCell();
    academicYearCell.textContent = user.academicYear;

    const hasInternshipCell = newRow.insertCell();
    if (user.role === 'ETUDIANT') {
      hasInternshipCell.textContent = user.hasInternship ? 'Oui' : 'Non';
      newRow.style.cursor = "pointer";
      newRow.addEventListener('click', () => {
        Navigate(`/studentprofile?id=${user.id}`);
      });
    } else {
      newRow.style.cursor = "not-allowed";
      hasInternshipCell.textContent = '/';
    }

  }
};

// Fonction pour afficher les entreprises dans le tableau
const displayCompaniesInTable = (companies) => {
  const tableBody = document.getElementById('companyTableBody');
  // Effacer le contenu précédent du tableau
  tableBody.innerHTML = '';
  // Parcourir chaque entreprise et ajouter une ligne dans le tableau
  companies.forEach(company => {
    const row = tableBody.insertRow();

    const isBlacklisted = company.blacklisted;

    const nameCell = row.insertCell();
    nameCell.textContent = blacklistName(company.name, isBlacklisted);

    const designationCell = row.insertCell();
    designationCell.textContent = company.designation ? company.designation
        : '/';

    const phoneNumberCell = row.insertCell();
    phoneNumberCell.textContent = company.phoneNumber;

    const studentsNumberCell = row.insertCell();
    studentsNumberCell.textContent = company.studentsNumber;

    const blacklistedCell = row.insertCell();
    const detailsCell = row.insertCell();
    detailsCell.style.wordBreak = 'break-word';
    if (isBlacklisted) {
      blacklistedCell.textContent = 'Oui';
    } else {
      blacklistedCell.textContent = 'Non';
    }
    addBlacklistButton(detailsCell, company);
  });

};

const constructModalContactTable = async (company) => {
  // Récupérer tous les contacts pour l'entreprise spécifiée
  const contacts = await getAllContactsByCompany(company.id);
  let tableHTML;
  if (company.blacklisted) {
    tableHTML = `
 <h3 style="color: #1581d0" class="mt-lg-3 pb-3">${company.name} </h3>
 <h4 class="mt-lg-3">Entreprise Blacklistée ⚠️ </h4>
    <h4 style="margin: 15px">Raison : ${company.blacklistReason}</h4>
    <div class="card-body" style="max-height: 400px; overflow-y: auto;">
    <h4 class="mt-lg-4 mb-lg-3 pb-4">Liste des contacts</h4>
      <table class="full-width-table">
        <thead>
        <tr>
          <th>ID du Contact</th>
          <th>Prénom</th>
          <th>Nom</th>
          <th>État</th>
          <th>Détails</th>
        </tr>
        </thead>
        <tbody>
        `;
  } else {
    tableHTML = `
  <div class="card-body" style="max-height: 400px; overflow-y: auto;">
  <h2 style="color: #1581d0" class="mt-lg-3 pb-3">${company.name} </h2>
 <h3 class="mt-lg-3 pb-3">Liste des contacts</h3>
    <table class="full-width-table">
      <thead>
        <tr>
          <th>ID du Contact</th>
          <th>Prénom</th>
          <th>Nom</th>
          <th>État</th>
          <th>Détails</th>
        </tr>
      </thead>
      <tbody>
  `;
  }

  // Parcourir chaque contact pour construire les lignes du tableau
  contacts.forEach(contact => {
    // Si la raison de refus est null, remplacer par "/"
    const stateString = transformState2String(contact.state);
    let details = '/';
    if (contact.state === 'REFUSE') {
      details = contact.refusalReason;
    } else if (contact.state === 'PRIS') {
      details = transformPlace2String(contact.meetingPlace);
    }

    // Ajouter la ligne du tableau avec les informations récupérées
    tableHTML += `
      <tr>
        <td>#${contact.id}</td>
        <td>${contact.studentDTO.firstName}</td>
        <td>${contact.studentDTO.lastName}</td>
        <td>${stateString}</td>
        <td>${details}</td>
      </tr>
    `;
  });

  tableHTML += `
      </tbody>
    </table>
  </div>
  `;
  return tableHTML;
}

// Fonction pour ajouter le bouton de blacklistage à une ligne du tableau
const addBlacklistButton = (cell, company) => {
  const blacklistButton = document.createElement('button');
  blacklistButton.textContent = "Détails";
  blacklistButton.classList.add("btn", "btn-dark", "btn-sm");

  blacklistButton.addEventListener('click', async () => {
    const comp = await getOneCompany(company.id);
    if (company.blacklisted) {
      const {value: reason} = await Swal.fire({
        html: await constructModalContactTable(company),
        showCancelButton: true,
        cancelButtonText: 'Fermer',
        cancelButtonColor: '#1581d0',
        showConfirmButton: false,
        inputValidator: (value) => {
          if (!value) {
            return 'Vous devez saisir une raison pour le blacklistage.';
          }
        },
        customClass: {
          popup: 'custom-modal-class'
        }
      });
    } else {
      const {value: reason} = await Swal.fire({
        html: await constructModalContactTable(company),
        input: 'textarea',
        inputLabel: 'Saisissez la raison du blacklistage',
        inputPlaceholder: 'Entrez la raison...',
        showCancelButton: true,
        cancelButtonText: 'Annuler',
        confirmButtonText: 'Blacklister',
        confirmButtonColor: 'Black',
        inputValidator: (value) => {
          if (!value) {
            return 'Vous devez saisir une raison pour le blacklistage.';
          }
        },
        customClass: {
          popup: 'custom-modal-class'
        }
      });
      if (reason) {
        await blacklistCompany(company.id, reason, company.version);
        await Swal.fire({
          icon: "success",
          title: "L'entreprise a été blacklistée avec succès!",
          timer: 2000,
          timerProgressBar: true,
          showConfirmButton: false,
        }).then(async () => {
          await updateCompanyTable();
          await updateContactsTable();
        });
      }
    }
  });
  cell.appendChild(blacklistButton);
};

const updateCompanyTable = async () => {
  const main = document.querySelector('main');
  try {
    const academicYearSelectCompany = document.getElementById(
        'academicYearSelectCompany');
    const academicYear = academicYearSelectCompany.value;

    // Récupérer les entreprises depuis le backend
    const {companies, error: companiesError} = await getAllCompanies(
        academicYear
    );

    if (companiesError) {
      console.error(companiesError);
      main.innerHTML = `<p style="margin: 10px">⚠️ Une erreur s'est produite lors de la récupération des entreprises : ${companiesError}</p>`;
      return;
    }

    // Récupérer la valeur sélectionnée pour le tri
    const sortBySelect = document.getElementById('sortBySelect');
    const sortBy = sortBySelect.value;

    // Trier les entreprises en fonction du critère sélectionné
    let sortedCompanies =
        sortedCompanies = (await getAllCompanies(academicYear,
            sortBy)).companies;

    await displayCompaniesInTable(sortedCompanies);
  } catch (error) {
    console.error('Erreur lors de la mise à jour de la table des entreprises :',
        error);
  }
};

const sortCompaniesByNumberOfStudents = (a, b) => {
  const numberOfStudentsA = a.studentsNumber;
  const numberOfStudentsB = b.studentsNumber;
  return numberOfStudentsB - numberOfStudentsA;
};

// Fonction pour mettre à jour la table des contacts
const updateContactsTable = async () => {
  const main = document.querySelector('main');
  try {
    // Récupérer les entreprises depuis le backend
    const {contacts, error: contactsError} = await getAllContacts();

    if (contactsError) {
      console.error(contactsError);
      main.innerHTML = `<p style="margin: 10px">⚠️ Une erreur s'est produite lors de la récupération des contacts : ${contactsError}</p>`;
      return;
    }
    await displayContactsInTable(contacts);
  } catch (error) {
    console.error('Erreur lors de la mise à jour de la table des contacts :',
        error);
  }
};

const displayContactsInTable = (contacts) => {
  const tableBody = document.getElementById('contactTableBody');
  tableBody.innerHTML = '';
  contacts.forEach(contact => {
    const newRow = tableBody.insertRow();

    const idContact = newRow.insertCell();
    idContact.textContent = '#' + contact.id;

    const firstNameCell = newRow.insertCell();
    firstNameCell.textContent = contact.studentDTO.firstName;

    const lastNameCell = newRow.insertCell();
    lastNameCell.textContent = contact.studentDTO.lastName;

    const companyNameCell = newRow.insertCell();
    let companyName;
    if (contact.companyDTO.designation) {
      companyName = `${contact.companyDTO.name} : ${contact.companyDTO.designation}`;

    } else {
      companyName = `${contact.companyDTO.name}`;
    }
    companyName = blacklistName(companyName, contact.companyDTO.blacklisted);
    companyNameCell.textContent = companyName;

    const stateCell = newRow.insertCell();
    stateCell.textContent = transformState2String(contact.state);

    const detailsCell = newRow.insertCell();
    if (contact.state === "REFUSE") {
      detailsCell.textContent = contact.refusalReason ? contact.refusalReason
          : '/';
    } else if (contact.state === "PRIS") {
      detailsCell.textContent = contact.meetingPlace ? transformPlace2String(
              contact.meetingPlace)
          : '/';
    } else {
      detailsCell.textContent = '/';
    }

  });
};

// Fonction pour filtrer les utilisateurs selon l'annee academique
const filterUsersByAcademicYear = async (users) => {
  const academicYearSelect = document.getElementById('academicYearSelect');
  const selectedAcademicYear = academicYearSelect.value;
  const filteredUsers = users.filter(user =>
      !selectedAcademicYear || selectedAcademicYear === "all"
      || user.academicYear === selectedAcademicYear
  );

  await displayUsersInTable(filteredUsers);
};

// Fonction pour filtrer les utilisateurs selon leur role
const filterUsersByRole = async (users) => {
  const roleSelect = document.getElementById('roleSelect');
  const selectedRole = roleSelect.value.toUpperCase();
  const filteredUsers = users.filter(user =>
      selectedRole === "ALL" || user.role === selectedRole
  );
  await displayUsersInTable(filteredUsers);
}

// Fonction pour gérer le changement dans le champ de recherche
const handleSearchInputChange = async (users) => {
  const searchInput = document.getElementById('searchInput');
  const searchTerm = searchInput.value.trim().toLowerCase();

  // Récupérer la valeur de l'année académique sélectionnée
  const academicYearSelect = document.getElementById('academicYearSelect');
  const selectedAcademicYear = academicYearSelect.value;

  // Filtrer les utilisateurs en fonction du terme de recherche et de l'année académique sélectionnée
  const filteredUsers = users.filter(user => {
    const fullName = `${user.firstName.toLowerCase()} ${user.lastName.toLowerCase()}`;
    const fullNameBis = `${user.lastName.toLowerCase()} ${user.firstName.toLowerCase()}`;
    return (
        (user.lastName.toLowerCase().includes(searchTerm) ||
            user.firstName.toLowerCase().includes(searchTerm) ||
            fullName.includes(searchTerm) ||
            fullNameBis.includes(searchTerm)) &&
        (!selectedAcademicYear || selectedAcademicYear === "all"
            ||
            user.academicYear === selectedAcademicYear)
    );
  });

  // Afficher les utilisateurs filtrés dans le tableau
  if (filteredUsers.length > 0) {
    await displayUsersInTable(filteredUsers);
  } else {
    const tableBody = document.getElementById('userTableBody');
    tableBody.innerHTML = `<tr><td colspan="4">⚠️ Aucun utilisateur ne correspond aux critères de recherche. </td></tr>`;
  }

  // Mettre à jour la liste de suggestions pour l'autocomplétion
  const firstNames = filteredUsers.map(user => user.firstName.toLowerCase());
  const lastNames = filteredUsers.map(user => user.lastName.toLowerCase());

  const uniqueSuggestionsSet = new Set([...firstNames, ...lastNames]);
  const uniqueSuggestions = Array.from(uniqueSuggestionsSet);

  searchInput.setAttribute('data-list', uniqueSuggestions.join(','));
  completion.list = uniqueSuggestions;
};

// Fonction pour afficher le graphique des stages
const displayStageChart = async (year) => {
  const stageChartCanvas = document.getElementById('stageChart');

  const xValues = ["Avec Stage", "Sans Stage"];
  const yValues = []; // Tableau pour stocker le nombre d'étudiants avec et sans stage

  const withStageCount = await getNbrOfStudentsWithInternship(year);
  const withoutStageCount = await getNbrOfStudentsWithoutInternship(year);

  const loadingChartData = document.getElementById('loadingChartData');
  loadingChartData.style.display = 'none';

  const valueWith = withStageCount.nbr;
  const valueWithout = withoutStageCount.nbr;

  // Vérifier si l'un des nombres est égal à 0
  if (valueWith === 0 && valueWithout === 0) {
    if (stageChart !== undefined) {
      stageChart.destroy();
    }
    stageChartCanvas.style.display = 'none';
    // Afficher le message
    const noDataMessage = document.getElementById('noDataMessage');
    noDataMessage.style.display = 'block';
    return;
  }

  // Remplir le tableau yValues avec les nombres calculés
  yValues.push(valueWith, valueWithout);
  const barColors = ["#b91d47", "#00aba9"];

  if (stageChart !== undefined) {
    stageChart.destroy();
  }
  stageChart = new Chart(stageChartCanvas, {
    type: "pie",
    data: {
      labels: xValues,
      datasets: [{
        backgroundColor: barColors,
        data: yValues
      }]
    },
    options: {
      aspectRatio: 1.9,
    }
  });

  return stageChart;
};

const renderDashboardPage = async () => {
  clearPage();

  if (!isConnected()) {
    Navigate('/');
    return;
  }

  const isAStudent = await isStudent();
  if (isAStudent) {
    nonAuthorizedPopUp()
    return Navigate('/studentboard');
  }

  await Navbar();

  const main = document.querySelector('main');

  try {

    const adminHtml = `
 <section class="d-flex py-4 py-xl-5 w-100">
  <div class="container-fluid px-0" style="margin: 0px 10%; ">
    <div class="row align-items-center justify-content-center">
     <div class="text-center mb-3">
          <h1 >Recherche utilisateur</h1>
        </div>
        <div class="mb-3">
          <label for="academicYearSelect" class="form-label">Filtrer par année académique :</label>
          <select class="form-select" id="academicYearSelect">
            <option value="all">Toutes les années</option>
            <option value="2023-2024">2023-2024</option>
            <option value="2022-2023">2022-2023</option>
            <option value="2021-2022">2021-2022</option>
            <option value="2020-2021">2020-2021</option>
          </select>
        </div>     
         <div class="mb-lg-6">
          <label for="roleSelect" class="form-label">Filtrer par rôle :</label>
          <select class="form-select" id="roleSelect">
            <option value="all">Tous les rôles</option>
            <option value="Etudiant">Étudiant</option>
            <option value="Professeur">Professeur</option>
            <option value="Administratif">Administratif</option>
          </select>
        </div>
        <div class="mb-3">
          <label for="searchInput" class="form-label">Rechercher par nom ou prénom :</label>
          <input type="text" class="form-control" id="searchInput" placeholder="Entrez le nom ou le prénom">
        </div>
        <div class="card mb-6">
          <div class="card-body" style="height: 500px; overflow-y: auto;">
            <table class="table text-center">
              <thead>
                <tr>
                  <th>Nom</th>
                  <th>Prénom</th>
                  <th>Rôle</th>
                  <th>Année académique</th>
                  <th>Stagiaire</th>
                </tr>
              </thead>
              <tbody id="userTableBody">
                <tr>
                  <td class="loading-animation" colspan="5">Chargement des données</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
  </div>
</section>
`;
    const professeurHTMl = `
     
     <section class="d-flex py-4 py-xl-5 ">
  <div class="container-fluid px-0" style="margin: 0px 5%">
    <div class="row align-items-center justify-content-center">
      <div class="col-lg-6">
        <div class="text-center mb-3  ">
          <h1>Graphique des stages</h1>
        </div>
        <div class="mb-3 ">
          <label for="academicYearSelectGraph" class="form-label">Filtrer par année académique :</label>
          <select class="form-select" id="academicYearSelectGraph">
            <option value="2023-2024">2023-2024</option>
            <option value="2022-2023">2022-2023</option>
            <option value="2021-2022">2021-2022</option>
            <option value="2020-2021">2020-2021</option>
          </select>
        </div>
        
        <div class="card mb-5">
          <div class="card-body d-flex flex-column align-items-center" style="height: 525px">
            <div id="loadingChartData">
              <p class="loading-animation">Chargement des données</p>
            </div>
            <canvas id="stageChart" style="width:100%;"></canvas>
            <div id="noDataMessage" style="display: none;">
               <p>Aucune donnée disponible pour afficher le graphique.</p>
            </div>
          </div>
        </div>
        
        <div class="text-center mb-3">
          <h1 >Recherche utilisateur</h1>
        </div>
        <div class="mb-3">
          <label for="academicYearSelect" class="form-label">Filtrer par année académique :</label>
          <select class="form-select" id="academicYearSelect">
            <option value="all">Toutes les années</option>
            <option value="2023-2024">2023-2024</option>
            <option value="2022-2023">2022-2023</option>
            <option value="2021-2022">2021-2022</option>
            <option value="2020-2021">2020-2021</option>
          </select>
        </div>
        <div class="mb-3">
          <label for="roleSelect" class="form-label">Filtrer par rôle :</label>
          <select class="form-select" id="roleSelect">
            <option value="all">Tous les rôles</option>
            <option value="Etudiant">Étudiant</option>
            <option value="Professeur">Professeur</option>
            <option value="Administratif">Administratif</option>
          </select>
        </div>
        <div class="mb-3 ">
          <label for="searchInput" class="form-label">Rechercher par nom ou prénom :</label>
          <input type="text" class="form-control" id="searchInput" placeholder="Entrez le nom ou le prénom">
        </div>
        <div class="card mb-6">
          <div class="card-body" style="height: 400px; overflow-y: auto;">
            <table class="table text-center">
              <thead>
                <tr>
                  <th>Nom</th>
                  <th>Prénom</th>
                  <th>Rôle</th>
                  <th>Année académique</th>
                  <th>Stagiaire</th>
                </tr>
              </thead>
              <tbody id="userTableBody">
                <tr>
                  <td class="loading-animation" colspan="5">Chargement des données</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
        
      </div>

      <div class="col-lg-6" style="height: 148vh">
        <div class="text-center mb-lg-4">
          <h1>Tableau des Entreprises</h1>
        </div>
        <div class="mb-3">
          <label for="sortBySelect" class="form-label">Trier par :</label>
          <select  class="form-select" id="sortBySelect">
            <option  value="name">Nom</option>
            <option  value="-name">Nom (desc)</option>
            <option  value="-is_blacklisted">Blacklist</option>
            <option  value="is_blacklisted">Blacklist (desc)</option>
            <option  value="designation">Appellation</option>
            <option  value="-designation">Appellation (desc)</option>
            <option  value="phone_number">Télephone</option>
            <option  value="-phone_number">Télephone (desc)</option>
            <option  value="students_nbr">Nombre d'étudiants</option>      
            <option  value="-students_nbr">Nombre d'étudiants (desc)</option>
            <option  value="">--</option> 
          </select>
        </div>
        <div class="mb-3">
          <label for="academicYearSelectCompany" class="form-label">Choisir l'année académique :</label>
          <select  class="form-select" id="academicYearSelectCompany">
            <option value="2023-2024">2023-2024</option>
            <option value="2022-2023">2022-2023</option>
            <option value="2021-2022">2021-2022</option>
            <option value="2020-2021">2020-2021</option>
            <option value="">Toutes les années</option>
          </select>
        </div>
        <div class="card mb-5">
          <div class="card-body " style="height: 430px; overflow-y: auto;">
            <table class="table text-center">
              <thead>
                <tr>
                  <th style="width: 16%">Nom</th>
                  <th style="width: 16%">Appellation</th>
                  <th style="width: 16%">Téléphone</th>
                  <th style="width: 16%">Nombre Étudiants</th>
                  <th style="width: 16%">Blacklisté</th>
                  <th style="width: 16%"></th>
                </tr>
              </thead>
              <tbody id="companyTableBody">
                <tr>
                  <td class="loading-animation" colspan="6">Chargement des données</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      
        <div class="text-center mb-lg-4" style="margin-top:5%!important;">
          <h1>Liste des contacts</h1>
        </div>
        <div class="card mb-6" style="margin-top:5%!important;">
          <div class="card-body" style="max-height: 620px; overflow-y: auto;">
            <table class="table text-center">
              <thead>
                <tr>
                  <th>ID du Contact</th>
                  <th>Prénom</th>
                  <th>Nom</th>
                  <th>Entreprise</th>
                  <th>État du Contact</th>
                  <th>Détails</th>
                </tr>
              </thead>
              <tbody id="contactTableBody">
                <tr>
                  <td class="loading-animation" colspan="6">Chargement des données</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
        
      </div>
    </div>
  </div>
</section>
`;
    let currentUser = await getConnectedUser();
    if (currentUser.role === 'ADMINISTRATIF') {
      main.innerHTML = adminHtml;
    } else {
      main.innerHTML = professeurHTMl;
    }
    main.style.height = `calc(100vh - 5vw)`;

    const {users, error} = await getAllUsers();

    if (error) {
      main.innerHTML = `<p style="margin: 10px">⚠️ Une erreur s'est produite lors de la récupération des utilisateurs : ${error}</p>`;
      return;
    }

    // gestionnaire d'événements pour le champ de recherche
    const searchInput = document.getElementById('searchInput');
    searchInput.addEventListener('input',
        async () => await handleSearchInputChange(users));

    // gestionnaire d'événements pour le menu déroulant d'année académique
    const academicYearSelect = document.getElementById('academicYearSelect');
    academicYearSelect.addEventListener('change',
        () => filterUsersByAcademicYear(users));

    // gestionnaire d'événements pour le menu déroulant d'année académique
    const roleSelect = document.getElementById('roleSelect');
    roleSelect.addEventListener('change',
        () => filterUsersByRole(users));

    // Ajout de la fonctionnalité de complétion automatique
    const firstNames = users.map(user => user.firstName.toLowerCase());
    const lastNames = users.map(user => user.lastName.toLowerCase());

    const suggestions = [...firstNames, ...lastNames];

    completion = new Awesomplete(searchInput, {
      list: suggestions,
      minChars: 3,
      autoFirst: false,
      tabSelect: true
    });
    searchInput.addEventListener("awesomplete-select", function (event) {
      // Récupérer la valeur sélectionnée dans l'autocomplétion
      const selectedValue = event.text.value;

      // Filtrer les utilisateurs en fonction du nom ou prénom sélectionné
      const filteredUsers = users.filter(user =>
          user.firstName.toLowerCase() === selectedValue.toLowerCase() ||
          user.lastName.toLowerCase() === selectedValue.toLowerCase()
      );
      displayUsersInTable(filteredUsers);
    });

    searchInput.addEventListener("keypress", function (event) {
      // Récupération de la touche pressée
      const key = event.keyCode;
      if (key === 38) {
        completion.previous();
      }
      if (key === 40) {
        completion.next();
      }
      if (key === 13) {
        completion.select();
      }
    });

    if (currentUser.role === 'ADMINISTRATIF') {
      await displayUsersInTable(users);

    } else {
      // récupérer les contacts depuis le backend
      const {contacts, error: contactsError} = await getAllContacts();

      if (contactsError) {
        console.error(contactsError);
        main.innerHTML = `<p style="margin: 10px">⚠️ Une erreur s'est produite lors de la récupération des contacts : ${contactsError}</p>`;
        return;
      }
      await updateCompanyTable();
      const sortBySelect = document.getElementById('sortBySelect');
      sortBySelect.addEventListener('change', async () => {
        await updateCompanyTable();
      })
      const academicYearSelectCompany = document.getElementById(
          'academicYearSelectCompany');
      academicYearSelectCompany.addEventListener('change',
          async () => await updateCompanyTable());

      // Ajouter un écouteur d'événements sur le changement de valeur de l'année académique du graph
      const academicYearSelectGraph = document.getElementById(
          'academicYearSelectGraph')
      academicYearSelectGraph.addEventListener('change', async () => {
        // Récupérer l'année sélectionnée
        const selectedYear = academicYearSelectGraph.value; // Cette valeur sera de type String

        const noDataMessage = document.getElementById('noDataMessage');
        const stageChartCanvas = document.getElementById('stageChart');
        stageChartCanvas.style.display = 'block';
        noDataMessage.style.display = 'none';

        stageChart = await displayStageChart(selectedYear);
      });
      await displayUsersInTable(users);
      await displayContactsInTable(contacts);
      await displayStageChart(academicYearSelectGraph.value);
    }

  } catch (error) {
    console.error(error);
    main.innerHTML = `<p>Une erreur s'est produite lors de la récupération des données.</p>`;
  }
};

// Fonction principale pour afficher le tableau de bord des utilisateurs
const Dashboard = async () => {
  await renderDashboardPage();
};

export default Dashboard;
