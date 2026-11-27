import { useState } from "react";
import { consulterEnquete, creerPartie, genererEnquete } from "./api/enqueteApi";
import { EnqueteVisibleDto } from "./types/enquete";

export function App() {
  const [partieId, setPartieId] = useState<string>("");
  const [seedTexte, setSeedTexte] = useState<string>("42");
  const [nombreSuspects, setNombreSuspects] = useState<number>(5);
  const [enquete, setEnquete] = useState<EnqueteVisibleDto | null>(null);
  const [chargement, setChargement] = useState<boolean>(false);
  const [erreur, setErreur] = useState<string | null>(null);

  async function lancerNouvelleEnquete() {
    setErreur(null);
    setChargement(true);
    try {
      let idCourant = partieId;
      if (!idCourant) {
        const nouvellePartie = await creerPartie();
        idCourant = nouvellePartie.partieId;
        setPartieId(idCourant);
      }
      const seed = seedTexte.trim() === "" ? undefined : Number(seedTexte);
      if (seed !== undefined && Number.isNaN(seed)) {
        throw new Error("La graine (seed) doit etre un entier valide.");
      }
      const resultat = await genererEnquete(idCourant, {
        seed,
        nombreSuspects
      });
      setEnquete(resultat);
    } catch (e) {
      setErreur(e instanceof Error ? e.message : "Erreur inconnue");
    } finally {
      setChargement(false);
    }
  }

  async function rechargerDepuisApi() {
    if (!partieId || !enquete) {
      return;
    }
    setErreur(null);
    setChargement(true);
    try {
      const rechargee = await consulterEnquete(partieId, enquete.id);
      setEnquete(rechargee);
    } catch (e) {
      setErreur(e instanceof Error ? e.message : "Erreur inconnue");
    } finally {
      setChargement(false);
    }
  }

  async function reinitialiserPartie() {
    setErreur(null);
    setChargement(true);
    try {
      const nouvellePartie = await creerPartie();
      setPartieId(nouvellePartie.partieId);
      setEnquete(null);
    } catch (e) {
      setErreur(e instanceof Error ? e.message : "Erreur inconnue");
    } finally {
      setChargement(false);
    }
  }

  return (
    <div className="container">
      <header>
        <h1>MINDCRIME — Parcours S1 (D1)</h1>
        <p>Generation et consultation d&apos;une enquete a solution unique sans divulgation du coupable.</p>
      </header>

      <section className="panel">
        <div className="controls">
          <div className="field">
            <label htmlFor="seed">Graine (seed, optionnelle)</label>
            <input
              id="seed"
              type="text"
              value={seedTexte}
              onChange={(e) => setSeedTexte(e.target.value)}
              placeholder="ex. 42"
            />
          </div>

          <div className="field">
            <label htmlFor="suspects">Nombre de suspects</label>
            <select
              id="suspects"
              value={nombreSuspects}
              onChange={(e) => setNombreSuspects(Number(e.target.value))}
            >
              <option value={3}>3 suspects</option>
              <option value={4}>4 suspects</option>
              <option value={5}>5 suspects</option>
              <option value={6}>6 suspects</option>
            </select>
          </div>

          <button onClick={lancerNouvelleEnquete} disabled={chargement}>
            {chargement ? "Generation..." : "Generer l'enquete"}
          </button>

          <button className="secondary" onClick={reinitialiserPartie} disabled={chargement}>
            Nouvelle partie
          </button>

          {enquete && (
            <button className="secondary" onClick={rechargerDepuisApi} disabled={chargement}>
              Recharger via GET
            </button>
          )}
        </div>

        {partieId && (
          <div className="meta">
            <span className="badge">Partie : {partieId}</span>
            {enquete && <span className="badge">Enquete : {enquete.id}</span>}
            {enquete && <span className="badge">Seed : {enquete.seed}</span>}
            {enquete && <span className="badge ok">Statut : {enquete.statutUnicite}</span>}
          </div>
        )}
      </section>

      {erreur && <div className="error">{erreur}</div>}

      {enquete && (
        <>
          <section className="panel">
            <h2>Contexte du crime</h2>
            <p>
              Un crime a ete commis dans le lieu <strong>{enquete.crime.lieuNom}</strong> ({enquete.crime.lieuId})
              au creneau <strong>{enquete.crime.creneauLibelle}</strong>.
            </p>
            <div className="meta">
              {enquete.lieux.map((lieu) => (
                <span key={lieu.id} className="badge">
                  {lieu.id} — {lieu.nom}
                </span>
              ))}
            </div>
          </section>

          <section className="panel">
            <h2>Suspects ({enquete.suspects.length})</h2>
            <div className="grid">
              {enquete.suspects.map((suspect) => (
                <div key={suspect.id} className="card">
                  <h3>
                    {suspect.nom} ({suspect.id})
                  </h3>
                  <p>{suspect.profil}</p>
                </div>
              ))}
            </div>
          </section>

          <section className="panel">
            <h2>Indices visibles ({enquete.indicesVisibles.length})</h2>
            <div className="indice-list">
              {enquete.indicesVisibles.map((indice) => (
                <div key={indice.id} className="indice-item">
                  <div className="indice-header">
                    <span>
                      <strong>{indice.id}</strong> — {indice.type}
                    </span>
                    <span>
                      Suspect {indice.suspectId} | Lieu {indice.lieuId} | {indice.creneauLibelle}
                    </span>
                  </div>
                  <div>{indice.description}</div>
                </div>
              ))}
            </div>
          </section>
        </>
      )}
    </div>
  );
}