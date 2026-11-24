"""Assemble une photographie documentaire de release MINDCRIME."""

from argparse import ArgumentParser
from datetime import datetime, timezone
from html import escape
from pathlib import Path
import json
import math
import posixpath
import re
import shutil
import subprocess
import sys
import tempfile


DOC_ROOT = Path(__file__).resolve().parents[1]
AXES = [
    'Code', 'Tests', 'CI/CD', 'Rédaction', 'Débogage',
    'Validation', 'Algorithmique', 'Spécification', 'Conception', 'Idéation',
]
COLORS = ['#007f86', '#7353a6', '#d66b19', '#3d7f3a', '#bb3e5d']


def load_json(path):
    return json.loads(path.read_text(encoding='utf-8'))


def validate_member(member, data, version):
    identity = data.get('membre', {})
    if identity.get('id') != member['id'] or identity.get('github') != member['github']:
        raise ValueError(f"Identité incohérente dans {member['dossier']}/part-ia.json")
    if data.get('version') != version:
        raise ValueError(f"{member['id']} : renseigner le bilan IA pour {version} avant l'assemblage.")
    axes = data.get('axes', [])
    if [axis.get('nom') for axis in axes] != AXES:
        raise ValueError(f"Les dix axes sont invalides dans {member['dossier']}/part-ia.json")
    for axis in axes:
        value = axis.get('ia')
        if value is not None and (not isinstance(value, (int, float)) or not 0 <= value <= 100):
            raise ValueError(f"Part IA invalide pour {member['id']} / {axis.get('nom')}")
        for key in ('objectif', 'plafond'):
            limit = axis.get(key)
            if not isinstance(limit, (int, float)) or not 0 <= limit <= 100:
                raise ValueError(f"{key} invalide pour {member['id']} / {axis.get('nom')}")


def source_commit():
    result = subprocess.run(
        ['git', 'rev-parse', 'HEAD'], cwd=DOC_ROOT.parent,
        check=True, capture_output=True, text=True,
    )
    return result.stdout.strip()


def worktree_is_dirty():
    result = subprocess.run(
        ['git', 'status', '--porcelain'], cwd=DOC_ROOT.parent,
        check=True, capture_output=True, text=True,
    )
    return bool(result.stdout.strip())


def generate_spider(path, members):
    width, height = 760, 560
    cx, cy, radius = 330, 260, 170

    def point(index, value):
        angle = -math.pi / 2 + index * 2 * math.pi / len(AXES)
        return cx + radius * value / 100 * math.cos(angle), cy + radius * value / 100 * math.sin(angle)

    lines = [
        f'<svg xmlns="http://www.w3.org/2000/svg" width="{width}" height="{height}" viewBox="0 0 {width} {height}">',
        '<rect width="100%" height="100%" fill="#ffffff"/>',
        '<text x="380" y="28" text-anchor="middle" font-family="sans-serif" font-size="18" fill="#18364e">Part IA par membre</text>',
    ]
    for level in (25, 50, 75, 100):
        points = ' '.join(f'{x:.1f},{y:.1f}' for x, y in (point(i, level) for i in range(len(AXES))))
        lines.append(f'<polygon points="{points}" fill="none" stroke="#d3dae2" stroke-width="1"/>')
    for index, name in enumerate(AXES):
        x, y = point(index, 100)
        lx, ly = point(index, 116)
        anchor = 'start' if lx > cx + 8 else 'end' if lx < cx - 8 else 'middle'
        lines.append(f'<line x1="{cx}" y1="{cy}" x2="{x:.1f}" y2="{y:.1f}" stroke="#d3dae2"/>')
        lines.append(f'<text x="{lx:.1f}" y="{ly:.1f}" text-anchor="{anchor}" font-family="sans-serif" font-size="11" fill="#24374a">{escape(name)}</text>')
    for member, color in zip(members, COLORS):
        values = [axis.get('ia') for axis in member['axes']]
        if all(isinstance(value, (int, float)) for value in values):
            points = ' '.join(f'{x:.1f},{y:.1f}' for x, y in (point(i, value) for i, value in enumerate(values)))
            lines.append(f'<polygon points="{points}" fill="{color}" fill-opacity="0.08" stroke="{color}" stroke-width="2"/>')
        else:
            for index, value in enumerate(values):
                if isinstance(value, (int, float)):
                    x, y = point(index, value)
                    lines.append(f'<circle cx="{x:.1f}" cy="{y:.1f}" r="4" fill="{color}"/>')
    for index, (member, color) in enumerate(zip(members, COLORS)):
        y = 455 + index * 19
        completed = sum(axis.get('ia') is not None for axis in member['axes'])
        lines.append(f'<rect x="185" y="{y - 10}" width="14" height="14" fill="{color}"/>')
        label = f"{member['id']} · {member['nom']} · {completed}/10 axes renseignés"
        lines.append(f'<text x="207" y="{y + 1}" font-family="sans-serif" font-size="12" fill="#24374a">{escape(label)}</text>')
    lines.append('</svg>')
    path.write_text('\n'.join(lines) + '\n', encoding='utf-8')


def rebase_links(source, directory):
    def rebase(match):
        label, target = match.groups()
        if re.match(r'(?:[a-zA-Z][a-zA-Z0-9+.-]*:|/|#)', target):
            return match.group(0)
        return f'{label}({posixpath.normpath(posixpath.join(directory, target))})'

    return re.sub(r'(\[[^\]\n]+\])\(([^()\s]+)\)', rebase, source)


def assemble_markdown(version, commit, members, dirty):
    rows = ['| Membre | Compte | Axes IA renseignés |', '| --- | --- | --- |']
    for member in members:
        completed = sum(axis.get('ia') is not None for axis in member['axes'])
        rows.append(f"| {member['id']} · {member['nom']} | {member['github']} | {completed}/10 |")

    parts = [
        f'# MINDCRIME — Dossier de release {version}',
        '',
        f'Commit de base {commit}.',
        *(['Cet assemblage inclut des modifications locales.'] if dirty else []),
        '',
        '## Contributions intégrées',
        '',
        *rows,
        '',
        "[Araignée des cinq membres](part-ia.svg). Une valeur absente est affichée comme N/A.",
        '',
        '<!-- pagebreak -->',
        '',
        '# Documentation commune',
        '',
    ]
    common = (DOC_ROOT / 'equipe' / 'dossier.md').read_text(encoding='utf-8')
    common = common.replace(
        '![Part IA par compétence](part-ia.svg)',
        'Le bilan IA historique de R1 est conservé dans equipe/part-ia.json et equipe/part-ia.svg.',
    )
    parts.extend([rebase_links(common, 'equipe'), ''])
    for member in members:
        directory = f"membres/{member['dossier']}"
        dossier = (DOC_ROOT / directory / 'dossier.md').read_text(encoding='utf-8')
        parts.extend([
            '<!-- pagebreak -->',
            '',
            rebase_links(dossier, directory),
            '',
        ])
    return '\n'.join(parts)


def main():
    parser = ArgumentParser(description='Fige les documents communs et individuels pour une release.')
    parser.add_argument('version', help='Version au format vX.Y.Z, par exemple v0.1.0')
    parser.add_argument('--output', type=Path, help='Destination alternative, utile pour une vérification locale')
    args = parser.parse_args()
    if not re.fullmatch(r'v\d+\.\d+\.\d+(?:-[0-9A-Za-z.-]+)?', args.version):
        raise SystemExit('Version invalide : format attendu vX.Y.Z ou vX.Y.Z-suffixe.')
    dirty = worktree_is_dirty()

    destination = args.output or DOC_ROOT / 'releases' / args.version
    if destination.exists():
        raise SystemExit(f'Release déjà présente, aucun écrasement autorisé : {destination}')
    destination.parent.mkdir(parents=True, exist_ok=True)

    index = load_json(DOC_ROOT / 'membres' / 'index.json')['membres']
    members = []
    for member in index:
        member_root = DOC_ROOT / 'membres' / member['dossier']
        for required in ('dossier.md', 'part-ia.json'):
            if not (member_root / required).is_file():
                raise FileNotFoundError(member_root / required)
        data = load_json(member_root / 'part-ia.json')
        validate_member(member, data, args.version)
        members.append({**member, 'axes': data['axes'], 'feature': data.get('feature', '')})

    commit = source_commit()
    temporary = Path(tempfile.mkdtemp(prefix=f'.{args.version}-', dir=destination.parent))
    try:
        shutil.copytree(DOC_ROOT / 'equipe', temporary / 'equipe')
        shutil.copytree(DOC_ROOT / 'modeles', temporary / 'modeles')
        for member in members:
            shutil.copytree(
                DOC_ROOT / 'membres' / member['dossier'],
                temporary / 'membres' / member['dossier'],
            )
        manifest = {
            'version': args.version,
            'commit': commit,
            'copie_de_travail': 'modifiée' if dirty else 'propre',
            'genere_le': datetime.now(timezone.utc).isoformat(),
            'methode': 'Données individuelles copiées sans moyenne globale ; null reste N/A.',
            'axes': AXES,
            'membres': members,
        }
        (temporary / 'part-ia.json').write_text(
            json.dumps(manifest, ensure_ascii=False, indent=2) + '\n', encoding='utf-8'
        )
        generate_spider(temporary / 'part-ia.svg', members)
        (temporary / 'dossier.md').write_text(
            assemble_markdown(args.version, commit, members, dirty), encoding='utf-8'
        )
        subprocess.run([
            sys.executable, str(DOC_ROOT / 'outils' / 'generate_pdf.py'),
            '--source', str(temporary / 'dossier.md'),
            '--output', str(temporary / 'dossier.pdf'),
            '--expected-pages', '0',
            '--label', f'DOSSIER RELEASE  /  {args.version}',
            '--title', f'MINDCRIME — Dossier de release {args.version}',
        ], check=True)
        temporary.replace(destination)
    except Exception:
        shutil.rmtree(temporary, ignore_errors=True)
        raise
    print(f'Release documentaire générée : {destination}')


if __name__ == '__main__':
    main()
