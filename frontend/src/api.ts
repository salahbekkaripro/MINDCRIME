import type { Session, Referentiel, Liste, Analyse, Nature } from './types';

export class DossierApi {
  constructor(private authorization: string) {}

  private async requete<T>(url: string, options: RequestInit = {}): Promise<T> {
    const reponse = await fetch(url, {
      ...options, credentials: 'same-origin', cache: 'no-store',
      headers: { Authorization: this.authorization, ...options.headers },
    });
    const corps = await reponse.json();
    if (!reponse.ok) throw new Error(reponse.status === 401
      ? 'Identifiant ou mot de passe incorrect.'
      : corps.detail ?? corps.title ?? 'Le dossier est momentanément indisponible.');
    return corps as T;
  }
  session() { return this.requete<Session>('/api/v1/session'); }
  referentiel(id: string) { return this.requete<Referentiel>(`/api/v1/parties/${id}/referentiel`); }
  async etat(id: string) {
    for (let tentative = 0; tentative < 3; tentative++) {
      const [liste, analyse] = await Promise.all([
        this.requete<Liste>(`/api/v1/parties/${id}/temoignages`),
        this.requete<Analyse>(`/api/v1/parties/${id}/contradictions`),
      ]);
      if (liste.revision === analyse.revision) return { liste, analyse };
    }
    throw new Error('Le dossier a changé pendant la lecture. Actualisez pour consulter sa dernière version.');
  }
  soumettre(session: Session, corps: { suspectConcerneId: string; lieuId: string; instant: string; nature: Nature }) {
    return this.requete(`/api/v1/parties/${session.partieId}/temoignages`, {
      method: 'POST', headers: { 'Content-Type': 'application/json', 'X-XSRF-TOKEN': session.csrfToken },
      body: JSON.stringify(corps),
    });
  }
}
