# Design Notes + erste Ideen zur Kartenlogik & Discard-Mechanik Kartenlogik & Auswahl-Mechanik:

Erste Überlegungen, wie Karten auf der Hand gehalten, markiert und ausgewählt werden:

Eine Karte wird bei Klick „angehoben“ (selected)

Nochmaliger Klick = Deselection (wie beschrieben bei Nadine)

Ziel: intuitive Interaktion für Spielzug- und Abwurfentscheidung

Idee: Auswahlstatus visuell farblich hervorheben, z. B. grün für "spielen", rot für "abwerfen"

Discard-Mechanik (Abwerf-System):

Konzeptionelle Skizze:

Wenn Spieler A eine Karte abwirft, wird sie nicht sofort gelöscht, sondern landet in einem „Übergabestapel“ von Spieler B (erste Phase)

Erst wenn Spieler B dieselbe Karte ebenfalls abwirft, wird sie endgültig aus dem Spiel entfernt (zweite Phase)

Vorteil: Spieler können gezielt Karten „weitergeben“, um gemeinsam Poker-Hände zu optimieren -> stärkt Kooperation