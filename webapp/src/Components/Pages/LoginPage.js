import logoInBlueColor from '../../img/logoInBlueColor.png';
import {login} from '../../requests/auths';
import {
  isConnected,
  isStudent,
  setConnectedUser
} from '../../utils/connectedUser';
import Navigate from '../Router/Navigate';
import {clearPage} from '../../utils/render';
import {
  REMEMBER_ME_STORE_NAME,
  USER_EMAIL_STORE_NAME
} from "../../utils/storeNames";
import nonAuthorizedPopUp from "../../utils/NonAuthorizedPopUp";

const handleLoginFormSubmit = async (e) => {
  e.preventDefault();
  const form = e.target;
  const email = form.querySelector('#email').value;
  const password = form.querySelector('#password').value;
  const rememberMe = form.querySelector('#rememberMe').checked;
  const errorP = document.getElementById('errorP');

  try {
    const response = await login(email, password);
    if (response.error) {
      errorP.textContent = `⚠️ ${response.error}`;
    } else {
      setConnectedUser(JSON.stringify(response.user), rememberMe);
      const isAStudent = await isStudent();
      if (isAStudent) {
        Navigate('/studentboard');
      } else {
        Navigate('/dashboard');
      }
    }
  } catch (error) {
    console.error(error);
  }
};

const renderLoginPage = async () => {
  clearPage();

  const main = document.querySelector('main');

  if (isConnected()) {
    const isAStudent = await isStudent();
    if (isAStudent) {
      nonAuthorizedPopUp();
      return Navigate('/studentboard');
    } else {
      nonAuthorizedPopUp();
      return Navigate('/dashboard');
    }
  }

  const storedRememberMe = localStorage.getItem(REMEMBER_ME_STORE_NAME);
  const storedEmail = localStorage.getItem(USER_EMAIL_STORE_NAME);

  main.innerHTML = `
     <section class="position-relative py-4 py-xl-5" style="display: flex; justify-content: center; align-items: center; height: calc(100vh - 11vh);">
      <div class="container">
        <div class="row align-items-center">
          <div class="col-md-6 text-center" style="--bs-body-bg: #fdf9f9;">
            <img height="92" src="${logoInBlueColor}" width="366" alt="BlueLogo">
            <p class="mt-3" style="font-family: Inter, sans-serif;font-weight: bold;color: #3f4e6f;font-size: 18px;">Outil d'encodage simplifié pour les stages d'observation !</p>
          </div>
          <div class="col-md-6" style="border-left: 3px solid #ccc;">
            <div class="row">
              <div class="col-md-8 offset-md-2">
                <div class="mb-5">
                  <div class="card-body d-flex flex-column align-items-center">
                    <div>
                      <p class="text-muted" style="font-family: Inter, sans-serif;font-weight: bold;font-size: 25px">Se connecter</p>
                    </div>
                    <form class="text-center" id="loginForm">
                      <div class="mb-3" style="padding-bottom: 0;margin-bottom: 3px;">
                        <p class="text-muted" style="font-family: Inter, sans-serif;font-weight: bold;font-size: 14px;">Email</p>
                        <input class="form-control" name="email" id="email" placeholder="Email" type="email" value="${storedEmail
  || ''}" required>
                      </div>
                      <div class="mb-3">
                        <p class="text-muted" style="font-family: Inter, sans-serif;font-weight: bold;font-size: 14px;">Mot de passe</p>
                        <input class="form-control" name="password" id="password" placeholder="Password" type="password" required>
                      </div>
                      <div class="mb-3 form-check">
                        <input style="cursor: pointer" type="checkbox" class="form-check-input" id="rememberMe" ${storedRememberMe
  === 'yes' ? 'checked' : ''}>
                        <label class="form-check-label" style="float:left;font-family: Inter, sans-serif;font-weight: bold;font-size: 14px;">Se souvenir de moi</label>
                      </div>
                      <div class="mb-3">
                        <button class="btn btn-primary d-block w-100 custom-btn" type="submit">Se connecter</button>
                      </div>
                      <div>
                        <p class="text-muted" style="font-family: Inter, sans-serif;font-weight: bold;font-size: 12px;text-align: left;margin-right: 66px;">
                          Vous n'êtes pas inscrit ?&nbsp;<a class="link" id="noRegisteredA">Inscrivez-Vous</a>
                        </p>
                      </div>
                    </form>
                    <div>
                      <p id="errorP"></p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>`;

  const loginForm = document.getElementById('loginForm');
  loginForm.addEventListener('submit', handleLoginFormSubmit);

  if (storedRememberMe === 'yes') {
    const rememberMeCheckbox = document.getElementById('rememberMe');
    rememberMeCheckbox.checked = true;
  }

  const noRegisteredA = document.querySelector('#noRegisteredA');
  noRegisteredA.addEventListener('click', () => {
    return Navigate('/register');
  });
};

const LoginPage = async () => {
  await renderLoginPage();
};

export default LoginPage;
