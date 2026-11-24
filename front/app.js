// Point d'entrée : initialise chaque section du front.
import * as api from './api.js';
import { $, tenter } from './commun.js';

tenter(async () => {
  const { name, version } = await api.info();
  $('#version').textContent = `${name} ${version}`;
});
