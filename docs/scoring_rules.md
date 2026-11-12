##Basics
Stapel: 2x52 (kein Joker)
Zielpunktzahl: 500
Kapazität der Spielerhand: 8
Verfügbare Züge: 4
Verfügbare Abwürfe: 4
Maximale Anzahl der Runden: 8 (4 abgeworfene, 4 gespielte)

##Vollständiger Spielablauf 
Die Spieler beginnen mit 8 Karten und haben die Möglichkeit, jede Runde zu spielen oder abzulegen (Aktion)
Nach jeder Aktion werden Karten vom Stapel genommen, um wieder auf 8 Karten aufzufüllen
Dann wieder spielen oder ablegen (Aktion)
Endbedingungen:
1. das Spiel endet automatisch, wenn die Chance zu spielen 0 wird (Ablegen = 0 beendet das Spiel nicht) -> Fail
2. wenn der Punktstand in der Mitte des Spiels das Ziel erreicht, endet das Spiel automatisch -> Win
UML link:
(https://www.figma.com/board/7ENkVdTERbxKF3VZir6MF2/Welcome-to-FigJam?node-id=0-1&t=Y24Fox9kAXohRXlP-1)

##Punkte-Regeln
Es gibt eine Basisformel, die auf dem Deck basiert (siehe “Typ”), die Punkte der tatsächlich gespielten Karten werden zur Basispunktzahl addiert und dann mit dem Multiplikator multipliziert, der die Anzahl der Punkte angibt, die man für die Runde erhält 

Typ:
Basispunktzahl x Multiplikator

Straight Flush
100x8
Four of a kind(Quads)
60x7
Full house
40x4
Flush
35x4
Straight
30x4
Three of a Kind(Trips)
30x3
Two pairs
20x2
One pair
10x2
High card
5x1

Karten:
2-9 = eigene Punkte
JQK = 10
A = 11
