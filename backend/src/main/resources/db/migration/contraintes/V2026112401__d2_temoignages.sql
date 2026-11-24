-- Migration D2 locale, jamais publiée ; numéro à coordonner avant intégration commune.
-- Référentiels provisoires : à adapter aux contrats Partie/acteurs/lieux de D1/D4.
CREATE TABLE d2_dossier (
    id UUID PRIMARY KEY,
    titre VARCHAR(180) NOT NULL,
    fuseau VARCHAR(80) NOT NULL
);
CREATE TABLE d2_acteur (
    partie_id UUID NOT NULL REFERENCES d2_dossier(id),
    id UUID NOT NULL,
    nom VARCHAR(120) NOT NULL,
    PRIMARY KEY (partie_id, id)
);
CREATE TABLE d2_lieu (
    partie_id UUID NOT NULL REFERENCES d2_dossier(id),
    id UUID NOT NULL,
    nom VARCHAR(120) NOT NULL,
    PRIMARY KEY (partie_id, id)
);
CREATE TABLE d2_acces (
    partie_id UUID NOT NULL,
    lecteur_id UUID NOT NULL,
    PRIMARY KEY (partie_id, lecteur_id),
    FOREIGN KEY (partie_id, lecteur_id) REFERENCES d2_acteur(partie_id, id)
);
CREATE TABLE d2_temoignage (
    id UUID PRIMARY KEY,
    partie_id UUID NOT NULL REFERENCES d2_dossier(id),
    auteur_id UUID NOT NULL,
    suspect_id UUID NOT NULL,
    lieu_id UUID NOT NULL,
    instant TIMESTAMP(0) WITH TIME ZONE NOT NULL,
    nature VARCHAR(12) NOT NULL CHECK (nature IN ('PRESENCE', 'ABSENCE')),
    visibilite VARCHAR(12) NOT NULL DEFAULT 'PUBLIC' CHECK (visibilite IN ('PUBLIC', 'PRIVE')),
    FOREIGN KEY (partie_id, auteur_id) REFERENCES d2_acteur(partie_id, id),
    FOREIGN KEY (partie_id, suspect_id) REFERENCES d2_acteur(partie_id, id),
    FOREIGN KEY (partie_id, lieu_id) REFERENCES d2_lieu(partie_id, id)
);
CREATE INDEX d2_temoignage_variable ON d2_temoignage(partie_id, suspect_id, instant);
