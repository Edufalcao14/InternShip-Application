import Swal from 'sweetalert2'

const nonAuthorizedPopUp = () => {

  Swal.fire({
    toast: true,
    position: "center",
    text: "Vous n'êtes pas autorisé(e) à effectuer cette opération." ,
    icon: "error",
    button: false,
    timer: 3000,
    timerProgressBar: true,
    showConfirmButton: false,
  });
}

export default nonAuthorizedPopUp;