import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';

export default class MapRoute extends Route {
  @service store;
  async model() {
    console.log('inside router');
    //console.log(this.store.findAll('My'));
    return {
      sf: await this.store.findAll('sfattr'),
      db: await this.store.findAll('dbattr'),
      map: await this.store.findAll('map'),
    };
  }
}
