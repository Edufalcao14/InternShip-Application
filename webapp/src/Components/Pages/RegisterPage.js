import logoInBlueColor from '../../img/logoInBlueColor.png';
import {
  isConnected,
  isStudent,
  setConnectedUser
} from '../../utils/connectedUser';
import Navigate from '../Router/Navigate';
import {clearPage} from '../../utils/render';
import {register} from "../../requests/auths";
import nonAuthorizedPopUp from "../../utils/NonAuthorizedPopUp";

const handleRegisterFormSubmit = async (e) => {
  e.preventDefault();
  const form = e.target;
  const firstName = form.querySelector('#firstname').value.trim();
  const lastName = form.querySelector('#lastname').value.trim();
  const phoneNumberPart1 = form.querySelector('#phoneNumberPart1').value.trim();
  const phoneNumberPart2 = form.querySelector('#phoneNumberPart2').value.trim();
  const phoneNumberPart3 = form.querySelector('#phoneNumberPart3').value.trim();
  const phoneNumber = phoneNumberPart1 + phoneNumberPart2 + phoneNumberPart3
  const email = form.querySelector('#email').value.trim();
  const password = form.querySelector('#password').value.trim();
  const confirmPassword = form.querySelector('#confirmPassword').value.trim();
  let role = form.querySelector('#role').value;
  const rememberMe = form.querySelector('#rememberMe').checked;
  const errorP = document.getElementById('errorP');

  if (firstName === '' || lastName === '' || phoneNumber === '' || email === ''
      || password === '' || confirmPassword === '') {
    errorP.textContent = `⚠️ Tous les champs sont requis.`;
    return;
  }

  if (phoneNumber.length !== 10) {
    errorP.textContent = `⚠️ Numéro de téléphone invalide.`;
    return;
  }

  const regexStudent = /@student\.vinci\.be$/;
  const regexVinci = /@vinci\.be$/;

  if (!regexStudent.test(email) && !regexVinci.test(email)) {
    errorP.textContent = `⚠️ Email invalide.`;
    return;
  }
  if (regexStudent.test(email)) {
    role = "ETUDIANT";
  }

  const regexPassword = /^(?=.*[0-9]).{5,}$/;

  if (!regexPassword.test(password)) {
    errorP.textContent = `⚠️ Le mot-de-passe doit contenir au moins 5 caractères dont 1 chiffre.`;
    return;
  }

  if (password !== confirmPassword) {
    errorP.textContent = `⚠️ Les mots-de-passe ne correspondent pas.`;
    return;
  }

  try {
    const res = await register(firstName, lastName, phoneNumber, email,
        password, role);
    if (res.error) {
      errorP.textContent = `⚠️ ${res.error}`;
    } else {
      setConnectedUser(JSON.stringify(res.user), rememberMe);

      const isAStudent = await isStudent();
      if (isAStudent) {
        Navigate('/studentboard');
      } else {
        Navigate('/dashboard');
      }
    }
  } catch (error) {
  }
};

const renderRegisterPage = async () => {

  clearPage();

  const main = document.querySelector('main');

  if (isConnected()) {
    if (isConnected()) {
      const isAStudent = await isStudent()
      if (isAStudent) {
        nonAuthorizedPopUp();
        return Navigate('/studentboard');
      } else {
        nonAuthorizedPopUp();
        return Navigate('/dashboard');
      }
    }
  }

  const moveToNext = (input, previousField, nextField) => {
    const maxLength = input.getAttribute("maxlength");
    if (input.value.length >= maxLength && nextField) {
      nextField.focus();
    } else if (input.value.length === 0 && previousField) {
      previousField.focus();
    }
  }

  //  Left part - Logo
  const section = document.createElement('section');
  section.classList.add('position-relative', 'py-4', 'py-xl-5');
  section.style.display = 'flex';
  section.style.justifyContent = 'center';
  section.style.height = 'calc(100vh - 11vh)'

  const container = document.createElement('div');
  container.classList.add('container');

  const row = document.createElement('div');
  row.classList.add('row', 'align-items-center');

  const logoCol = document.createElement('div');
  logoCol.classList.add('col-md-6', 'text-center');
  logoCol.style.setProperty('--bs-body-bg', '#fdf9f9');

  const logoImg = document.createElement('img');
  logoImg.height = 92;
  logoImg.src = logoInBlueColor;
  logoImg.width = 366;
  logoImg.alt = 'BlueLogo';

  const logoP = document.createElement('p');
  logoP.classList.add('mt-3');
  logoP.textContent = 'Outil d\'encodage simplifié pour les stages d\'observation !';
  logoP.style.fontFamily = 'Inter, sans-serif';
  logoP.style.fontWeight = 'bold';
  logoP.style.color = '#3f4e6f';
  logoP.style.fontSize = '18px';

  logoCol.appendChild(logoImg);
  logoCol.appendChild(logoP);

  //  Right Part - Form
  const formCol = document.createElement('div');
  formCol.classList.add('col-md-6', 'pe-0', 'me-0');
  formCol.style.borderLeft = '3px solid #ccc';
  formCol.style.height = 'calc(100vh - 22vh)'

  const innerRow = document.createElement('div');
  innerRow.classList.add('row');

  const formColInner = document.createElement('div');
  formColInner.classList.add('col-md-8', 'offset-md-2', 'mb-0');

  const cardBody = document.createElement('div');
  cardBody.classList.add('mb-5', 'card-body', 'd-flex', 'flex-column',
      'align-items-center');

  const titleDiv = document.createElement('div');
  titleDiv.classList.add('mb-3');
  const titleLabel = document.createElement('p');
  titleLabel.classList.add('text-muted');
  titleLabel.style.fontFamily = 'Inter, sans-serif';
  titleLabel.style.fontWeight = 'bold';
  titleLabel.style.fontSize = '25px';
  titleLabel.textContent = 'S\'inscrire';
  titleDiv.appendChild(titleLabel);

  cardBody.appendChild(titleDiv);

  const form = document.createElement('form');
  form.classList.add('text-center');
  form.id = 'registerForm';

  //  First Name
  const firstNameDiv = document.createElement('div');
  firstNameDiv.classList.add('mb-3');
  const firstNameLabel = document.createElement('p');
  firstNameLabel.classList.add('text-muted');
  firstNameLabel.style.fontFamily = 'Inter, sans-serif';
  firstNameLabel.style.fontWeight = 'bold';
  firstNameLabel.style.fontSize = '14px';
  firstNameLabel.textContent = 'Prénom';
  firstNameLabel.style.marginBottom = '3px';
  const firstNameInput = document.createElement('input');
  firstNameInput.classList.add('form-control');
  firstNameInput.name = 'firstname';
  firstNameInput.id = 'firstname';
  firstNameInput.placeholder = 'Prénom';
  firstNameInput.type = 'text';
  firstNameInput.required = true;
  firstNameDiv.appendChild(firstNameLabel);
  firstNameDiv.appendChild(firstNameInput);
  form.appendChild(firstNameDiv);

  //  Last Name
  const lastNameDiv = document.createElement('div');
  lastNameDiv.classList.add('mb-3');
  const lastNameLabel = document.createElement('p');
  lastNameLabel.classList.add('text-muted');
  lastNameLabel.style.fontFamily = 'Inter, sans-serif';
  lastNameLabel.style.fontWeight = 'bold';
  lastNameLabel.style.fontSize = '14px';
  lastNameLabel.textContent = 'Nom';
  lastNameLabel.style.marginBottom = '3px';
  const lastNameInput = document.createElement('input');
  lastNameInput.classList.add('form-control');
  lastNameInput.name = 'lastname';
  lastNameInput.id = 'lastname';
  lastNameInput.placeholder = 'Nom';
  lastNameInput.type = 'text';
  lastNameInput.required = true;
  lastNameDiv.appendChild(lastNameLabel);
  lastNameDiv.appendChild(lastNameInput);
  form.appendChild(lastNameDiv);

  //  Phone Number
  const phoneNumberDiv = document.createElement('div');
  phoneNumberDiv.classList.add('mb-3', 'text-center');
  const phoneNumberLabel = document.createElement('p');
  phoneNumberLabel.classList.add('text-muted');
  phoneNumberLabel.style.fontFamily = 'Inter, sans-serif';
  phoneNumberLabel.style.fontWeight = 'bold';
  phoneNumberLabel.style.fontSize = '14px';
  phoneNumberLabel.textContent = 'Numéro de téléphone';
  phoneNumberLabel.style.marginBottom = '3px';
  const phoneNumberContainer = document.createElement('div');
  phoneNumberContainer.classList.add('d-flex', 'justify-content-between',
      'align-items-center');

  const phoneNumberPart1Input = document.createElement('input');
  phoneNumberPart1Input.classList.add('form-control', 'mr-2');
  phoneNumberPart1Input.style.width = '30%';
  phoneNumberPart1Input.style.textAlign = 'center';
  phoneNumberPart1Input.name = 'phoneNumberPart1';
  phoneNumberPart1Input.id = 'phoneNumberPart1';
  phoneNumberPart1Input.placeholder = '0412';
  phoneNumberPart1Input.type = 'tel';
  phoneNumberPart1Input.maxLength = 4;
  phoneNumberPart1Input.required = true;

  const phoneNumberPart2Input = document.createElement('input');
  phoneNumberPart2Input.classList.add('form-control', 'mr-2');
  phoneNumberPart2Input.style.width = '30%';
  phoneNumberPart2Input.style.textAlign = 'center';
  phoneNumberPart2Input.name = 'phoneNumberPart2';
  phoneNumberPart2Input.id = 'phoneNumberPart2';
  phoneNumberPart2Input.placeholder = '345';
  phoneNumberPart2Input.type = 'tel';
  phoneNumberPart2Input.maxLength = 3;
  phoneNumberPart2Input.required = true;

  const phoneNumberPart3Input = document.createElement('input');
  phoneNumberPart3Input.classList.add('form-control');
  phoneNumberPart3Input.style.width = '30%';
  phoneNumberPart3Input.style.textAlign = 'center';
  phoneNumberPart3Input.name = 'phoneNumberPart3';
  phoneNumberPart3Input.id = 'phoneNumberPart3';
  phoneNumberPart3Input.placeholder = '678';
  phoneNumberPart3Input.type = 'tel';
  phoneNumberPart3Input.maxLength = 3;
  phoneNumberPart3Input.required = true;

  phoneNumberPart1Input.addEventListener('input', () => {
    phoneNumberPart1Input.value = phoneNumberPart1Input.value.replace(/\D/g,
        '');
    moveToNext(phoneNumberPart1Input, null, phoneNumberPart2Input)
  });
  phoneNumberPart2Input.addEventListener('input', () => {
    phoneNumberPart2Input.value = phoneNumberPart2Input.value.replace(/\D/g,
        '');
    moveToNext(phoneNumberPart2Input, phoneNumberPart1Input,
        phoneNumberPart3Input)
  });
  phoneNumberPart3Input.addEventListener('input', () => {
    phoneNumberPart3Input.value = phoneNumberPart3Input.value.replace(/\D/g,
        '');
    moveToNext(phoneNumberPart3Input, phoneNumberPart2Input, null)
  });

  phoneNumberContainer.appendChild(phoneNumberPart1Input);
  phoneNumberContainer.appendChild(phoneNumberPart2Input);
  phoneNumberContainer.appendChild(phoneNumberPart3Input);

  phoneNumberDiv.appendChild(phoneNumberLabel);
  phoneNumberDiv.appendChild(phoneNumberContainer);

  form.appendChild(phoneNumberDiv);

  //  Email
  const emailDiv = document.createElement('div');
  emailDiv.classList.add('mb-3');
  const emailLabel = document.createElement('p');
  emailLabel.classList.add('text-muted');
  emailLabel.style.fontFamily = 'Inter, sans-serif';
  emailLabel.style.fontWeight = 'bold';
  emailLabel.style.fontSize = '14px';
  emailLabel.textContent = 'Email';
  emailLabel.style.marginBottom = '3px';
  const emailInput = document.createElement('input');
  emailInput.classList.add('form-control');
  emailInput.name = 'email';
  emailInput.id = 'email';
  emailInput.placeholder = 'Email';
  emailInput.type = 'email';
  emailInput.required = true;
  emailDiv.appendChild(emailLabel);
  emailDiv.appendChild(emailInput);
  form.appendChild(emailDiv);

  //  Password
  const passwordDiv = document.createElement('div');
  passwordDiv.classList.add('mb-3');
  const passwordLabel = document.createElement('p');
  passwordLabel.classList.add('text-muted');
  passwordLabel.style.fontFamily = 'Inter, sans-serif';
  passwordLabel.style.fontWeight = 'bold';
  passwordLabel.style.fontSize = '14px';
  passwordLabel.textContent = 'Mot-de-passe';
  passwordLabel.style.marginBottom = '3px';
  const passwordInput = document.createElement('input');
  passwordInput.classList.add('form-control');
  passwordInput.name = 'password';
  passwordInput.id = 'password';
  passwordInput.placeholder = 'Mot-de-passe';
  passwordInput.type = 'password';
  passwordInput.required = true;
  passwordDiv.appendChild(passwordLabel);
  passwordDiv.appendChild(passwordInput);
  form.appendChild(passwordDiv);

  //  Confirm Password
  const confirmPasswordDiv = document.createElement('div');
  confirmPasswordDiv.classList.add('mb-3');
  const confirmPasswordLabel = document.createElement('p');
  confirmPasswordLabel.classList.add('text-muted');
  confirmPasswordLabel.style.fontFamily = 'Inter, sans-serif';
  confirmPasswordLabel.style.fontWeight = 'bold';
  confirmPasswordLabel.style.fontSize = '14px';
  confirmPasswordLabel.textContent = 'Confirmer Mot-de-passe';
  confirmPasswordLabel.style.marginBottom = '3px';
  const confirmPasswordInput = document.createElement('input');
  confirmPasswordInput.classList.add('form-control');
  confirmPasswordInput.name = 'confirmPassword';
  confirmPasswordInput.id = 'confirmPassword';
  confirmPasswordInput.placeholder = 'Confirmer Mot-de-passe';
  confirmPasswordInput.type = 'password';
  confirmPasswordInput.required = true;
  confirmPasswordDiv.appendChild(confirmPasswordLabel);
  confirmPasswordDiv.appendChild(confirmPasswordInput);
  form.appendChild(confirmPasswordDiv);

  //  Role
  const roleDiv = document.createElement('div');
  roleDiv.classList.add('mb-3');
  roleDiv.style.display = 'none';
  const roleLabel = document.createElement('p');
  roleLabel.classList.add('text-muted');
  roleLabel.style.fontFamily = 'Inter, sans-serif';
  roleLabel.style.fontWeight = 'bold';
  roleLabel.style.fontSize = '14px';
  roleLabel.textContent = "S'inscrire en tant que";
  roleLabel.style.marginBottom = '3px';
  roleDiv.appendChild(roleLabel);
  const roleSelect = document.createElement('select');
  roleSelect.classList.add('form-select');
  roleSelect.setAttribute('id', 'role');
  roleSelect.setAttribute('name', 'role');
  roleSelect.value = "ETUDIANT";
  const profOption = document.createElement('option');
  profOption.setAttribute('value', 'PROFESSEUR');
  profOption.textContent = 'Professeur';
  const adminOption = document.createElement('option');
  adminOption.setAttribute('value', 'ADMINISTRATIF');
  adminOption.textContent = 'Administratif';
  roleSelect.appendChild(profOption);
  roleSelect.appendChild(adminOption);
  roleDiv.appendChild(roleSelect);
  form.appendChild(roleDiv);

  //  Remember Me
  const rememberMeDiv = document.createElement('div');
  rememberMeDiv.classList.add('mb-3', 'form-check');
  const rememberMeInput = document.createElement('input');
  rememberMeInput.style.cursor = 'pointer';
  rememberMeInput.type = 'checkbox';
  rememberMeInput.classList.add('form-check-input');
  rememberMeInput.id = 'rememberMe';
  const rememberMeLabel = document.createElement('label');
  rememberMeLabel.style.float = 'left';
  rememberMeLabel.classList.add('form-check-label');
  rememberMeLabel.style.fontFamily = 'Inter, sans-serif';
  rememberMeLabel.style.fontWeight = 'bold';
  rememberMeLabel.style.fontSize = '14px';
  rememberMeLabel.textContent = 'Se souvenir de moi';
  rememberMeLabel.style.marginBottom = '3px';
  rememberMeDiv.appendChild(rememberMeInput);
  rememberMeDiv.appendChild(rememberMeLabel);
  form.appendChild(rememberMeDiv);

  //  Register Button
  const signUpButtonDiv = document.createElement('div');
  signUpButtonDiv.classList.add('mb-3');
  const signUpButton = document.createElement('button');
  signUpButton.classList.add('btn', 'btn-primary', 'd-block', 'w-100',
      'custom-btn');
  signUpButton.type = 'submit';
  signUpButton.textContent = 'S\'inscrire';
  signUpButtonDiv.appendChild(signUpButton);
  form.appendChild(signUpButtonDiv);

  //  Already Registered
  const alreadyRegisteredDiv = document.createElement('div');
  const alreadyRegisteredText = document.createElement('p');
  alreadyRegisteredText.classList.add('text-muted');
  alreadyRegisteredText.style.fontFamily = 'Inter, sans-serif';
  alreadyRegisteredText.style.fontWeight = 'bold';
  alreadyRegisteredText.style.fontSize = '12px';
  alreadyRegisteredText.style.textAlign = 'left';
  alreadyRegisteredText.style.marginRight = '66px';
  alreadyRegisteredText.innerHTML = 'Vous êtes déjà inscrit ?&nbsp;<a class="link" id="alreadyRegisteredA">Connectez-Vous</a>';
  alreadyRegisteredDiv.appendChild(alreadyRegisteredText);
  form.appendChild(alreadyRegisteredDiv);

  //  Error P
  const errorP = document.createElement('p');
  errorP.classList.add('me-0');
  errorP.id = 'errorP';
  form.appendChild(errorP);

  cardBody.appendChild(form);
  formColInner.appendChild(cardBody);
  innerRow.appendChild(formColInner);
  formCol.appendChild(innerRow);
  row.appendChild(logoCol);
  row.appendChild(formCol);
  container.appendChild(row);
  section.appendChild(container);
  main.appendChild(section);

  emailInput.addEventListener('input', () => {
    const emailValue = emailInput.value.trim();
    const isVinciEmail = emailValue.endsWith('@vinci.be');

    roleDiv.style.display = isVinciEmail ? 'block' : 'none';
  });

  const alreadyRegisteredA = document.querySelector('#alreadyRegisteredA');
  alreadyRegisteredA.addEventListener('click', (e) => {
    e.preventDefault();
    return Navigate('/');
  });

  const registerForm = document.getElementById('registerForm');
  registerForm.addEventListener('submit', handleRegisterFormSubmit);

};

const RegisterPage = async () => {
  await renderRegisterPage();
};

export default RegisterPage;