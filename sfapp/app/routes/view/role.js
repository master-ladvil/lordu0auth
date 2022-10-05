import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';

export default class ViewRoleRoute extends Route {
  @service store;
  async model() {
    console.log('inside router');
    //console.log(this.store.findAll('My'));
    return await this.store.findAll('role');
  }
}
