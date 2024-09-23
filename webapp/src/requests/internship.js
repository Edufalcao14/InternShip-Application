import {getToken} from "../utils/connectedUser";

const getNbrOfStudentsWithInternship = async (year) => {
  try {
    const response = await fetch(
        `http://localhost:3030/internships/with?year=${year}`, {
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

    return await response.json();
  } catch (error) {
    return {error: error.message};
  }
};

const getNbrOfStudentsWithoutInternship = async (year) => {
  try {
    const url = `http://localhost:3030/internships/without?year=${year}`;
    const response = await fetch(url, {
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

    return await response.json();
  } catch (error) {
    return {error: error.message};
  }
};

const getStageByStudent = async () => {
  try {

    const response = await fetch(`http://localhost:3030/internships/one`, {
      method: 'GET',
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
const getStageByStudentID = async (id) => {
  try {

    const response = await fetch(
        `http://localhost:3030/internships/one?studentId=${id}`, {
          method: 'GET',
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
const modifyStageSubject = async (id, subject, version) => {
  const bodyData = {id, subject, version};
  try {
    const response = await fetch(
        `http://localhost:3030/internships/modifySubject`, {
          method: 'PATCH',
          body: JSON.stringify(bodyData),
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

export {
  getNbrOfStudentsWithInternship,
  getNbrOfStudentsWithoutInternship,
  getStageByStudent,
  getStageByStudentID,
  modifyStageSubject
};