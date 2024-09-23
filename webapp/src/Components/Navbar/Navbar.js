import logo from '../../img/logoInWhiteColor.png';
import {
  clearConnectedUser, getConnectedUser,
  getFirstname,
  isConnected,
} from '../../utils/connectedUser';
import Navigate from '../Router/Navigate';


const Navbar = async () => {
  const firstname = getFirstname();

  const navbarWrapper = document.querySelector('#navbarWrapper');
  navbarWrapper.innerHTML = `
    <nav class="navbar navbar-expand-md py-3" style="background-color: #3f4e6f; color: white; height: 5vw">
      <div class="container">
        <a class="navbar-brand d-flex align-items-center" href="" style="background: var(--bs-emphasis-color);"></a>
        <button class="navbar-toggler" data-bs-target="#navcol-1" data-bs-toggle="collapse">
          <span class="visually-hidden">Toggle navigation</span>
          <span class="navbar-toggler-icon"></span>
        </button>
        <img class="navLink" id="logo"  height="46" src=${logo} style="color: var(--bs-nav-link-color); cursor: pointer;" width="183" alt="Logo">
        <div  class="d-flex collapse navbar-collapse justify-content-center" id="navcol-1">
          <ul id="navbarUl" class="navbar-nav w-50">
            <li class="navLink nav-item"></li>
          </ul>
        </div>
        <div class="navbar-nav" id="isConnectedDiv">
          <p class="m-3" data-uri="/profil" id="profil">${firstname}</p>
          <button class="btn shadow me-1" role="button" id="logout" >Se déconnecter</button> 
        </div>
      </div>
    </nav>
    `;

  const isConnectedDiv = document.getElementById('isConnectedDiv');
  const logoButton = document.getElementById('logo');
  const profilButton = document.getElementById('profil');
  if (isConnected()) {
    isConnectedDiv.hidden = false;

    logoButton.addEventListener('click',async (e)  => {
      e.preventDefault();
      const user = await getConnectedUser();
      if(user.role==="ETUDIANT"){
        return Navigate('/studentboard');
      }
      return Navigate('/dashboard');
    })

    profilButton.addEventListener('click', (e) => {
      e.preventDefault();
      return Navigate(profilButton.dataset.uri);
    })

    const logoutBtn = document.getElementById('logout');
    logoutBtn.addEventListener('click', (e) => {
      e.preventDefault();
      clearConnectedUser();
      isConnectedDiv.hidden = true;
      return Navigate('/');
    });

  } else {
    isConnectedDiv.hidden = true;
  }
};

export default Navbar;
