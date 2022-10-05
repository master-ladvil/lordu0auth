import Controller from '@ember/controller';
import { tracked } from '@glimmer/tracking';
import { action } from '@ember/object';

export default class AdddbuserController extends Controller {
  @action
  submituser() {
    var lastname = document.getElementById('lastname').value;
    var firstname = document.getElementById('firstname').value;
    var email = document.getElementById('email').value;
    var timezone = document.getElementById('timezone').value;
    var local = document.getElementById('local').value;
    var langkey = document.getElementById('langkey').value;
    var local = document.getElementById('local').value;
    var dpt = document.getElementById('dpt').value;
    var div = document.getElementById('div').value;
    var tit = document.getElementById('tit').value;
    var semail = document.getElementById('semail').value;
    var mobile = document.getElementById('mobile').value;

    $.ajax({
      url: 'http://localhost:8080/lorduoauth/AddDbUser',
      data: {
        lastname: lastname,
        firstname: firstname,
        email: email,
        timezone: timezone,
        locale: local,
        language: langkey,
        dpt: dpt,
        div: div,
        tit: tit,
        semail: semail,
        mobile: mobile,
      },
      method: 'GET',
      success: function (response) {
        alert(response);
      },
      error: function (xhr, status, error) {
        var errorMessage = xhr.status + ': ' + xhr.statusText;
        alert('error' + errorMessage);
      },
    });
  }
}
