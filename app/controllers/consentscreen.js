import Controller from '@ember/controller';
import { action } from '@ember/object';
import { service } from '@ember/service';
import { tracked } from '@glimmer/tracking';
//import EmberResolver from 'ember-resolver';
import Ember from 'ember';
import $ from 'jquery';

export default class ConsentscreenController extends Controller {
    @tracked scoped
    scope = ()=>{
        var dis = this;
        $.ajax({
            url:'http://localhost:8080/lorduoauth/Auth',
            method:'POST',
            success : (response) =>{
                console.log(response);
            
                dis.scoped = response;
            },
            error: function (xhr, status, error) {
                var errorMessage = xhr.status + ':' + xhr.statusText;
                alert('error' + errorMessage);
              },
        })
    }

    @action
    get(){
        console.log("Get")
        $.ajax({
            url : 'http://localhost:8080/lorduoauth/Elloauth',
            method:'GET',
        })
    }
}
