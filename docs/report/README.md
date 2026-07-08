# SAP Report
**Version:** v0.1.0  
**Last Updated:** 2026-07-05

This folder contains the LaTeX source for the SAP project report.

## Files

- `report.tex`: main LaTeX document.
- `assets/`: optional local images used only by the report.

## Build

From this folder, compile the report with:

```bash
pdflatex report.tex
pdflatex report.tex
```

The second run updates the table of contents and cross-references.

If `latexmk` is available, use:

```bash
latexmk -pdf report.tex
```
