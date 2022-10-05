import Controller from '@ember/controller';
import { tracked } from '@glimmer/tracking';
import { action } from '@ember/object';

export default class AddController extends Controller {
  @action
  submituser() {
    var username = document.getElementById('username').value;
    console.log(username);
    var lastname = document.getElementById('lastname').value;
    console.log(username);
    var firstname = document.getElementById('firstname').value;
    console.log(username);
    var email = document.getElementById('email').value;
    console.log(username);
    var isactive = document.getElementById('isactive').value;
    console.log(username);
    var alias = document.getElementById('alias').value;
    console.log(username);
    var timezone = document.getElementById('timezone').value;
    console.log(username);
    var local = document.getElementById('local').value;
    console.log(username);
    var emailencode = document.getElementById('emailencode').value;
    console.log(username);
    var profileid = document.getElementById('profileid').value;
    console.log(username);
    var langkey = document.getElementById('langkey').value;
    console.log(username);
    console.log(
      '\n' +
        username +
        '\n' +
        lastname +
        '\n' +
        firstname +
        '\n' +
        email +
        '\n' +
        isactive +
        '\n' +
        alias +
        '\n' +
        timezone +
        '\n' +
        local +
        '\n' +
        emailencode +
        '\n' +
        profileid +
        '\n' +
        langkey
    );
    $.ajax({
      url: 'http://localhost:8080/lorduoauth/SubmitUser',
      data: {
        username: username,
        lastname: lastname,
        firstname: firstname,
        email: email,
        isactive: isactive,
        alias: alias,
        timezone: timezone,
        local: local,
        emailencode: emailencode,
        profileid: profileid,
        langkey: langkey,
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
  @action
  get() {
    var name = document.getElementById('rolename').value;
    var name = document.getElementById('parent').value;
    var name = document.getElementById('roledesc').value;
    console.log(name);

    $.ajax({
      url: 'http://localhost:8080/lorduoauth/AddSfRoles',
      data: {
        name: name,
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
  @action
  getprofile() {
    var name = document.getElementById('profilename').value;
    var perm = document.getElementById('perm').value;
    console.log(name + perm);

    $.ajax({
      url: 'http://localhost:8080/lorduoauth/AddSfProfile',
      data: {
        name: name,
        perm: perm,
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
