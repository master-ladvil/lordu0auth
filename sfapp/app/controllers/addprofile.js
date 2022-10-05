import Controller from '@ember/controller';
import { tracked } from '@glimmer/tracking';
import { action } from '@ember/object';
import Ember from 'ember';

export default class AddprofileController extends Controller {
  @action
  get() {
    var name = document.getElementById('profilename').value;
    console.log(name);

    $.ajax({
      url: 'http://localhost:8080/lorduoauth/AddSfProfile',
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
}
