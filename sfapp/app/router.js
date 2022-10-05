import EmberRouter from '@ember/routing/router';
import config from 'sf/config/environment';

export default class Router extends EmberRouter {
  location = config.locationType;
  rootURL = config.rootURL;
}

Router.map(function () {
  this.route('view', function () {
    this.route('profile');
    this.route('role');
  });
  this.route('add');
  this.route('adddbuser');
  this.route('map');
});
