import Swal from 'sweetalert2'
import Navigate from '../Router/Navigate';
import {clearPage} from '../../utils/render';
import {
  blacklistName,
  formatDate,
  transformPlace2String,
  transformState2String
} from "../../utils/utils";
import {getConnectedUser, isConnected,} from '../../utils/connectedUser';
import {getStageByStudent, modifyStageSubject} from "../../requests/internship";
import profileIcon from '../../img/profileIcon.svg';
import emailIcon from '../../img/emailIcon.svg';
import phoneNumberIcon from '../../img/phoneNumberIcon.svg';

import {getAllContactsByUser} from "../../requests/contact";
import {modifyPassword, modifyUserField} from "../../requests/user";

const ProfilPage = async () => {

  if (!isConnected()) {
    return Navigate('/');
  }

  let user = await getConnectedUser();
  let stage = await getStageByStudent();
  const userId = user.id;

  const allContacts = await getAllContactsByUser(userId);
  clearPage();

  const divContainer = document.createElement('div');
  divContainer.className = "d-flex justify-content-center ";
  divContainer.style.height = "100%";

  const divPersonnalInformation = document.createElement('div');

  divPersonnalInformation.innerHTML = `
    <div id="personnalInfo" class="d-flex flex-column mb-3 p-5 shadow-lg p-3 mb-5 bg-white rounded  me-lg-5" style=" height: 100%;">
       <h1>Données Personelles</h1>
       <div class="d-flex flex-column " style="margin-top: 10% ">
          <div class="d-flex align-items-center  mb-3">
            <img class="me-lg-2" src="${profileIcon}" alt="Icon Profile" class="me-2">
            <h5 id="fName-h5" class="align-text-center pt-1 me-lg-2">${user.firstName}</h5>
            <h5 id="lName-h5"  class="align-text-center pt-1" >${user.lastName}</h5>
          </div>
          <div hidden="until-found"  class="changeInfo d-flex flex-row align-items-center mb-3"  style="margin-top: 3%">
            <label for="fname" class="p-3">Prénom:</label>
            <input class="form-control me-lg-3" type="text" id="fname" name="fname">
            <button type="button" class="btn-changeInfo btn btn-success ml-2 mr-2" id="btn-changeInfo-firstName">Sauvegarder</button>
         </div>
      </div>
      <div class="d-flex flex-column justify-content-center">
        <div hidden="until-found"  class="changeInfo d-flex flex-row align-items-center mb-3">
            <label class="p-4" for="lname">Nom:  </label>
            <input type="text" class=" form-control me-lg-3" id="lname" name="lname">
            <button type="button" class="btn-changeInfo btn btn-success ml-2 mr-2" id="btn-changeInfo-lastName">Sauvegarder</button>
         </div>
      </div>
       <div class="d-flex flex-column mb-5 ">
        <div class="d-flex align-items-center  mb-3">
            <img class="me-lg-2" src="${emailIcon}" alt="Icon Profile" class="me-2">
             <h5 id="email-h5"  class="align-text-center pt-1">${user.email}</h5>
          </div> 
         <div hidden="until-found"  class="changeInfo d-flex flex-row align-items-center">
            <label class="p-4" for="email">Email:  </label>
            <input type="text" class="form-control me-lg-3" id="email" name="email" style="">
            <button type="button" class="btn-changeInfo btn btn-success ml-2 mr-2" id="btn-changeInfo-email">Sauvegarder</button>
         </div>
       </div>
       <div  class="d-flex flex-column mb-3 ">
       <div class="d-flex align-items-center  mb-3">
              <img class="me-lg-2" src="${phoneNumberIcon}" alt="Icon Profile" class="me-2">
              <h5 class="align-text-center pt-1" id="phoneNumber-h5">${user.phoneNumber}</h5>
          </div> 
        
         <div hidden="until-found"  class="changeInfo d-flex flex-row align-items-center mb-3">
            <label class="p-4" for="email">Télephone:  </label>
            <input type="text" class="form-control me-lg-3" id="phoneNumber" name="email" style="">
            <button type="button" class="btn-changeInfo btn btn-success ml-2 mr-2" id="btn-changeInfo-phoneNumber">Sauvegarder</button>
         </div>
       </div>
       <div class="d-grid gap-4">
        <button type="button" class=" btn btn-primary btn-lg  " id="change-password-btn">Modifier Mot-de-Passe</button>
         <button type="button" class=" btn btn-primary btn-lg mb-lg-5 " id="change-all-info-btn">Modifier Informations Personelles</button>
       </div>
      </div> `;
  divContainer.appendChild(divPersonnalInformation);

  let htmlProfilStage;

  if (stage.error) {
    htmlProfilStage = ` 
        <div id="container-stage-profil" class="shadow-lg p-3 mb-5 bg-white rounded align-items-center justify-content-center " style="width: 700px;">
            <div class="d-flex align-items-center justify-content-center ">
                <h1 class="align-text-center me-lg-3" style="padding-top: 2%" >Vous n'avez pas encore trouvé de stage</h1>
            </div>
        </div>`;
  } else {
    htmlProfilStage = `
        <div id="container-stage-profil" class="shadow-lg p-3 mb-5 bg-white rounded align-items-center justify-content-center " style="width: 700px;">
            <div class="d-flex align-items-center justify-content-center ">
                <h1 class="align-text-center me-lg-3" style="padding-top: 2%" >Mon Stage</h1>
            </div>`;

    // Check if stage.contactDTO and stage.contactDTO.companyDTO are defined before accessing them
    if (stage.contactDTO && stage.contactDTO.companyDTO) {
      let nomAppellation;
      if (stage.contactDTO.companyDTO.designation) {
        nomAppellation = `${stage.contactDTO.companyDTO.name} : ${stage.contactDTO.companyDTO.designation}`;

      } else {
        nomAppellation = `${stage.contactDTO.companyDTO.name}`;
      }
      console.log(stage.contactDTO.companyDTO.designation)
      htmlProfilStage += `
            <div class="d-flex m-3">
                <h4 class="me-lg-3">Entreprise : </h4>
                <h4> ${nomAppellation}</h4>
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
          <div class="d-flex m-3 justify-content-start" style="float: left; margin-bottom: 0px!important;">
           <h4 class="  me-lg-3 text-nowrap" style="float: left;">Sujet du Stage : </h4>
           <h4 class="text-wrap" id="subjectH4" style="width: fit-content">${stage.subject}</h4>
           </div>
           <div class="d-flex flex-column m-3">
           
            </div>
            `;
    } else {
      htmlProfilStage += `
            <div class="d-flex m-3">
                <h4 class="me-lg-3">Sujet du Stage : </h4>
                <h4 id="subjectH4">Sujet non mentionné.</h4>
            </div>`;
    }

    htmlProfilStage += `
            <div class="d-flex p-3  justify-content-end">
                <button id="btn-modifyStage" type="button" class="btn btn-success btn-lg mt-lg-5">Modifier Sujet</button>
            </div>     
        </div>`;
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
    divPersonnalInformation.style = "margin-left:20%"
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

    if (!stage.error) {
      const modifySubjectBTn = document.getElementById("btn-modifyStage");
      modifySubjectBTn.addEventListener("click", () => {
        Swal.fire({
          title: 'Modifier sujet de stage',
          html: `
            <input type="text" id="sujet" class="swal2-input" placeholder="Nouveau sujet">
              `,
          focusConfirm: false,
          confirmButtonText: "Modifier Sujet de Stage",
          confirmButtonColor: '#1581d0',
          didOpen: () => {
            const popup = Swal.getPopup();
            sujet.onkeyup = (event) => event.key === 'Enter'
                && Swal.clickConfirm();
          },
          preConfirm: () => {
            const sujet = Swal.getPopup().querySelector('#sujet').value;

            if (sujet.length < 4) {
              Swal.showValidationMessage(
                  `Veuillez remplir le champ avec minimun 4 characteres`)
            }
            if (sujet.length > 70) {
              Swal.showValidationMessage(
                  `Veuillez remplir le champ avec maximun 50 characteres`)
            }
            return sujet;
          }
        }).then(async (result) => {
          if (result.isConfirmed) {

            const response = await modifyStageSubject(stage.id, result.value,
                stage.version);
            const subjectUI = document.getElementById("subjectH4");
            if (response.error) {
              Swal.fire({
                toast: true,
                position: "center",
                icon: "error",
                text: `Le sujet de stage n'a pas pu être modifié  : ${response.error}`,
                timer: 1500,
                timerProgressBar: true,
                showConfirmButton: false,
              });
            } else {
              subjectUI.innerText = response.subject;
              Swal.fire({
                toast: true,
                position: "center",
                icon: "success",
                text: `Le sujet de stage a été modifié`,
                timer: 1500,
                timerProgressBar: true,
                showConfirmButton: false,
              });
              setTimeout(function () {
                location.reload();
              }, 1500);
            }
          }
        });

      });
    }
  } else {
    personalInfoDiv.style = "border: none; margin-top:10%;";

  }

  //Recupere la div des inputs
  const showInputsSaveInfos = document.getElementById(
      'change-all-info-btn');
  const allInputsDivs = document.querySelectorAll('.changeInfo');

  // Recupere les btn et les valeurs des inputs pour changer les informations
  const btnSaveInfoFirstName = document.getElementById(
      'btn-changeInfo-firstName');
  const btnSaveInfoLastName = document.getElementById(
      'btn-changeInfo-lastName');
  const btnSaveInfoEmail = document.getElementById(
      'btn-changeInfo-email');
  const btnSaveInfoPhoneNumber = document.getElementById(
      'btn-changeInfo-phoneNumber');
  const fnameInput = document.getElementById('fname');
  const lnameInput = document.getElementById('lname');
  const emailInput = document.getElementById('email');
  const phoneNumberInput = document.getElementById('phoneNumber');

// Affiche les Inputs de modification et appelle les functions pour changer les informations de l'utilisateur.
  showInputsSaveInfos.addEventListener('click', () => {
    personalInfoDiv.style = "margin-top:5%;"
    if (allInputsDivs[0].hidden) {
      //affiche les inputs pour modifier le mot de passe
      showAllInputs(false);

      //Modifie FirstName
      btnSaveInfoFirstName.addEventListener('click', async () => {
        const firstName = fnameInput.value;
        await modifyFirstNameAndUpdateUI(firstName);
      });

      //Modifie LastName
      btnSaveInfoLastName.addEventListener('click', async () => {
        const lastName = lnameInput.value;
        await modifyLastNameAndUpdateUI(lastName);
      });

      //Modifie Email
      btnSaveInfoEmail.addEventListener('click', async () => {
        const email = emailInput.value;
        await modifyEmailAndUpdateUI(email);
      });

      //Modifie numero de Telephone
      btnSaveInfoPhoneNumber.addEventListener('click', async () => {
        const phoneNumber = phoneNumberInput.value;
        await modifyPhoneNumberAndUpdateUI(phoneNumber);
      });
    } else {
      showAllInputs("until-found");
      personalInfoDiv.style = "margin-top:20%;"
    }
  });

  const changePasswordBtn = document.getElementById('change-password-btn');
  changePasswordBtn.addEventListener('click', () => {
    let currentPasswordInput;
    let newPassword1Input;
    let newPassword2Input;

    let currentPasswordValue;
    let newPassword1Value;
    let newPassword2Value;

    // modal changer le mot de passe.
    Swal.fire({
      title: 'Modifier mot de passe',
      html: `
      <label class="p-4" for="email">Mot de Passe Actuel  </label>
      <input type="password" id="currentPassword" class="swal2-input" placeholder="Mot-de-passe Actuel">
      <label class="p-4" for="email">Nouveau mot de passe  </label>
      <input type="password" id="newPassword1" class="swal2-input" placeholder="Nouveau Mot-de-passe">
      <label class="p-4" for="email">Confirmation nouveau mot de passe  </label>
      <input type="password" id="newPassword2" class="swal2-input" placeholder="Nouveau Mot-de-passe">`,
      confirmButtonText: 'Modifier mot de passe',
      confirmButtonColor: '#1581d0',
      focusConfirm: false,
      didOpen: () => {
        const popup = Swal.getPopup();
        currentPasswordInput = popup.querySelector('#currentPassword');
        newPassword1Input = popup.querySelector('#newPassword1');
        newPassword2Input = popup.querySelector('#newPassword2');

        currentPasswordInput.onkeyup = (event) => event.key === 'Enter'
            && Swal.clickConfirm();
        newPassword1Input.onkeyup = (event) => event.key === 'Enter'
            && Swal.clickConfirm();
        newPassword2Input.onkeyup = (event) => event.key === 'Enter'
            && Swal.clickConfirm();
      },
      preConfirm: async () => {
        currentPasswordValue = currentPasswordInput.value;
        newPassword1Value = newPassword1Input.value;
        newPassword2Value = newPassword2Input.value;
        if (currentPasswordValue === '' || newPassword1Value === ''
            || newPassword2Value === '') {
          Swal.showValidationMessage(
              `Veuillez remplir tous les champs.`);
          return false; // Prevent the modal from closing
        }
        if (newPassword1Value !== newPassword2Value) {
          Swal.showValidationMessage(
              `Les mots-de-passe ne correspondent pas.`);
          return false;
        }

        const statusModifyPassword = await modifyPassword(user.id,
            currentPasswordValue, newPassword1Value, user.version);
        if (statusModifyPassword.ok) {
          Swal.fire({
            icon: "success",
            title: "Le mot-de-passe a été changé avec succès!",
            timer: 2000,
            timerProgressBar: true,
            showConfirmButton: false,
          });
        } else {
          Swal.fire({
            icon: "error",
            title: ` Le mot-de-passe n'a pas pu être modifié : ${statusModifyPassword.error}`,
            timer: 2000,
            timerProgressBar: true,
            showConfirmButton: false,
          });
        }
        return true;
      },
    });

  });

  function showAllInputs(value) {
    allInputsDivs.forEach((div) => {
      div.hidden = value;
    });
  }

  async function modifyFirstNameAndUpdateUI(firstName) {
    if (verifyInputValue(firstName) === false) {
      Swal.fire({
        toast: true,
        position: "center",
        text: 'Veuillez remplir le champ.',
        icon: "error",
        button: false,
        timer: 1200,
        timerProgressBar: true,
        showConfirmButton: false,
      });
      return;
    }
    let userDTO = {};
    userDTO.version = user.version;
    userDTO.role = user.role;
    userDTO.firstName = firstName;
    const updatedUser = await modifyUserField(userDTO);
    if (updatedUser.error) {
      Swal.fire({
        toast: true,
        position: "center",
        icon: "error",
        text: updatedUser.error,
        timer: 3000,
        timerProgressBar: true,
        showConfirmButton: false,
      });
      return;
    }
    user = updatedUser.user;
    document.getElementById('fName-h5').innerText = firstName;
    Swal.fire({
      toast: true,
      position: "center",
      icon: "success",
      title: "Modifications enregistrées avec succès !",
      timer: 800,
      timerProgressBar: true,
      showConfirmButton: false,
    });
    showAllInputs("until-found");
    fnameInput.value = "";
    setTimeout(function () {
      location.reload();
    }, 800);
  }

  async function modifyLastNameAndUpdateUI(lastName) {
    if (verifyInputValue(lastName) === false) {
      Swal.fire({
        toast: true,
        position: "center",
        text: 'Veuillez remplir le champ.',
        icon: "error",
        button: false,
        timer: 1200,
        timerProgressBar: true,
        showConfirmButton: false,
      });
      return;
    }
    let userDTO = {};
    userDTO.version = user.version;
    userDTO.role = user.role;
    userDTO.lastName = lastName;
    const updatedUser = await modifyUserField(userDTO);
    if (updatedUser.error) {
      Swal.fire({
        toast: true,
        position: "center",
        icon: "success",
        text: updatedUser.error,
        timer: 3000,
        timerProgressBar: true,
        showConfirmButton: false,
      });
      return;
    }
    if (updatedUser.error) {
      Swal.fire({
        toast: true,
        position: "center",
        icon: "error",
        text: updatedUser.error,
        timer: 3000,
        timerProgressBar: true,
        showConfirmButton: false,
      });
      location.reload();
    }
    user = updatedUser.user;
    document.getElementById('lName-h5').innerText = lastName;
    Swal.fire({
      toast: true,
      position: "center",
      icon: "success",
      title: "Modifications enregistrées avec succès !",
      timer: 800,
      timerProgressBar: true,
      showConfirmButton: false,
    });
    showAllInputs("until-found");
    lnameInput.value = "";
    setTimeout(function () {
      location.reload();
    }, 800);
  }

  async function modifyEmailAndUpdateUI(email) {
    if (verifyInputValue(email) === false) {
      Swal.fire({
        toast: true,
        position: "center",
        text: 'Veuillez remplir le champ.',
        icon: "error",
        button: false,
        timer: 1200,
        timerProgressBar: true,
        showConfirmButton: false,
      });
      return;
    }

    if (!endsWithEmail(email)) {
      Swal.fire({
        toast: true,
        position: "center",
        text: 'Veuillez fournir un email valide (@student.vinci.be ou @vinci.be).',
        icon: "error",
        button: false,
        timer: 3000,
        timerProgressBar: true,
        showConfirmButton: false,
      });
      return;
    }
    if (isStudent(user.email) && !isStudent(email)) {
      Swal.fire({
        toast: true,
        position: "center",
        text: 'Veuillez fournir un email étudiant valide se terminant par @student.vinci.be .',
        icon: "error",
        button: false,
        timer: 3000,
        timerProgressBar: true,
        showConfirmButton: false,
      });
      return;
    }
    let userDTO = {};
    userDTO.version = user.version;
    userDTO.role = user.role;
    userDTO.email = email;
    const updatedUser = await modifyUserField(userDTO);
    if (updatedUser.error) {
      Swal.fire({
        toast: true,
        position: "center",
        icon: "error",
        text: updatedUser.error,
        timer: 3000,
        timerProgressBar: true,
        showConfirmButton: false,
      });
      return;
    }

    user = updatedUser.user;
    document.getElementById('email-h5').innerText = email;
    Swal.fire({
      toast: true,
      position: "center",
      icon: "success",
      title: "Modifications enregistrées avec succès !",
      timer: 800,
      timerProgressBar: true,
      showConfirmButton: false,
    });
    showAllInputs("until-found");
    emailInput.value = "";
    // Wait for 3 seconds before reloading the location
    setTimeout(function () {
      location.reload();
    }, 1000);
  }

  async function modifyPhoneNumberAndUpdateUI(phoneNumber) {
    if (verifyInputValue(phoneNumber) === false) {
      Swal.fire({
        toast: true,
        position: "center",
        text: 'Veuillez remplir le champ.',
        icon: "error",
        button: false,
        timer: 1200,
        timerProgressBar: true,
        showConfirmButton: false,
      });
      return;
    }
    if (!isValidPhoneNumber(phoneNumber)) {
      Swal.fire({
        toast: true,
        position: "center",
        text: 'Veuillez fournir un numero de telephone valide.',
        icon: "error",
        button: false,
        timer: 3000,
        timerProgressBar: true,
        showConfirmButton: false,
      });
      return;
    }
    let userDTO = {};
    userDTO.version = user.version;
    userDTO.role = user.role;
    userDTO.phoneNumber = phoneNumber;
    const updatedUser = await modifyUserField(userDTO);

    if (updatedUser.error) {
      Swal.fire({
        toast: true,
        position: "center",
        icon: "error",
        text: updatedUser.error,
        timer: 10000,
        timerProgressBar: true,
        showConfirmButton: false,
      });
      return;
    }
    user = updatedUser.user;
    document.getElementById('phoneNumber-h5').innerText = phoneNumber;
    Swal.fire({
      toast: true,
      position: "center",
      icon: "success",
      title: "Modifications enregistrées avec succès !",
      timer: 800,
      timerProgressBar: true,
      showConfirmButton: false,
    });
    showAllInputs("until-found");
    phoneNumberInput.value = "";
    setTimeout(function () {
      location.reload();
    }, 1000);
  }
}

function verifyInputValue(value) {
  if (value === '' || !value) {
    return false;
  }
  return true;
}

function endsWithEmail(str) {
  return /@(student\.)?vinci\.be$/.test(str);
}

function isStudent(str) {
  return /@student\.vinci\.be$/.test(str);
}

function isValidPhoneNumber(str) {
  if (str.length !== 10) {
    return false;
  }
  if (/[a-zA-Z]/.test(str)) {
    return false;
  }
  return true;
}

export default ProfilPage;


