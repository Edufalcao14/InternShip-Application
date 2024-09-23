import Swal from 'sweetalert2';
import {
  acceptContact,
  addContact,
  denyContact,
  getAllContactsByUser,
  takeContact,
  unfollowAContact
} from "../../requests/contact";
import {
  addCompany,
  getAllCompanies,
  getOneCompany
} from "../../requests/company";
import {
  addSupervisor,
  getAllSupervisorByCompany
} from "../../requests/supervisor";
import {clearPage} from "../../utils/render";
import {isConnected, isStudent} from "../../utils/connectedUser";
import Navigate from "../Router/Navigate";
import Navbar from "../Navbar/Navbar";
import nonAuthorizedPopUp from "../../utils/NonAuthorizedPopUp";
import {
  blacklistName,
  transformPlace2String,
  transformState2String
} from "../../utils/utils";

let resultAllContactsByUser;
let resultAllCompanies;

const StudentboardPage = async () => {
  clearPage();

  if (!isConnected()) {
    Navigate('/');
    return;
  }

  const isAStudent = await isStudent();

  if (!isAStudent) {
    nonAuthorizedPopUp();
    Navigate('/dashboard');
    return;
  }

  await Navbar();

  const main = document.querySelector("main");
  main.style.height = `calc(100vh - 5vw)`;
  resultAllContactsByUser = await getAllContactsByUser(); // type of object with
  resultAllCompanies = await getAllCompanies();

  let innerHtml =
      `
                       <div class="d-flex justify-content-center" style="color: #044879;">
                          <h1 style="margin: 15px">Contacts</h1>
                       </div>
                        <div class=" d-flex flex-column modal-dialog shadow bg-white rounded p-3 mb-lg-5" style="margin:0px 20%">
                            <div class="modal-content">
                      
                              <!-- Modal Header -->
                              <div class="modal-header">
                                <h3 class="modal-title mb-4" style="text-align: center; width: 100%; color: #044879;">Initier un contact</h4>
                                
                              </div>
                              <!-- Modal Body -->
                              <div class="d-flex flex-column justify-content-center modal-body">
                                <div class="form-group row">
                                  <label for="company" class="col-form-label" style="padding-left: 3%;width: 12%">Entreprise</label>
                                  <div class="col-sm-10">
                                    <select class="form-control" id="companySelect">`;

  innerHtml += resultAllCompanies.companies.map(company => {
    let companyName;
    if (company.designation) {
      companyName = `${company.name} : ${company.designation}`;

    } else {
      companyName = `${company.name}`;
    }
    companyName = blacklistName(companyName, company.blacklisted);
    return `<option value="${company.id}">${companyName}</option>`;
  }).join('');

  innerHtml += `
             </select>
              <em style="margin-top: 10px;">Vous ne pouvez pas choisir une entreprise ayant ce symbole ⚠️</em><br>
                <button type="button" class="btn btn-link" data-toggle="modal"
                       id="addcompany">
                   Ajouter une entreprise
                </button>
              </div>
              </div>
              </div>
            
              <!-- Modal Footer -->
              <div class="modal-footer">
                <button type="button" class="btn btn-success mx-auto" id="buttonAddContact">Initier</button>
              </div>
            
            </div>
            </div>
              `;
  main.innerHTML += innerHtml;
  htmlContactsTable(resultAllContactsByUser.contacts);

  // Script pour gérer l'ajout d'une entreprise
  const addButton = document.querySelector("#addcompany");

  addButton.addEventListener("click", () => {
    Swal.fire({
      title: 'Ajouter une entreprise',
      html: `
      <input type="text" id="name" class="swal2-input" placeholder="Nom">
      <input type="text" id="designation" class="swal2-input" placeholder="Appellation">
      <input type="text" id="address" class="swal2-input" placeholder="Adresse">
      <input type="tel" id="phoneNumber" class="swal2-input" placeholder="Numéro de téléphone">
      <input type="email" id="email" class="swal2-input" placeholder="Email">
    `,
      focusConfirm: false,
      showCancelButton: true,
      cancelButtonText: 'Annuler',
      preConfirm: () => {
        const name = Swal.getPopup().querySelector('#name').value;
        const designation = Swal.getPopup().querySelector('#designation').value;
        const address = Swal.getPopup().querySelector('#address').value;
        const phoneNumber = Swal.getPopup().querySelector('#phoneNumber').value;
        const email = Swal.getPopup().querySelector('#email').value;

        if (!name || !address) {
          Swal.showValidationMessage(
              `Veuillez remplir les champs champs obligatoires `)
        }

        return {name, designation, address, phoneNumber, email}
      }
    }).then(async (result) => {

      if (result.isConfirmed) {
        let company;
        if (result.value.email.length === 0 && result.value.phoneNumber.length
            === 0) {
          company = {
            name: result.value.name,
            designation: result.value.designation || "", // Ajout de la désignation ou chaîne vide
            address: result.value.address
          }
        }
        if (result.value.email.length === 0 && result.value.phoneNumber.length
            !== 0) {
          company = {
            name: result.value.name,
            designation: result.value.designation || "", // Ajout de la désignation ou chaîne vide
            address: result.value.address,
            phoneNumber: result.value.phoneNumber
          }
        }
        if (result.value.email.length !== 0 && result.value.phoneNumber.length
            === 0) {
          company = {
            name: result.value.name,
            designation: result.value.designation || "", // Ajout de la désignation ou chaîne vide
            address: result.value.address,
            email: result.value.email
          }
        }
        if (result.value.email.length !== 0 && result.value.phoneNumber.length
            !== 0) {
          company = {
            name: result.value.name,
            designation: result.value.designation || "", // Ajout de la désignation ou chaîne vide
            address: result.value.address,
            phoneNumber: result.value.phoneNumber,
            email: result.value.email
          }
        }

        // Envoyez une requête à votre serveur pour ajouter l'entreprise
        const response = await addCompany(company);

        if (response.error) {
          Swal.fire('Erreur',
              `L'entreprise n'a pas pu être ajoutée : ${response.error}`,
              'error');
        } else {
          Swal.fire('Succès', 'L\'entreprise a été ajoutée avec succès.',
              'success');
          StudentboardPage();
        }
      }
    });
  });
}

async function htmlContactsTable(allContactsByUser) {
  const main = document.querySelector("main");
  let innerHtml = '';

  if (allContactsByUser === undefined) {
    innerHtml = `
      
      <div class="d-flex flex-column justify-content-center shadow-lg p-3 bg-white rounded-2 mb-5" style="margin: 0px 12%">
      <div class="d-flex justify-content-center " style="color: #044879;">
        <h3>Liste Des Contacts Suivis</h3>
      </div>
        <table class="table table-striped" style="width: 1150px; text-align: center;">
          <thead>
            <tr style="background-color: #044879; color: #ffffff;">
              <th scope="col">Entreprise</th>
              <th scope="col">Suivi</th>
              <th scope="col">État Actuel</th>
              <th scope="col">Informer changement</th>
              <th scope="col">Détails</th>
            </tr>
          </thead>
        </table>
        <div class="d-flex justify-content-center">
      <p>Aucun contact à afficher.</p>
      </div>
      </div>
      
`;
  } else {
    innerHtml = ` 
      <div class="d-flex flex-column justify-content-center shadow-lg p-3 bg-white rounded-2 mb-5" style="margin: 0px 15%">
      <div class="d-flex justify-content-center  " style="color: #044879;">
        <h3 style="margin: 15px">Liste Des Contacts Suivis </h3>
      </div>
        <table class="table table-striped mb-5" style="width: auto; border:1px solid;text-align: center;">
          <thead>
            <tr style="background-color: #044879; color: #ffffff;">
              <th scope="col">Entreprise</th>
              <th scope="col">Suivi</th>
              <th scope="col">État Actuel</th>
              <th scope="col">Informer changement</th>
              <th scope="col">Détails</th>
            </tr>
          </thead>
          <tbody>
          `;

    innerHtml += allContactsByUser.map(contact => {
      let takeButtonHtml = '';
      let denyAndAcceptButtonsHtml = '';
      let detailsHtml = '';
      let unfollowButton = '';

      if (contact.state === "INITIE") {
        takeButtonHtml = `
      <td>
        <button type="button" class="btn btn-secondary" id="firstButtonTake" data-id="${contact.id}" data-version="${contact.version}">Prendre contact</button>
      </td>
      <td>
      </td>`;
        unfollowButton = `<button type="button" class="btn btn-warning" id="unfollow" data-id="${contact.id}" data-version="${contact.version}">Ne plus suivre</button>`;
      } else if (contact.state === "PRIS") {
        denyAndAcceptButtonsHtml = `
      <td>
          <button type="button" class="btn btn-success" data-toggle="modal" data-target="#myModalCreate" data-id="${contact.id}" data-companyId="${contact.companyId}" data-version="${contact.version}">Accepter</button>
          <button type="button" class="btn btn-danger" data-toggle="modal" data-target="#myModalDenied" data-id="${contact.id}" data-version="${contact.version}">Refuser</button>
      </td>`;
        detailsHtml = `
      <td>
        ${transformPlace2String(contact.meetingPlace)}
      </td>`;
        unfollowButton = `<button type="button" class="btn btn-warning" id="unfollow" data-id="${contact.id}" data-version="${contact.version}">Ne plus suivre</button>`;
      } else if (contact.state === "REFUSE") {
        detailsHtml = `
      <td></td>
      <td>
        ${contact.refusalReason}
      </td>`;
      } else if (contact.state === "SUSPENDU" || contact.state === "ACCEPTE") {
        detailsHtml = `
      <td></td>
      <td></td>`;
      } else if (contact.state === "BLACKLISTE") {
        detailsHtml = `
      <td></td>
      <td>${contact.companyDTO.blacklistReason}</td>`;
      }

      let companyName;
      if (contact.companyDTO.designation) {
        companyName = `${contact.companyDTO.name} : ${contact.companyDTO.designation}`;

      } else {
        companyName = `${contact.companyDTO.name}`;
      }
      companyName = blacklistName(companyName, contact.companyDTO.blacklisted);

      const stateString = transformState2String(contact.state);
      return `
    <tr style="background-color: #e9ecef;" data-id="${contact.id}">
        <th scope="row">${companyName}</th>
        <td>
           ${unfollowButton}
        </td>                   
        <td>${stateString}</td>     
        ${takeButtonHtml}
        ${denyAndAcceptButtonsHtml}
        ${detailsHtml}
    </tr>`;
    }).join('');

    innerHtml += `</tbody>
          </table>
      </div>`;
  }

  main.innerHTML += innerHtml;

  addListenerOnButtonAddContact();
  addListenerOnButtonUnfollow();
  addListenerOnButtonTake();
  await addListenerOnButtonAccept();
  addListenerOnButtonDeny();

}

function addListenerOnButtonTake() {
  // Ajoutez un écouteur d'événements sur les boutons "Pris"
  const takeButtons = document.querySelectorAll("#firstButtonTake");
  takeButtons.forEach(button => button.addEventListener("click", async () => {
    const contactId = button.getAttribute("data-id");
    const version = button.getAttribute("data-version");
    const selectedLocation = await Swal.fire({
      title: 'Lieu de rencontre',
      input: 'select',
      inputOptions: {
        'presentiel': 'Présentiel', 'distanciel': 'Distanciel'
      },
      inputPlaceholder: 'Sélectionnez un lieu',
      showCancelButton: true,
      confirmButtonText: 'Prendre contact',
      cancelButtonText: 'Annuler',
      inputValidator: (value) => {
        return new Promise((resolve) => {
          if (value !== '') {
            resolve();
          } else {
            resolve('Vous devez sélectionner un lieu.');
          }
        });
      }
    });
    if (selectedLocation.isConfirmed) {
      // Appeler la méthode takeContact avec les paramètres appropriés
      const response = await takeContact(contactId, selectedLocation.value,
          version);
      if (response.error) {
        await Swal.fire('Erreur',
            `Le contact n'a pas pû être pris : ${response.error}`,
            'error');
      } else {
        Swal.fire('Contact Pris !', '', 'success').then(() => {
          StudentboardPage();
        });
      }
    }
  }));
}

async function addListenerOnButtonAccept() {
  const acceptButtons = document.querySelectorAll(
      "[data-target='#myModalCreate']");
  acceptButtons.forEach(button =>
      button.addEventListener("click", async () => {
        const contactId = button.getAttribute("data-id");
        const companyId = button.getAttribute("data-companyId");
        const version = button.getAttribute("data-version");

        // Création du formulaire de création de stage
        await createInternshipAndSupervisorForms(version, contactId, companyId);

      }));
}

function addListenerOnButtonUnfollow() {
  const buttonsUnfollow = document.querySelectorAll("#unfollow");

  buttonsUnfollow.forEach(buttonUnfollow => {
    buttonUnfollow.addEventListener('click', async () => {
      Swal.fire({
        text: 'Êtes-vous sûr(e) de ne plus vouloir suivre le contact ?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Oui',
        cancelButtonText: 'Annuler',
      }).then(async (result) => {
        if (result.isConfirmed) {
          const id = buttonUnfollow.getAttribute("data-id");
          const version = buttonUnfollow.getAttribute("data-version");

          const response = await unfollowAContact(id, version);

          if (response.error) {
            await Swal.fire('Erreur',
                `La modification du contact n'a pas abouti : ${response.error}`,
                'error');
          } else {
            await Swal.fire('Le contact n\'est plus suivi !', '', 'success');
            await StudentboardPage();
          }
        }
      });
    });
  });
}

function addListenerOnButtonAddContact() {
  const buttonAddContact = document.querySelector("#buttonAddContact");

  buttonAddContact.addEventListener('click', async () => {
    const companyId = document.querySelector("#companySelect").value;
    const company = await getOneCompany(companyId);
    if (company.blacklisted === true) {
      await Swal.fire({
        icon: "error",
        title: "Vous ne pouvez pas choisir une entreprise blacklistée !",
      });
    } else {
      const response = await addContact(companyId);

      if (response.error) {
        await Swal.fire('Erreur',
            `Le contact n'a pas pû être initié : ${response.error}`,
            'error');
      } else {
        Swal.fire('Contact Initié !', '', 'success').then(() => {
          StudentboardPage();
        });
      }
    }
  });
}

function addListenerOnButtonDeny() {
  // Ajoutez un écouteur d'événements sur les boutons "Refuser"
  const denyButtons = document.querySelectorAll(
      "[data-target='#myModalDenied']");
  denyButtons.forEach(button => button.addEventListener("click", () => {
    const contactId = button.getAttribute("data-id");
    const version = button.getAttribute("data-version");
    Swal.fire({
      title: 'Raison du refus',
      input: 'text',
      inputPlaceholder: 'Entrez la raison du refus',
      showCancelButton: true,
      confirmButtonText: 'Refuser',
      cancelButtonText: 'Annuler',
      inputValidator: (value) => {
        return new Promise((resolve) => {
          if (value.trim()) {
            resolve();
          } else {
            resolve('Veuillez saisir une raison de refus.');
          }
        });
      }
    }).then(async (result) => {
      if (result.isConfirmed) {
        const refusalReason = result.value;
        const response = await denyContact(contactId, refusalReason, version);

        if (response.error) {
          await Swal.fire('Erreur',
              `Le contact n'a pas pû être refusé : ${response.error}`,
              'error');
        } else {
          Swal.fire('Contact Refusé !', '', 'success').then(() => {
            StudentboardPage();
          });
        }
      }
    });
  }));
}

// Fonction pour créer le formulaire de création de stage
async function createInternshipAndSupervisorForms(version, contactId,
    companyId) {
  let response = await getAllSupervisorByCompany(companyId);
  let allSupervisors = response.supervisors;
  let supervisorChoice = '<p> <i>Aucun responsable n\'a été trouvé pour cette entreprise.</i> </p>';

  // Créer les options pour le sélecteur de superviseur en fonction des données récupérées
  if (allSupervisors.length > 0) {
    let supervisorOptions = allSupervisors.map(supervisor => {
      return `<option value="${supervisor.id}">${supervisor.firstName} ${supervisor.lastName}</option>`;
    }).join("");
    supervisorChoice =
        `<select class="form-control" id="supervisor" style="margin: 10px;">
          ${supervisorOptions} 
        </select>`;
  }

  Swal.fire({
    title: 'Créer un stage',
    html:
        `<div style="margin: 10px;">
                    <label for="supervisor" style="color: #044879; margin: 10px;">Responsable de stage</label>
                    ${supervisorChoice}
                     <button type="button" class="btn btn-link" id="createSupervisorLink" data-toggle="modal">
                      Ajouter un responsable
                     </button>
                </br>
                </div>

                <div style="margin: 10px;">
                    <label for="dateSignature" style="color: #044879;margin: 10px;">Date de signature:</label>
                    <input type="date" class="form-control" id="dateSignature">
                </div>
                <div style="margin: 10px;">
                    <label for="subject" style="color: #044879;margin: 10px;">Sujet (optionnel)</label>
                    <input type="text" class="form-control" id="subject">
                </div>`,
    showCancelButton: true,
    confirmButtonText: 'Créer',
    confirmButtonColor: 'green',
    cancelButtonText: 'Annuler',
    focusConfirm: false,
    preConfirm: () => {
      const supervisor = document.getElementById('supervisor').value;
      const signatureDate = document.getElementById(
          'dateSignature').value;
      const subject = document.getElementById('subject').value;

      // Valider les champs obligatoires
      if (!supervisor || !signatureDate) {
        Swal.showValidationMessage(
            'Veuillez remplir tous les champs obligatoires : le responsable et la date de signature.');
      }

      return {
        supervisor: supervisor,
        signatureDate: signatureDate,
        subject: subject,
      };
    }
  }).then(async (result) => {
    if (result.isConfirmed) {
      const {supervisor, signatureDate, subject} = result.value;

      // Ajoutez ici votre logique pour créer le stage avec les données fournies
      let internship;
      if (subject.length === 0) {
        internship = {
          supervisorId: supervisor,
          signatureDate,
          version
        };
      } else {
        internship = {
          supervisorId: supervisor,
          signatureDate,
          subject,
          version
        };
      }
      const response = await acceptContact(internship, contactId);
      if (response.error) {
        Swal.fire('Erreur lors de la création du stage', response.error,
            'error');
      } else {
        Swal.fire({
          title: 'Stage créé avec succès !',
          icon: 'success',
          showCancelButton: false,
          confirmButtonText: 'OK',
        }).then((result) => {
          // Actualiser la page lorsque l'utilisateur clique sur le bouton "OK"
          if (result.isConfirmed) {
            window.location.reload();
          }
        });
      }
    }
  });

  document.querySelectorAll("#createSupervisorLink")
  .forEach(buttons =>
      buttons.addEventListener("click", () => {
            // Afficher un autre pop-up pour créer un superviseur
            Swal.fire({
              title: "Ajouter un responsable",
              html:
                  `<div style="margin: 10px;">
                        <label for="nom" style="margin: 10px;">Nom:</label>

                        <input type="text" class="form-control" id="nom">
                    </div>
                    <div style="margin: 10px;">
                        <label for="prenom" style="margin: 10px;">Prénom:</label>

                        <input type="text" class="form-control" id="prenom">
                    </div>
                    <div style="margin: 10px;">
                        <label for="email" style="margin: 10px;">Email:</label>

                        <input type="email" class="form-control" id="email">
                    </div>
                    <div style="margin: 10px;">
                        <label for="telephone" style="margin: 10px;">Téléphone:</label>

                        <input type="tel" class="form-control" id="telephone">
                    </div>`,
              showCancelButton: true,
              confirmButtonText: 'Ajouter',
              cancelButtonText: 'Annuler',
              focusConfirm: false,
              preConfirm: () => {
                const firstName = document.getElementById('nom').value;
                const lastName = document.getElementById('prenom').value;
                const email = document.getElementById('email').value;
                const phoneNumber = document.getElementById('telephone').value;

                // Valider les champs obligatoires
                if (!firstName || !lastName || !phoneNumber) {
                  Swal.showValidationMessage(
                      'Veuillez remplir tous les champs obligatoires : le nom, prénom et le numéro de téléphone.');
                }

                return {
                  firstName: firstName,
                  lastName: lastName,
                  email: email,
                  phoneNumber: phoneNumber
                };
              }
            }).then(async (result) => {
              if (result.isConfirmed) {
                const {firstName, lastName, email, phoneNumber} = result.value;
                // Utilisez les valeurs récupérées pour créer le superviseur
                let supervisorToAdd;
                if (email.length > 0) {
                  supervisorToAdd = {firstName, lastName, email, phoneNumber};
                } else {
                  supervisorToAdd = {firstName, lastName, phoneNumber};
                }
                // Appeler acceptContact et gérer les cas de réussite et d'échec

                const response = await addSupervisor(supervisorToAdd, companyId);

                if (response.error) {
                  Swal.fire('Erreur lors de l\'ajout du responsable',
                      response.error,
                      'error');
                } else {
                  Swal.fire({
                    title: 'Responsable Ajouté !',
                    icon: 'success',
                    showCancelButton: false,
                    confirmButtonText: 'OK',
                  }).then((result) => {
                    // Actualiser la page lorsque l'utilisateur clique sur le bouton "OK"
                    if (result.isConfirmed) {
                      createInternshipAndSupervisorForms(version, contactId,
                          companyId);
                    }
                  });
                }
              } else {
                // Réafficher le formulaire de création de stage
                createInternshipAndSupervisorForms(version, contactId, companyId);
              }
            });
          }
      ))
}

export default StudentboardPage;