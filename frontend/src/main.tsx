import React, { useState } from 'react';
import { createRoot } from 'react-dom/client';
import { DossierApi } from './api';
import type { Session, Referentiel, Declaration, Analyse, Nature } from './types';
import './style.css';

function Icon({ type = 'search' }: { type?: 'search' | 'file' | 'check' | 'alert' | 'refresh' }) {
  const paths = {
    search: <><circle cx="10.5" cy="10.5" r="6.5" /><path d="m16 16 5 5" /></>,
    file: <><path d="M7 3h7l4 4v14H6V3zM14 3v5h5M9 12h6M9 16h6" /></>,
    check: <><circle cx="12" cy="12" r="9" /><path d="m7.5 12 3 3 6-6" /></>,
    alert: <><path d="m12 3 10 18H2L12 3zM12 9v5M12 17v.1" /></>,
    refresh: <><path d="M20 7v5h-5M4 17v-5h5" /><path d="M6 7a7 7 0 0 1 12-1l2 3M4 15l2 3a7 7 0 0 0 12-1" /></>,
  };
  return <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">{paths[type]}</svg>;
}

const heure = (instant: string) => new Intl.DateTimeFormat('fr-FR', { timeZone: 'UTC', hour: '2-digit', minute: '2-digit', second: '2-digit' }).format(new Date(instant));
const date = (instant: string) => new Intl.DateTimeFormat('fr-FR', { timeZone: 'UTC', day: 'numeric', month: 'short', year: 'numeric' }).format(new Date(instant));

function App() {
  const [identifiant, setIdentifiant] = useState('demo');
  const [motDePasse, setMotDePasse] = useState('mindcrime-demo');
  const [api, setApi] = useState<DossierApi>();
  const [session, setSession] = useState<Session>();
  const [ref, setRef] = useState<Referentiel>();
  const [declarations, setDeclarations] = useState<Declaration[]>([]);
  const [analyse, setAnalyse] = useState<Analyse>();
  const [suspect, setSuspect] = useState('');
  const [lieu, setLieu] = useState('');
  const [instant, setInstant] = useState('2026-11-24T20:00:00');
  const [nature, setNature] = useState<Nature>('PRESENCE');
  const [occupe, setOccupe] = useState(false);
  const [erreur, setErreur] = useState('');
  const [message, setMessage] = useState('');
  const [enregistre, setEnregistre] = useState(false);
  const nomSuspect = (id: string) => ref?.suspects.find(s => s.id === id)?.nom ?? id;
  const nomLieu = (id: string) => ref?.lieux.find(l => l.id === id)?.nom ?? id;

  async function connecter(e: React.FormEvent) {
    e.preventDefault(); setOccupe(true); setErreur('');
    try {
      const client = new DossierApi(`Basic ${btoa(`${identifiant}:${motDePasse}`)}`);
      const connexion = await client.session();
      const [references, etat] = await Promise.all([client.referentiel(connexion.partieId), client.etat(connexion.partieId)]);
      setApi(client); setSession(connexion); setRef(references);
      setDeclarations(etat.liste.declarations); setAnalyse(etat.analyse);
      setSuspect(references.suspects.find(s => s.id !== connexion.lecteurId)?.id ?? references.suspects[0]?.id ?? '');
      setLieu(references.lieux[0]?.id ?? ''); setMotDePasse('');
    } catch (error) { setErreur(error instanceof Error ? error.message : 'Connexion impossible.'); }
    finally { setOccupe(false); }
  }
  async function actualiser() {
    if (!api || !session) return;
    setOccupe(true); setErreur(''); setMessage('');
    try { const etat = await api.etat(session.partieId); setDeclarations(etat.liste.declarations); setAnalyse(etat.analyse); }
    catch (error) { setErreur(error instanceof Error ? error.message : 'Lecture impossible.'); }
    finally { setOccupe(false); }
  }
  async function soumettre(e: React.FormEvent) {
    e.preventDefault(); if (!api || !session) return;
    setOccupe(true); setErreur(''); setMessage(''); setEnregistre(false);
    let sauvegarde = false;
    try {
      await api.soumettre(session, { suspectConcerneId: suspect, lieuId: lieu, instant: new Date(`${instant}Z`).toISOString(), nature });
      sauvegarde = true; setEnregistre(true);
      const etat = await api.etat(session.partieId);
      setDeclarations(etat.liste.declarations); setAnalyse(etat.analyse);
      setMessage('Témoignage enregistré. L’analyse du dossier a été actualisée.');
    } catch (error) {
      setErreur(`${sauvegarde ? 'Le témoignage est enregistré, mais l’affichage n’a pas pu être actualisé. ' : ''}${error instanceof Error ? error.message : 'Enregistrement impossible.'}`);
    } finally { setOccupe(false); }
  }
  const conflit = analyse?.verdict === 'CONTRADICTION';
  const impliques = new Set(analyse?.conflits.flatMap(c => c.faits.flatMap(f => f.sources)) ?? []);

  return <>
    <header className="topbar"><a className="brand" href="/" aria-label="MINDCRIME, accueil"><span className="brand-mark"><Icon /></span>MIND<span>CRIME</span></a>
      <span className="top-caption">L’enquête commence par les faits.</span>
      <span className="version">v0.1.0 · Démonstration</span></header>
    <main>
      {!session || !ref ? <section className="login-layout">
        <div><span className="eyebrow">SALLE D’ANALYSE</span><h1>Chaque récit<br />laisse une trace.</h1><p className="intro">Rassemblez les témoignages. Repérez les déclarations qui ne peuvent pas être vraies en même temps.</p><div className="intro-line" /><p className="muted">Un même suspect. Un même instant.<br />Des versions à confronter.</p></div>
        <form className="card login" onSubmit={connecter}><span className="card-kicker">VOTRE ESPACE D’ENQUÊTE</span><h2>Ouvrir le dossier</h2><p>Connectez-vous pour consulter et compléter les témoignages.</p>
          <label htmlFor="identifiant">Identifiant</label><input id="identifiant" autoComplete="username" required value={identifiant} onChange={e => setIdentifiant(e.target.value)} />
          <label htmlFor="mot-de-passe">Mot de passe</label><input id="mot-de-passe" type="password" autoComplete="current-password" required value={motDePasse} onChange={e => setMotDePasse(e.target.value)} />
          <p className="demo-note">Compte local de démonstration : <strong>demo</strong> / <strong>mindcrime-demo</strong></p>
          {erreur && <p role="alert" className="error">{erreur}</p>}
          <button className="primary" disabled={occupe}>{occupe ? 'Ouverture…' : 'Accéder au dossier'}</button>
        </form>
      </section> : <>
        <div className="breadcrumb">Enquêtes <span>/</span> {ref.titre} <span>/</span> <strong>Témoignages</strong></div>
        <section className="hero"><div><span className="eyebrow">DOSSIER D’ENQUÊTE</span><h1>{ref.titre}</h1><p>Confrontez les récits, faites apparaître les incompatibilités.</p></div><div className="hero-session"><span>SESSION ACTIVE</span><strong>{nomSuspect(session.lecteurId)}</strong><button onClick={() => { setSession(undefined); setApi(undefined); setRef(undefined); setErreur(''); setMessage(''); }} className="text-button">Fermer le dossier</button></div></section>
        <div className="workspace-heading"><div><span className="eyebrow dark">LES DÉCLARATIONS</span><h2>Témoignages & cohérence</h2></div><button className="secondary" disabled={occupe} onClick={actualiser}><Icon type="refresh" />Actualiser</button></div>
        {erreur && <div role="alert" className="error">{erreur}{enregistre && ' Utilisez « Actualiser » avant toute nouvelle saisie.'}</div>}
        {message && <div role="status" className="success-message"><Icon type="check" />{message}</div>}
        <div className="workspace">
          <form className="card declaration-form" onSubmit={soumettre}>
            <div className="section-title"><span className="square-icon"><Icon type="file" /></span><div><h3>Nouveau témoignage</h3><p>Consignez une présence ou une absence.</p></div></div>
            <label htmlFor="suspect">De qui parle le témoignage ?</label><select id="suspect" value={suspect} onChange={e => setSuspect(e.target.value)} required>{ref.suspects.map(s => <option key={s.id} value={s.id}>{s.nom}</option>)}</select>
            <fieldset><legend>Déclaration</legend><div className="nature-choice"><button type="button" aria-pressed={nature === 'PRESENCE'} className={nature === 'PRESENCE' ? 'selected' : ''} onClick={() => setNature('PRESENCE')}>Présence</button><button type="button" aria-pressed={nature === 'ABSENCE'} className={nature === 'ABSENCE' ? 'selected' : ''} onClick={() => setNature('ABSENCE')}>Absence</button></div></fieldset>
            <label htmlFor="lieu">Lieu concerné</label><select id="lieu" value={lieu} onChange={e => setLieu(e.target.value)} required>{ref.lieux.map(l => <option key={l.id} value={l.id}>{l.nom}</option>)}</select>
            <label htmlFor="instant">Date et heure précises <span className="label-note">UTC</span></label><input id="instant" type="datetime-local" step="1" required value={instant} onChange={e => setInstant(e.target.value)} />
            <div className="author"><span>Témoignage de</span><strong>{nomSuspect(session.lecteurId)}</strong></div>
            <button className="primary" disabled={occupe || !suspect || !lieu}>{occupe ? 'Enregistrement…' : 'Enregistrer et analyser'}</button>
            <p className="form-note">Votre déclaration est conservée, même si elle contredit un autre témoignage.</p>
          </form>
          <section className="card analysis" aria-labelledby="analysis-title">
            <div className="analysis-heading"><div><span className="eyebrow dark">LECTURE DU DOSSIER</span><h3 id="analysis-title">Analyse de cohérence</h3></div><span className="analysis-count">{analyse?.declarationsAnalysees ?? 0} témoignage{analyse?.declarationsAnalysees === 1 ? '' : 's'}</span></div>
            <div className={`verdict ${conflit ? 'conflict' : 'clear'}`} role="status"><Icon type={conflit ? 'alert' : 'check'} /><div><strong>{conflit ? `${analyse?.conflits.length} incompatibilité${analyse?.conflits.length === 1 ? '' : 's'} détectée${analyse?.conflits.length === 1 ? '' : 's'}` : analyse?.verdict === 'NON_VERIFIABLE' ? 'Informations insuffisantes' : declarations.length === 0 ? 'Le dossier attend vos témoignages' : 'Aucune contradiction détectée'}</strong><p>{conflit ? 'Certaines déclarations ne peuvent pas être vraies simultanément.' : declarations.length === 0 ? 'Enregistrez une première déclaration pour commencer l’analyse.' : 'Les déclarations analysées ne se contredisent pas selon les règles de présence et d’absence.'}</p></div></div>
            {analyse?.completude === 'INCOMPLETE' && <p className="error">Contrôle incomplet : certaines déclarations ne peuvent pas être analysées.</p>}
            {conflit ? <div className="conflict-list">{analyse?.conflits.map(c => <article className="conflict-card" key={c.motif + c.faits.map(f => f.sources.join()).join()}><span className="conflict-label">{c.motif === 'LIEUX_DISTINCTS' ? 'Présences simultanées' : 'Présence et absence'}</span><h4>{nomSuspect(c.faits[0].suspectConcerneId)} · {heure(c.faits[0].instant)} UTC</h4><p>{c.message}</p><div className="compared-facts">{c.faits.map(f => <div key={f.nature + f.lieuId}><strong>{f.nature === 'PRESENCE' ? 'Présent' : 'Absent'} · {nomLieu(f.lieuId)}</strong><span>{date(f.instant)} · {f.sources.length} déclaration{f.sources.length > 1 ? 's' : ''}</span><small>Références : {f.sources.map(id => id.slice(0, 8)).join(', ')}</small></div>)}</div></article>)}</div> : <div className="analysis-empty"><div className="empty-icon"><Icon /></div><h4>Les faits avant les conclusions.</h4><p>Un conflit signale des récits incompatibles.<br />Il ne désigne ni un menteur ni un coupable.</p></div>}
            <div className="scope-note"><Icon type="file" /><p>Cette analyse porte sur les témoignages qui vous sont accessibles. La faisabilité des déplacements n’est pas évaluée ici.</p></div>
          </section>
        </div>
        <section className="card history"><div className="history-heading"><div><h3>Déclarations enregistrées</h3><p>Les récits accessibles dans ce dossier.</p></div><span className="count-badge">{declarations.length}</span></div>
          {declarations.length ? <div className="table-scroll"><table><thead><tr><th>Suspect concerné</th><th>Déclaration</th><th>Lieu</th><th>Instant · UTC</th><th>Auteur</th><th>Analyse</th></tr></thead><tbody>{declarations.map(d => <tr key={d.id} id={d.id}><td><strong>{nomSuspect(d.suspectConcerneId)}</strong><small title={d.id}>Réf. {d.id.slice(0, 8)}</small></td><td><span className={`nature-badge ${d.nature.toLowerCase()}`}>{d.nature === 'PRESENCE' ? 'Présence' : 'Absence'}</span></td><td>{nomLieu(d.lieuId)}</td><td>{heure(d.instant)}<small>{date(d.instant)}</small></td><td>{nomSuspect(d.auteurId)}</td><td><span className={impliques.has(d.id) ? 'involved' : 'muted'}>{impliques.has(d.id) ? 'En conflit' : 'Sans conflit détecté'}</span></td></tr>)}</tbody></table></div> : <div className="history-empty">Aucun témoignage enregistré. Le premier récit vous appartient.</div>}
        </section>
        <footer><span>MINDCRIME · Salle d’analyse</span><span>Les conclusions restent entre vos mains.</span></footer>
      </>}
    </main>
  </>;
}

createRoot(document.getElementById('root')!).render(<React.StrictMode><App /></React.StrictMode>);
