import {getToken} from "../utils/connectedUser";

const getAllSupervisorByCompany = async (idCompany) => {
  try {

    const response = await fetch(
        `http://localhost:3030/supervisors?companyId=${idCompany}`, {
          method: 'GET',
          headers: {
            Authorization: getToken(),
          }
        });

    if (!response.ok) {
      const error = await response.text();
      return {error};
    }
    return await response.json();
  } catch (error) {
    return {error: error.message};
  }
};
const addSupervisor = async (supervisor, idCompany) => {
  try {

    const response = await fetch(
        `http://localhost:3030/supervisors/${idCompany}`, {
          method: 'POST',
          body: JSON.stringify(supervisor),
          headers: {
            'content-type': 'application/json',
            Authorization: getToken(),
          }
        });

    if (!response.ok) {
      const error = await response.text();
      return {error};
    }

    return await response.json();
  } catch (error) {
    return {error: error.message};
  }
};
export {getAllSupervisorByCompany, addSupervisor};