import {loginToken} from "../requests/auths";
import {
  REMEMBER_ME_STORE_NAME,
  TOKEN_STORE_NAME,
  USER_EMAIL_STORE_NAME,
  USER_FIRSTNAME_STORE_NAME
} from "./storeNames";

let connectedUser;

const setRememberMe = () => {
  if (connectedUser) {
    localStorage.setItem(REMEMBER_ME_STORE_NAME, 'yes');
    localStorage.setItem(USER_EMAIL_STORE_NAME, connectedUser.email);
  }
};

const clearRememberMe = () => {
  localStorage.removeItem(REMEMBER_ME_STORE_NAME);
  localStorage.removeItem(USER_EMAIL_STORE_NAME);
}

const clearConnectedUser = () => {
  connectedUser = {};
  localStorage.removeItem(TOKEN_STORE_NAME);
  sessionStorage.removeItem(TOKEN_STORE_NAME);
  localStorage.removeItem(USER_FIRSTNAME_STORE_NAME);
  sessionStorage.removeItem(USER_FIRSTNAME_STORE_NAME);
};

const setConnectedUser = (user, rememberMe) => {
  const authenticatedUser = JSON.parse(user);

  connectedUser = {};

  Object.entries(authenticatedUser).forEach(([key, value]) => {
    connectedUser[key] = value;
  });

  if (rememberMe) {
    localStorage.setItem(TOKEN_STORE_NAME, connectedUser.token);
    localStorage.setItem(USER_FIRSTNAME_STORE_NAME, connectedUser.firstName);
    setRememberMe();
  } else {
    clearRememberMe();
    sessionStorage.setItem(TOKEN_STORE_NAME, connectedUser.token);
    sessionStorage.setItem(USER_FIRSTNAME_STORE_NAME, connectedUser.firstName);
  }
};

const getToken = () => {
  if (localStorage.getItem(REMEMBER_ME_STORE_NAME)) {
    return localStorage.getItem(TOKEN_STORE_NAME);
  }
  return sessionStorage.getItem(TOKEN_STORE_NAME);
}

const getFirstname = () => {
  if (localStorage.getItem(USER_FIRSTNAME_STORE_NAME)) {
    return localStorage.getItem(USER_FIRSTNAME_STORE_NAME);
  }
  if (sessionStorage.getItem(USER_FIRSTNAME_STORE_NAME)) {
    return sessionStorage.getItem(USER_FIRSTNAME_STORE_NAME);
  }
  return 'Anonymous';
}

const isConnected = () => getToken() !== undefined && getToken() !== null;

const isStudent = async () => {
  const user = await getConnectedUser();
  return user.role === "ETUDIANT";
}

async function checkTokenValidity() {
  if (isConnected()) {
    let rememberMe = false;

    if (localStorage.getItem(REMEMBER_ME_STORE_NAME)) {
      rememberMe = true;
    }

    const response = await loginToken();

    if (response.error) {
      console.error(response.error);
      clearConnectedUser();
    } else {
      setConnectedUser(JSON.stringify(response.user), rememberMe);
    }
  }
}

async function getConnectedUser() {
  if (!connectedUser) {
    await checkTokenValidity();
  }
  return connectedUser;
}

export {
  getConnectedUser,
  setConnectedUser,
  clearConnectedUser,
  setRememberMe,
  isConnected,
  isStudent,
  getToken,
  getFirstname,
  checkTokenValidity
};
