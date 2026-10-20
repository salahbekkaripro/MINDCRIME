"""Convert the dossier's Markdown subset to a four-page A4 PDF.

Requires Python 3 and ReportLab. Supports headings, paragraphs, bold,
bullet lists, plain URLs and tables. Explicit pagebreak comments define pages.
Run from any directory: python3 doc/generate_pdf.py
"""

from html import escape
from pathlib import Path
import json
import math
import re

from reportlab.lib import colors
from reportlab.lib.enums import TA_LEFT
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.graphics.shapes import Circle, Drawing, Line, Polygon, Rect, String
from reportlab.graphics import renderSVG
from reportlab.platypus import (
    PageBreak, Paragraph, Preformatted, SimpleDocTemplate, Spacer, Table, TableStyle,
)


ROOT = Path(__file__).resolve().parent
FONT_DIR = Path('/usr/share/fonts/truetype/dejavu')
for name, filename in [('Body', 'DejaVuSans.ttf'), ('Body-Bold', 'DejaVuSans-Bold.ttf'),
                       ('Mono', 'DejaVuSansMono.ttf')]:
    pdfmetrics.registerFont(TTFont(name, str(FONT_DIR / filename)))
pdfmetrics.registerFontFamily('Body', normal='Body', bold='Body-Bold')

styles = getSampleStyleSheet()
body = ParagraphStyle('DossierBody', fontName='Body', fontSize=8.5,
                      leading=11.5, spaceAfter=7, textColor=colors.HexColor('#24374a'))
heading = ParagraphStyle('DossierHeading', parent=body, fontName='Body-Bold',
                         fontSize=19, leading=23, spaceAfter=12,
                         textColor=colors.HexColor('#13283d'))
subheading = ParagraphStyle('DossierSubheading', parent=body, fontName='Body-Bold',
                            fontSize=10.2, leading=13, spaceBefore=8, spaceAfter=8,
                            textColor=colors.HexColor('#087e8b'),
                            backColor=colors.HexColor('#edf6f6'), borderPadding=5)
cell = ParagraphStyle('DossierCell', parent=body, fontSize=7.1, leading=10,
                      spaceAfter=0, alignment=TA_LEFT, splitLongWords=True)
header_cell = ParagraphStyle('DossierHeaderCell', parent=cell,
                             fontName='Body-Bold', textColor=colors.white)
code = ParagraphStyle('DossierCode', fontName='Mono', fontSize=6.1,
                      leading=8.3, spaceAfter=9, backColor=colors.HexColor('#f1f4f8'),
                      borderPadding=6)


def inline(text):
    text = escape(text)
    text = re.sub(r'\*\*(.+?)\*\*', r'<b>\1</b>', text)
    return re.sub(r'https://[^\s<]+', lambda m: '<link href="' + m[0] + '">' + m[0] + '</link>', text)


def radar():
    data = json.loads((ROOT / 'part-ia.json').read_text(encoding='utf-8'))
    names = ['Code', 'Tests', 'CI/CD', 'Rédaction', 'Débogage', 'Validation',
             'Algorithmique', 'Spécification', 'Conception', 'Idéation']
    axes = data['axes']
    if [axis['nom'] for axis in axes] != names:
        raise ValueError('Les dix axes communs doivent être conservés dans leur ordre.')
    mode = data.get('mode', 'previsionnel')
    if mode not in ('previsionnel', 'bilan'):
        raise ValueError('Mode attendu : previsionnel ou bilan.')
    for axis in axes:
        for key in ('ia', 'objectif', 'plafond'):
            if key == 'ia' and axis[key] is None:
                continue
            if not isinstance(axis[key], (int, float)) or not 0 <= axis[key] <= 100:
                raise ValueError(f'Valeur invalide : {axis}')
    width = A4[0] - 84
    drawing = Drawing(width, 264)
    real_color = colors.HexColor('#b15b19')
    muted = colors.HexColor('#738397')
    ink = colors.HexColor('#24374a')

    def point(cx, index, value):
        angle = math.pi / 2 - index * 2 * math.pi / 10
        return cx + 48 * value / 100 * math.cos(angle), 175 + 48 * value / 100 * math.sin(angle)

    def polygon(cx, values, color, fill=None, dashed=False):
        points = [coordinate for i, value in enumerate(values) for coordinate in point(cx, i, value)]
        shape = Polygon(points, strokeColor=color, fillColor=fill, strokeWidth=0.8)
        if dashed:
            shape.strokeDashArray = [3, 2]
        drawing.add(shape)

    for panel, cx in enumerate((width / 4, 3 * width / 4)):
        title = 'OBJECTIFS DE DÉVELOPPEMENT' if panel == 0 else 'USAGE DOCUMENTAIRE R1'
        subtitle = 'Prévisionnel' if panel == 0 else 'Estimations de cette session'
        drawing.add(String(cx, 253, title, fontName='Body-Bold', fontSize=7.7,
                           textAnchor='middle', fillColor=ink))
        drawing.add(String(cx, 241, subtitle, fontName='Body', fontSize=6.4,
                           textAnchor='middle', fillColor=muted))
        for value in (25, 50, 75, 100):
            polygon(cx, [value] * 10, colors.HexColor('#d3dae2'))
        for i, axis in enumerate(axes):
            x, y = point(cx, i, 100)
            drawing.add(Line(cx, 175, x, y, strokeColor=colors.HexColor('#d3dae2'), strokeWidth=0.4))
            x, y = point(cx, i, 128)
            anchor = 'start' if x > cx + 8 else 'end' if x < cx - 8 else 'middle'
            drawing.add(String(x, y - 2, axis['nom'], fontName='Body', fontSize=5.8,
                               textAnchor=anchor, fillColor=ink))
        if panel == 0:
            polygon(cx, [axis['objectif'] for axis in axes], colors.HexColor('#7953a5'), colors.HexColor('#e7def2'))
            polygon(cx, [axis['plafond'] for axis in axes], colors.HexColor('#287853'), dashed=True)
            legend = 'Violet : objectif · Vert pointillé : plafond'
        elif mode == 'bilan':
            # Radial markers keep missing activities absent, without broken polygons.
            for i, axis in enumerate(axes):
                if axis['ia'] is None:
                    continue
                x, y = point(cx, i, axis['ia'])
                drawing.add(Line(cx, 175, x, y, strokeColor=real_color, strokeWidth=1.4))
                drawing.add(Circle(x, y, 2, fillColor=real_color, strokeColor=real_color))
            legend = 'Orange : part estimée · N/A : sans point'
        else:
            legend = 'Bilan non affiché en mode prévisionnel'
        drawing.add(String(cx, 99, legend, fontName='Body', fontSize=6,
                           textAnchor='middle', fillColor=ink))
    drawing.add(Line(width / 2, 95, width / 2, 259,
                     strokeColor=colors.HexColor('#dbe4ed'), strokeWidth=0.5))
    # Two compact blocks share one table: five skills per half, no ratios in axis labels.
    for half in range(2):
        left = half * width / 2
        drawing.add(String(left + 5, 81, 'Compétence', fontName='Body-Bold', fontSize=6.3, fillColor=ink))
        drawing.add(String(left + 158, 81, 'Objectif', fontName='Body-Bold', fontSize=6.3, fillColor=ink))
        drawing.add(String(left + 220, 81, 'R1 estimé', fontName='Body-Bold', fontSize=6.3,
                           textAnchor='middle', fillColor=ink))
        for row, axis in enumerate(axes[half * 5:half * 5 + 5]):
            y = 66 - row * 13
            if row % 2 == 0:
                drawing.add(Rect(left, y - 3, width / 2 - 3, 12,
                                 fillColor=colors.HexColor('#f0f5f9'), strokeColor=None))
            drawing.add(String(left + 5, y, axis['nom'], fontName='Body', fontSize=6.4, fillColor=ink))
            drawing.add(String(left + 172, y, f"{axis['objectif']} %", fontName='Body', fontSize=6.4,
                               textAnchor='middle', fillColor=colors.HexColor('#7953a5')))
            actual = 'N/A' if axis['ia'] is None else f"{axis['ia']} %"
            drawing.add(String(left + 220, y, actual, fontName='Body', fontSize=6.4,
                               textAnchor='middle', fillColor=real_color))
    drawing.add(String(width / 2, 1, 'Échelle commune : 0 à 100 % · N/A ne signifie pas 0 %',
                       fontName='Body', fontSize=6, textAnchor='middle', fillColor=muted))
    renderSVG.drawToFile(drawing, str(ROOT / 'part-ia.svg'))
    return drawing


def parse(source):
    lines = source.splitlines()
    flow = []
    index = 0
    while index < len(lines):
        line = lines[index].strip()
        if not line:
            index += 1
            continue
        if line == '<!-- pagebreak -->':
            flow.append(PageBreak())
        elif line.startswith('```'):
            block = []
            index += 1
            while index < len(lines) and not lines[index].strip().startswith('```'):
                block.append(lines[index])
                index += 1
            if index == len(lines):
                raise ValueError('Bloc de code Markdown non fermé.')
            flow.append(Preformatted('\n'.join(block), code))
        elif line == '![Part IA par compétence](part-ia.svg)':
            flow.append(radar())
        elif line.startswith('# '):
            flow.append(Paragraph(inline(line[2:]), heading))
        elif line.startswith('## '):
            flow.append(Paragraph(inline(line[3:]), subheading))
        elif line.startswith('|'):
            rows = []
            while index < len(lines) and lines[index].strip().startswith('|'):
                values = [v.strip() for v in lines[index].strip().strip('|').split('|')]
                if not all(re.fullmatch(r':?-+:?', v) for v in values):
                    style = header_cell if not rows else cell
                    rows.append([Paragraph(inline(v), style) for v in values])
                index += 1
            count = len(rows[0])
            # Give IDs little space and prose columns most of the width.
            weights = {4: [0.055, 0.315, 0.315, 0.315],
                       3: [0.28, 0.59, 0.13], 2: [0.16, 0.84]}[count]
            if count == 4 and 'Étudiant' in rows[0][1].getPlainText():
                weights = [0.055, 0.24, 0.32, 0.385]
            table = Table(rows, colWidths=[w * (A4[0] - 84) for w in weights], repeatRows=1)
            table.setStyle(TableStyle([
                ('BACKGROUND', (0, 0), (-1, 0), colors.HexColor('#18364e')),
                ('ROWBACKGROUNDS', (0, 1), (-1, -1),
                 [colors.HexColor('#f0f5f9'), colors.white]),
                ('VALIGN', (0, 0), (-1, -1), 'TOP'),
                ('LINEBELOW', (0, 0), (-1, 0), 1.2, colors.HexColor('#16a6a1')),
                ('LINEBELOW', (0, 1), (-1, -1), 0.3, colors.HexColor('#dbe4ed')),
                ('LEFTPADDING', (0, 0), (-1, -1), 5),
                ('RIGHTPADDING', (0, 0), (-1, -1), 5),
                ('TOPPADDING', (0, 0), (-1, -1), 5),
                ('BOTTOMPADDING', (0, 0), (-1, -1), 5),
            ]))
            flow.extend([table, Spacer(1, 8)])
            continue
        elif line.startswith('- '):
            flow.append(Paragraph('• ' + inline(line[2:]), body))
        else:
            paragraph = [line]
            while index + 1 < len(lines) and lines[index + 1].strip():
                upcoming = lines[index + 1].strip()
                if upcoming.startswith(('#', '|', '- ', '<!--', '```')):
                    break
                index += 1
                paragraph.append(upcoming)
            flow.append(Paragraph(inline(' '.join(paragraph)), body))
        index += 1
    return flow


def main():
    source = (ROOT / 'dossier.md').read_text(encoding='utf-8')
    if source.count('<!-- pagebreak -->') != 3:
        raise ValueError('Le Markdown doit définir exactement quatre pages.')
    target = ROOT / 'dossier.pdf'
    temporary = ROOT / 'dossier.build.pdf'
    page_count = []

    def footer(canvas, document):
        page_count.append(document.page)
        canvas.saveState()
        width, height = A4
        canvas.setFillColor(colors.HexColor('#13283d'))
        canvas.rect(0, height - 43, width, 43, fill=1, stroke=0)
        canvas.setFillColor(colors.HexColor('#22c2b5'))
        canvas.rect(0, height - 46, width, 3, fill=1, stroke=0)
        canvas.setFont('Body-Bold', 11)
        canvas.setFillColor(colors.white)
        canvas.drawString(42, height - 28, 'MINDCRIME')
        canvas.setFont('Body', 7)
        canvas.drawRightString(width - 42, height - 27, 'DOSSIER R1  /  20 OCTOBRE 2026')
        canvas.setStrokeColor(colors.HexColor('#dbe4ed'))
        canvas.line(42, 38, width - 42, 38)
        canvas.setFont('Body', 7)
        canvas.setFillColor(colors.HexColor('#657080'))
        canvas.drawString(42, 25, 'MINDCRIME · M1 MIAGE Apprentissage · Dossier R1')
        canvas.setFillColor(colors.HexColor('#087e8b'))
        canvas.roundRect(width - 77, 15, 35, 18, 4, fill=1, stroke=0)
        canvas.setFillColor(colors.white)
        canvas.setFont('Body-Bold', 7)
        canvas.drawCentredString(width - 59.5, 21, f'{document.page} / 4')
        canvas.restoreState()

    document = SimpleDocTemplate(str(temporary), pagesize=A4,
                                 rightMargin=42, leftMargin=42,
                                 topMargin=62, bottomMargin=47,
                                 title='MINDCRIME — Dossier de lancement R1',
                                 author='Équipe MINDCRIME')
    document.build(parse(source), onFirstPage=footer, onLaterPages=footer)
    if len(page_count) != 4:
        raise RuntimeError(f'Conversion refusée : {len(page_count)} pages ; ajuster le Markdown ou le style. Brouillon : {temporary}')
    temporary.replace(target)
    print(f'PDF généré : {target} — {len(page_count)} pages A4')


if __name__ == '__main__':
    main()
