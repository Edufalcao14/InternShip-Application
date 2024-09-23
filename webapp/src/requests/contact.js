import {getToken} from "../utils/connectedUser";

const getAllContactsByUser = async (id) => {
  let url;

  if (id !== undefined) {
    url = `http://localhost:3030/contacts?idUser=-${id}`;
  } else {
    url = `http://localhost:3030/contacts`;
  }

  try {
    const response = await fetch(url,
        {
          method: 'GET',
          headers: {
            Authorization: getToken(),
          },
        });

    if (!response.ok) {
      const error = await response.text();
      return {error};
    }

    const contacts = await response.json();

    return contacts;
  } catch (error) {
    return {error: error.message};
  }
}

const getAllContactsByCompany = async (idCompany) => {
  try {
    const response = await fetch(
        `http://localhost:3030/contacts/company?id=${idCompany}`,
        {
          method: 'GET',
          headers: {
            Authorization: getToken(),
          },
        });

    if (!response.ok) {
      const error = await response.text();
      return {error};
    }

    return await response.json();
  } catch (error) {
    return {error: error.message};
  }
}

const unfollowAContact = async (idContact, version) => {
  try {
    const response = await fetch(
        `http://localhost:3030/contacts/${idContact}/unfollow`,
        {
          method: 'POST',
          body: JSON.stringify({version}),
          headers: {
            'content-type': 'application/json',
            Authorization: getToken(),
          },
        });

    if (!response.ok) {
      const error = await response.text();
      return {error};
    }

    const contactUnfollowed = await response.json();
    return contactUnfollowed;
  } catch (error) {
    return {error: error.message};
  }
}

const takeContact = async (idContact, meetingPlace, version) => {
  try {
    const response = await fetch(
        `http://localhost:3030/contacts/${idContact}/take`,
        {
          method: 'POST',
          body: JSON.stringify({meetingPlace, version}),
          headers: {
            'content-type': 'application/json',
            Authorization: getToken(),
          },
        });

    if (!response.ok) {
      const error = await response.text();
      return {error};
    }

    const contactTake = await response.json();

    return contactTake;
  } catch (error) {
    return {error: error.message};
  }
}

const denyContact = async (idContact, refusalReason, version) => {
  try {
    const response = await fetch(
        `http://localhost:3030/contacts/${idContact}/deny`,
        {
          method: 'POST',
          body: JSON.stringify({refusalReason, version}),
          headers: {
            'content-type': 'application/json',
            Authorization: getToken(),
          },
        });

    if (!response.ok) {
      const error = await response.text();
      return {error};
    }

    const contactDeny = await response.json();

    return contactDeny;
  } catch (error) {
    return {error: error.message};
  }
}

const addContact = async (company) => {
  try {
    const response = await fetch(`http://localhost:3030/contacts`,
        {
          method: 'POST',
          body: JSON.stringify({company}),
          headers: {
            'content-type': 'application/json',
            Authorization: getToken(),
          },
        });

    if (!response.ok) {
      const error = await response.text();
      return {error};
    }

    const contactAdded = await response.json();

    return contactAdded;
  } catch (error) {
    return {error: error.message};
  }
}

const getAllContacts = async () => {
  try {
    const response = await fetch('http://localhost:3030/contacts/all', {
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

    const contacts = await response.json();

    return {contacts};
  } catch (error) {
    return {error: error.message};
  }
};

const acceptContact = async (internship, contactId) => {
  try {
    const response = await fetch(
        `http://localhost:3030/contacts/${contactId}/accept`,
        {
          method: 'POST',
          body: JSON.stringify(internship),
          headers: {
            'content-type': 'application/json',
            Authorization: getToken(),
          },
        });

    if (!response.ok) {
      const error = await response.text();
      return {error};
    }

    const contactAccepted = await response.json();
    return contactAccepted;
  } catch (error) {
    return {error: error.message};
  }
}

export {
  getAllContactsByUser,
  getAllContactsByCompany,
  unfollowAContact,
  getAllContacts,
  takeContact,
  denyContact,
  addContact,
  acceptContact
};