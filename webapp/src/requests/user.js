import {getToken} from "../utils/connectedUser";

const getAllUsers = async () => {
  try {
    const response = await fetch('http://localhost:3030/users', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        Authorization: getToken(),
      }
    });

    if (!response.ok) {
      const error = await response.text();
      return {error};
    }

    const users = await response.json();

    return {users};
  } catch (error) {
    return {error: error.message};
  }
};

const modifyUserField = async (userDTO) => {

  try {
    const response = await fetch(
        `${process.env.API_BASE_URL}/users/modifyUserField`, {
          method: 'PATCH',
          body: JSON.stringify({
            firstName: userDTO.firstName,
            lastName: userDTO.lastName,
            phoneNumber: userDTO.phoneNumber,
            email: userDTO.email,
            version: userDTO.version,
            role: userDTO.role
          }),
          headers: {
            'content-type': 'application/json',
            Authorization: getToken(),
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

const getUserById = async (id) => {
  try {
    const response = await fetch(`http://localhost:3030/users/${id}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json'
      }
    });

    if (!response.ok) {
      const error = await response.text();
      return {error};
    }

    const user = await response.json(); // Convert response to JSON
    return user;
  } catch (error) {
    return {error: error.message};
  }
}
const modifyPassword = async (id, currentPassword, newPassword, version) => {
  try {
    const response = await fetch(
        `${process.env.API_BASE_URL}/users/modifyPassword`, {
          method: 'PATCH',
          body: JSON.stringify({currentPassword, newPassword, version}),
          headers: {
            'content-type': 'application/json',
            Authorization: getToken(),
          },
        });
    if (!response.ok) {
      const error = await response.text();
      return {error};
    }

    return await response;
  } catch (error) {
    return {error: error.message};
  }
};

export {getAllUsers, getUserById, modifyUserField, modifyPassword};
