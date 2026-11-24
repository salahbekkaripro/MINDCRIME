export type Nature = 'PRESENCE' | 'ABSENCE';
export type Reference = { id: string; nom: string };
export type Referentiel = { partieId: string; titre: string; fuseau: string; suspects: Reference[]; lieux: Reference[] };
export type Session = { lecteurId: string; nom: string; partieId: string; csrfToken: string };
export type Declaration = { id: string; partieId: string; auteurId: string; suspectConcerneId: string; lieuId: string; instant: string; nature: Nature };
export type Fait = { suspectConcerneId: string; lieuId: string; instant: string; nature: Nature; sources: string[] };
export type Analyse = {
  partieId: string; revision: string;
  verdict: 'CONTRADICTION' | 'AUCUNE_CONTRADICTION_DETECTEE' | 'NON_VERIFIABLE';
  completude: 'COMPLETE' | 'INCOMPLETE'; declarationsAnalysees: number;
  conflits: { motif: string; message: string; faits: Fait[] }[];
  nonExploitables: { id: string; raisons: string[] }[];
};
export type Liste = { revision: string; declarations: Declaration[] };
