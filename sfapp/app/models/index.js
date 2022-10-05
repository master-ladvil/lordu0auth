import Model, { attr } from '@ember-data/model';
import Ember from 'ember';

export default class IndexModel extends Model {
  @attr name;
  @attr alias;
  @attr email;
  @attr username;
  @attr title;
  @attr companyname;
  @attr department;
  @attr division;
  @attr timezone;
  @attr local;
  @attr language;
  @attr manager;
  @attr rolename;
  @attr profilename;
  @attr isactive;
}
