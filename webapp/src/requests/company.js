import {getToken} from "../utils/connectedUser";

const getOneCompany = async (id) => {
  try {
    const response = await fetch(`http://localhost:3030/companies/${id}`, {
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

const getAllCompanies = async (academicYear , orderBy) => {


  try {
    let url = 'http://localhost:3030/companies'
    if (academicYear !== undefined && academicYear !== "" ) {
      url += `?academicYear=${academicYear}`;
    }
    if (orderBy !== undefined && orderBy !== "" ) {
      if(academicYear !== undefined && academicYear !== "" && orderBy !== undefined && orderBy !== "" ){
        url += `&orderBy=${orderBy}`;
      }else{
        url += `?orderBy=${orderBy}`;

      }
    }
    console.log(url);
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

    const {companies} = await response.json();

    return {companies};
  } catch (error) {
    return {error: error.message};
  }
};

const blacklistCompany = async (idCompany, blacklistReason, version) => {
  try {
    const response = await fetch(
        `http://localhost:3030/companies/${idCompany}/blacklist`,
        {
          method: 'POST',
          body: JSON.stringify({blacklist_reason: blacklistReason, version}),
          headers: {
            'content-type': 'application/json',
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
};

const addCompany = async (company) => {
  try {
    const response = await fetch(`http://localhost:3030/companies`,
        {
          method: 'POST',
          body: JSON.stringify(company),
          headers: {
            'content-type': 'application/json',
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

export {
  getOneCompany,
  getAllCompanies,
  blacklistCompany,
  addCompany
};