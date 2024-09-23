import Swal from 'sweetalert2'
import Navigate from '../Router/Navigate';
import {clearPage} from '../../utils/render';
import {
  blacklistName,
  formatDate,
  transformPlace2String,
  transformState2String
} from "../../utils/utils";
import {getConnectedUser, isConnected} from '../../utils/connectedUser';
import profileIcon from '../../img/profileIcon.svg';
import emailIcon from '../../img/emailIcon.svg';
import phoneNumberIcon from '../../img/phoneNumberIcon.svg';

import {getAllContactsByUser} from "../../requests/contact";
import {getUserById} from "../../requests/user";
import Navbar from "../Navbar/Navbar";
import nonAuthorizedPopUp from "../../utils/NonAuthorizedPopUp";
import {getStageByStudentID} from "../../requests/internship";

const ProfileStudentPage = async () => {

  await Navbar();

  const urlParams = new URLSearchParams(window.location.search);
  const userId = urlParams.get('id');

  const connectedUser = await getConnectedUser();
  if (connectedUser.role === "ETUDIANT") {
    nonAuthorizedPopUp();
    return Navigate('/');
  }

  let user = await getUserById(userId);
  const stage = await getStageByStudentID(userId);

  if (!user || user.error) {
    Swal.fire({
      toast: true,
      position: "center",
      text: user ? user.error : "User not found",
      icon: "error",
      button: false,
      timer: 3000,
      timerProgressBar: true,
      showConfirmButton: false,
    });
    return Navigate('/');
  }

  if (!isConnected()) {
    return Navigate('/');
  }

  const allContacts = await getAllContactsByUser(userId);

  clearPage();

  const divContainer = document.createElement('div');
  divContainer.className = "d-flex justify-content-center ";
  divContainer.style.height = "100%";

  const divPersonnalInformation = document.createElement('div');

  // language=HTML
  divPersonnalInformation.innerHTML = `
    <div id="personnalInfo"
         class="d-flex flex-column mb-3 p-5 shadow-lg p-3 mb-5 bg-white rounded  me-lg-5"
         style=" height: 100%;">
      <h1>Données Personelles</h1>
      <div class="d-flex flex-column " style="margin-top: 10% ">
        <div class="d-flex align-items-center  mb-3">
          <img class="me-lg-2" src="${profileIcon}" alt="Icon Profile"
               class="me-2">
          <h5 id="fName-h5" class="align-text-center pt-1 me-lg-2">
            ${user.firstName}</h5>
          <h5 id="lName-h5" class="align-text-center pt-1">${user.lastName}</h5>
        </div>
      </div>
      <div class="d-flex flex-column justify-content-center">
      </div>
      <div class="d-flex flex-column mb-5 mt-lg-5">
        <div class="d-flex align-items-center  mb-3 ">
          <img class="me-lg-2" src="${emailIcon}" alt="Icon Profile"
               class="me-2">
          <h5 id="email-h5" class="align-text-center pt-1">${user.email}</h5>
        </div>
      </div>
      <div class="d-flex flex-column mb-3 ">
        <div class="d-flex align-items-center  mb-3">
          <img class="me-lg-2" src="${phoneNumberIcon}" alt="Icon Profile"
               class="me-2">
          <h5 class="align-text-center pt-1" id="phoneNumber-h5">
            ${user.phoneNumber}</h5>
        </div>
      </div>
    </div> `;
  divContainer.appendChild(divPersonnalInformation);
  divPersonnalInformation.style = "margin-left:20%"

  let htmlProfilStage;
  if (stage.error || !stage) {
    htmlProfilStage = ` 
        <div id="container-stage-profil" class="shadow-lg p-3 mb-5 bg-white rounded align-items-center justify-content-center " style="width: 700px;">
            <div class="d-flex align-items-center justify-content-center ">
                <h1 class="align-text-center me-lg-3" style="padding-top: 2%" >L'étudiant n'a pas encore trouvé de stage.</h1>
            </div>
        </div>
</div>`;
  } else {
    htmlProfilStage = `
        <div id="container-stage-profil" class="shadow-lg p-3 mb-5 bg-white rounded align-items-center justify-content-center " style="width: 700px;">
            <div class="d-flex align-items-center justify-content-center ">
                <h1 class="align-text-center me-lg-3" style="padding-top: 2%" >Stage</h1>
            </div>`;

    // Check if stage.contactDTO and stage.contactDTO.companyDTO are defined before accessing them
    if (stage.contactDTO && stage.contactDTO.companyDTO) {
      htmlProfilStage += `
            <div class="d-flex m-3">
                <h4 class="me-lg-3">Entreprise : </h4>
                <h4> ${stage.contactDTO.companyDTO.name}</h4>
            </div>`;
    }

    // Check if stage.signatureDate is defined before accessing it
    if (stage.signatureDate) {
      htmlProfilStage += `
            <div class="d-flex m-3">
                <h4 class="me-lg-3">Date de Signature : </h4>
                <h4> ${formatDate(stage.signatureDate)}</h4>
            </div>`;
    }

    // Check if stage.subject is defined before accessing it
    if (stage.subject) {
      htmlProfilStage += `
            <div class="d-flex m-3">
                <h4 class="me-lg-3">Sujet du Stage : </h4>
                <h4> ${stage.subject}</h4>
            </div>`;
    } else {
      htmlProfilStage += `
            <div class="d-flex m-3">
                <h4 class="me-lg-3">Sujet du Stage : </h4>
                <h4>Sujet non mentionné.</h4>
            </div>
        </div>`;
    }
    htmlProfilStage += `</div>`;
  }

  let htmlProfilContacts = `
        <div class="d-flex flex-column mt-lg-1 shadow-lg p-4 mb-5 bg-white rounded">
          <h1 id="h1-profil-stage">Contacts</h1>
          <table class=" table table-bordered  mt-lg-1 mb-lg-5 p-5 ">
            <thead >
              <tr>
                <th scope="col">Entreprise</th>
                <th scope="col">État Actuel</th>
                <th scope="col"> Détails </th>
              </tr>
            </thead>
            <tbody id="contactsTableBody"></tbody>
          </table>
        `;

  // ------------------------------------------------------------------------
  const main = document.querySelector('main');
  main.style.height = `calc(100vh - 5vw)`;

  main.appendChild(divContainer);

  const personalInfoDiv = document.getElementById("personnalInfo");

  if (user.role === 'ETUDIANT') {

    const divContacts = document.createElement('div');
    divContacts.className = "d-flex flex-column mt-1 pt-5 text-center"
    divContacts.style = "margin-right:20%;"
    if (allContacts.contacts.length === 0) {
      htmlProfilContacts += ` 
          <p class="text-light-emphasis">Aucun contact à afficher.</p>
          </div>
      `;
    } else {
      htmlProfilContacts += `  </div>`;
    }
    divContacts.innerHTML = htmlProfilStage + htmlProfilContacts;
    divContainer.appendChild(divContacts);
    divContainer.style = "margin-top:3%"
    personalInfoDiv.style = "margin-top:10%;";

    // Get the table body element
    const tableBody = document.getElementById('contactsTableBody');

    // Clear existing content
    tableBody.innerHTML = '';
    // Iterate through fetched data and create table rows
    allContacts.contacts.forEach(contact => {
      // Create a table row
      const row = document.createElement('tr');

      // Create table data cells for companyId and state
      const companyIdCell = document.createElement('td');
      let companyName;
      if (contact.companyDTO.designation) {
        companyName = `${contact.companyDTO.name} : ${contact.companyDTO.designation}`;

      } else {
        companyName = `${contact.companyDTO.name}`;
      }
      companyName = blacklistName(companyName, contact.companyDTO.blacklisted);
      companyIdCell.textContent = `${companyName}`;
      const stateCell = document.createElement('td');
      stateCell.textContent = transformState2String(contact.state);
      const detailsCell = document.createElement('td');
      if (contact.state === "REFUSE") {
        detailsCell.textContent = contact.refusalReason;
      } else {
        detailsCell.textContent = transformPlace2String(contact.meetingPlace);
      }

      // Append cells to the row
      row.appendChild(companyIdCell);
      row.appendChild(stateCell);
      row.appendChild(detailsCell);

      // Append the row to the table body
      tableBody.appendChild(row);

    });
  } else {
    personalInfoDiv.style = "border: none; margin-top:10%";
  }

}

export default ProfileStudentPage;
