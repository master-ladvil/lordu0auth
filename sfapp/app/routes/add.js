import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';

export default class AddRoleRoute extends Route {
  @service store;
  async model() {
    console.log('inside router');
    //console.log(this.store.findAll('My'));
    return {
      user: await this.store.findAll('add'),
      profile: await this.store.findAll('addprofile'),
      role: await this.store.findAll('addrole'),
    };
  }
}
