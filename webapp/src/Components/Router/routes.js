import LoginPage from '../Pages/LoginPage';
import Dashboard from "../Pages/Dashboard";
import ProfilPage from "../Pages/ProfilPage"

import RegisterPage from "../Pages/RegisterPage";
import StudentboardPage from "../Pages/StudentboardPage";
import ProfileStudentPage from "../Pages/ProfileStudentPage";

const routes = {
  '/': LoginPage,
  '/register': RegisterPage,
  '/profil': ProfilPage,
  '/studentboard': StudentboardPage,
  '/dashboard': Dashboard ,
  '/studentprofile': ProfileStudentPage
};

export default routes;
