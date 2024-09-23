import {REMEMBER_ME_STORE_NAME, TOKEN_STORE_NAME} from "../utils/storeNames";

const login = async (email, password) => {
  try {
    const response = await fetch(`${process.env.API_BASE_URL}/auths/login`,
        {
          method: 'POST',
          body: JSON.stringify({email, password}),
          headers: {
            'content-type': 'application/json',
          },
        });

    if (!response.ok) {
      const error = await response.text();
      return {error};
    }

    return {user: await response.json()};
  } catch (error) {
    return {error: error.message};
  }
};

const loginToken = async () => {
  let token;
  if (localStorage.getItem(REMEMBER_ME_STORE_NAME)) {
    token = localStorage.getItem(TOKEN_STORE_NAME);
  } else {
    token = sessionStorage.getItem(TOKEN_STORE_NAME);
  }

  try {
    const response = await fetch(`${process.env.API_BASE_URL}/auths`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        Authorization: token,
      },
    });
    if (!response.ok) {
      const error = await response.text();
      return {error};
    }
    return {user: await response.json()};
  } catch (error) {
    console.error(error);
    return null;
  }
};

const register = async (firstName, lastName, phoneNumber, email, password,
    role) => {
  try {
    const response = await fetch(`${process.env.API_BASE_URL}/auths/register`, {
      method: 'POST',
      body: JSON.stringify(
          {
            firstName,
            lastName,
            phoneNumber,
            email,
            password,
            role
          }),
      headers: {
        'Content-Type': 'application/json',
      },
    });

    if (!response.ok) {
      const error = await response.text();
      return {error};
    }

    return {user: await response.json()};
  } catch (error) {
    return {error: error.message};
  }
};

export {login, loginToken, register};
