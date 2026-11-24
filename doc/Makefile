.PHONY: all equipe release

PYTHON ?= python3
export VERSION

all: equipe

equipe:
	$(PYTHON) outils/generate_pdf.py

release:
	@test -n "$$VERSION" || (echo "VERSION est obligatoire, par exemple VERSION=v0.1.0" >&2; exit 2)
	$(PYTHON) outils/generate_release.py "$$VERSION"
