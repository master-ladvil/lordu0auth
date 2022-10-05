import Controller from '@ember/controller';
import { tracked } from '@glimmer/tracking';
import { on } from '@ember/modifier';
import { get } from '@ember/helper';
import { action } from '@ember/object';

export default class IndexController extends Controller {
    @action
    get(){
        $.ajax({
            url:"http://localhost:8080/lorduoauth/Sync",
            method:"GET",
            success:()=>{
                alert("Syncing started")
            },error:()=>{
                alert("Synced");
            }
        })
    }
}

