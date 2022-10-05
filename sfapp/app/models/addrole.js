import Model, { attr } from '@ember-data/model';

export default class AddRoleModel extends Model {
  @attr rolename;
  @attr rid;
}
