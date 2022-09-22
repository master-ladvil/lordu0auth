import Controller from '@ember/controller';
import { action } from '@ember/object';
import { service } from '@ember/service';
import { tracked } from '@glimmer/tracking';
//import EmberResolver from 'ember-resolver';
import Ember from 'ember';
import $ from 'jquery';

export default class LoginController extends Controller {
  @tracked result = 0;
  @service router;
  //canLogin:true;
  @action
  get() {
    var that = this;

    var email = document.getElementById('email').value;
    var lname = document.getElementById('lname').value;
    console.log('name ->' + email);
    console.log('mobile ->' + lname);
    $.ajax({
      url: 'http://localhost:8080/lorduoauth/oauthlogin',
      method: 'GET',
      data: { email: email, lname: lname },
      success: function (response) {
        that.result = response;
        console.log('response-> ' + response);
        if (that.result == 1) {
          alert('sucess');
          that.transitionToRoute('consentscreen');
          console.log(that.result);
        } else {
          alert('Login failed');
        }
      },
      error: function (xhr, status, error) {
        var errorMessage = xhr.status + ':' + xhr.statusText;
        alert('error' + errorMessage);
      },
    });
  }
}
