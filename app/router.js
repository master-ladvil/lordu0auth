import EmberRouter from '@ember/routing/router';
import config from 'consents/config/environment';

export default class Router extends EmberRouter {
  location = config.locationType;
  rootURL = config.rootURL;
}

Router.map(function () {
  this.route('error');
  this.route('consentscreen');
  this.route('login',{ path: '/auth' });
});
