import Model, { attr } from '@ember-data/model';

export default class ProfileModel extends Model {
  @attr profilename;
}
