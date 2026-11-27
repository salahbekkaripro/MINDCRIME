export interface CreerPartieResponse {
  partieId: string;
}

export interface GenererEnqueteRequest {
  seed?: number;
  nombreSuspects: number;
}

export interface CrimeDto {
  lieuId: string;
  lieuNom: string;
  creneauOrdre: number;
  creneauLibelle: string;
}

export interface SuspectDto {
  id: string;
  nom: string;
  profil: string;
}

export interface LieuDto {
  id: string;
  nom: string;
}

export interface IndiceVisibleDto {
  id: string;
  type: string;
  suspectId: string;
  lieuId: string;
  creneauLibelle: string;
  description: string;
}

export interface EnqueteVisibleDto {
  id: string;
  partieId: string;
  seed: number;
  crime: CrimeDto;
  suspects: SuspectDto[];
  lieux: LieuDto[];
  indicesVisibles: IndiceVisibleDto[];
  statutUnicite: string;
}

export interface ProblemDetail {
  type?: string;
  title?: string;
  status?: number;
  detail?: string;
}