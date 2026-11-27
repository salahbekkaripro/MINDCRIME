import {
  CreerPartieResponse,
  EnqueteVisibleDto,
  GenererEnqueteRequest,
  ProblemDetail
} from "../types/enquete";

const API_BASE = import.meta.env.VITE_API_URL ?? "http://localhost:8080";

async function lireReponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    let message = `Erreur HTTP ${response.status}`;
    try {
      const problem = (await response.json()) as ProblemDetail;
      if (problem.title || problem.detail) {
        message = `${problem.title ?? "Erreur"} (${problem.status ?? response.status}) : ${problem.detail ?? ""}`;
      }
    } catch {
      message = `Erreur HTTP ${response.status}`;
    }
    throw new Error(message);
  }
  return (await response.json()) as T;
}

export async function creerPartie(): Promise<CreerPartieResponse> {
  const response = await fetch(`${API_BASE}/api/v1/parties`, {
    method: "POST"
  });
  return lireReponse<CreerPartieResponse>(response);
}

export async function genererEnquete(
  partieId: string,
  payload: GenererEnqueteRequest
): Promise<EnqueteVisibleDto> {
  const response = await fetch(`${API_BASE}/api/v1/parties/${encodeURIComponent(partieId)}/enquetes`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(payload)
  });
  return lireReponse<EnqueteVisibleDto>(response);
}

export async function consulterEnquete(
  partieId: string,
  enqueteId: string
): Promise<EnqueteVisibleDto> {
  const response = await fetch(
    `${API_BASE}/api/v1/parties/${encodeURIComponent(partieId)}/enquetes/${encodeURIComponent(enqueteId)}`
  );
  return lireReponse<EnqueteVisibleDto>(response);
}