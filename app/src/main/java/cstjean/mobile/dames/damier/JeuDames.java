package cstjean.mobile.dames.damier;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Classe représentant le jeu de dames.
 * Gère l'état du jeu, le tour des joueurs et les règles du jeu.
 */
public class JeuDames {
    /**
     * Le damier du jeu, contenant les pions et dames en jeu.
     */
    
    private final Damier damier;

    /**
     * Liste enregistrant l'historique des déplacements effectués dans la partie.
     */
    private Stack<int[]> historiqueActions = new Stack<>();

    /**
     * Indicateur de tour du joueur : 0 pour le joueur 1, 1 pour le joueur 2.
     */
    private int tour;

    /**
     * Constructeur de la classe JeuDames.
     * Initialise le damier, place les pions au départ, et attribue le tour au joueur 1.
     */
    public JeuDames() {
        damier = new Damier();
        damier.initializer();
        tour = 0;
       historiqueActions = new Stack<>();
    }

    /**
     * Calcule la position intermédiaire d'un pion lors d'un mouvement de capture.
     *
     * @param positionActuelle La position actuelle du pion.
     * @param delta            La différence de position entre la case de départ et celle d'arrivée.
     * @return La position intermédiaire du pion.
     */
    public int getPositionIntermediaire(int positionActuelle, int delta) {
        int positionIntermediaire = 0;
        int colonneActuelle = positionActuelle % 10;  // Extrait la colonne (sur un plateau de 10 cases par ligne)

        if (delta == 9) {
            positionIntermediaire = (colonneActuelle >= 1 && colonneActuelle <= 5) ? positionActuelle + 5
                    : positionActuelle + 4;
        } else if (delta == 11) {
            // Vérifie si la position actuelle permet un mouvement valide de -11 (vers le bas-gauche)
            // Si le pion est dans les colonnes 1 à 5, on peut se déplacer directement à gauche
            positionIntermediaire = (colonneActuelle >= 1 && colonneActuelle <= 5) ? positionActuelle + 6
                    : positionActuelle + 5;  // Ajuste le calcul pour une colonne plus à droite
        } else if (delta == -9) {
            positionIntermediaire = (colonneActuelle >= 1 && colonneActuelle <= 5) ? positionActuelle - 4
                    : positionActuelle - 5;
        } else if (delta == -11) {
            // Si le pion est dans les colonnes 2 à 6, il peut se déplacer à gauche
            positionIntermediaire = (colonneActuelle >= 2 && colonneActuelle <= 6) ? positionActuelle - 5
                    : positionActuelle - 6;  // Ajuste le calcul pour la colonne plus à gauche
        }
        return positionIntermediaire;
    }


    /**
     * Déplace un pion d'une position actuelle vers une position souhaitée.
     *
     * @param positionActuelle  La position actuelle du pion.
     * @param positionSouhaitee La position où déplacer le pion.
     * @return true si le déplacement est réussi, false sinon.
     */
    public boolean deplacerPion(int positionActuelle, int positionSouhaitee) {
        Pion pion = damier.getPion(positionActuelle);

        if (!estDeTour(pion)) {
            System.out.println("Coup invalide :mauvais joueur.");
            Log.d("DEPLACERPION","Coup invalide :mauvais joueur.");
            return false;
        }


        if(pion == null)
        {
            System.out.println("Coup invalide : pas de pion.");
            Log.d("DEPLACERPION","Coup invalide : pas de pion.");
            return false; // Coup invalide
        }

        if (pion instanceof Dame) {
            if (!deplacementValideDame(positionActuelle, positionSouhaitee)) {
                System.out.println("Déplacement invalide pour la dame.");
                return false; // Mouvement invalide pour la dame
            }
        } else {
            // Sinon, vérifier si le déplacement est valide pour un pion normal
            if (!deplacementValide(positionActuelle, positionSouhaitee)) {
                System.out.println("Déplacement invalide pour un pion normal.");
                return false; // Mouvement invalide pour un pion normal
            }
        }

        // Effectuer le déplacement
        damier.enleverPion(positionActuelle);
        damier.ajouterPion(positionSouhaitee, pion);

        // Vérifier si le pion doit se transformer en dame
        if ((pion.getCouleur() == Pion.CouleurPion.blanc && positionSouhaitee <= 5) ||
                (pion.getCouleur() == Pion.CouleurPion.noir && positionSouhaitee >= 46)) {
            damier.enleverPion(positionSouhaitee);
            damier.ajouterPion(positionSouhaitee, new Dame(pion.getCouleur()));
            System.out.println("Le pion à la position " + positionSouhaitee + " a été promu en dame !");
        }
        historiqueActions.push(new int[] {positionActuelle, positionSouhaitee});

        // Changer le tour du joueur
        changerTour();

        return true; // Déplacement réussi
    }



    /**
     * Vérifie si le déplacement d'un pion est valide.
     *
     * @param positionDepart  La position de départ du pion.
     * @param positionArrivee La position d'arrivée du pion.
     * @return true si le déplacement est valide, false sinon.
     */
    public boolean deplacementValide(int positionDepart, int positionArrivee) {
        int delta = positionArrivee - positionDepart;

        // Récupérer le pion à déplacer
        Pion pion = damier.getPion(positionDepart);
        if (pion == null) {
            Log.d("ERREUR", "Aucun pion à déplacer à cette position.");
            return false; // Pas de pion à cette position
        }

        // Vérifier que la case d'arrivée est vide
        if (damier.getPion(positionArrivee) != null) {
            Log.d("ERREUR", "La case d'arrivée n'est pas vide.");
            return false;
        }

        // Déplacements valides en fonction de la couleur du pion
        if (pion.getCouleur() == Pion.CouleurPion.blanc) {
            // Pion blanc se déplace "vers le haut" (diagonales négatives)
            return delta == -4 || delta == -5 || delta == -11 || delta == -6;
        } else if (pion.getCouleur() == Pion.CouleurPion.noir) {
            // Pion noir se déplace "vers le bas" (diagonales positives)
            return delta == 4 || delta == 5 || delta == 11 || delta == 6;
        }

        return false; // Mouvement non valide
    }


    /**
     * Vérifie si le déplacement d'une dame est valide.
     *
     * @param positionDepart  La position de départ de la dame.
     * @param positionArrivee La position d'arrivée de la dame.
     * @return true si le déplacement est valide, false sinon.
     */
    public boolean deplacementValideDame(int positionDepart, int positionArrivee) {
        // Vérifier si la case d'arrivée est vide
        if (damier.getPion(positionArrivee) != null) {
            return false; // La case d'arrivée n'est pas vide
        }

        // Vérifier que le déplacement est en diagonale
        int delta = Math.abs(positionArrivee - positionDepart);

        // Vérifier si le déplacement est en diagonale
        return (delta % 9 == 0) || (delta % 11 == 0); // Déplacement valide
    }

    /**
     * Effectue la capture d'un pion adverse.
     *
     * @param positionActuelle  La position actuelle du pion.
     * @param positionSouhaitee La position où déplacer le pion après la capture.
     * @return true si la capture est réussie, false sinon.
     */
    public boolean capturerPion(int positionActuelle, int positionSouhaitee) {
        Pion pion = damier.getPion(positionActuelle);
        Log.d("PION", "Selection du pion :" + pion);

        if (pion == null || !estDeTour(pion)) {
            System.out.println("Capture invalide : pas de pion ou mauvais joueur.");
            return false; // Capture invalide
        }

        int delta = positionSouhaitee - positionActuelle;
        if (delta != 9 && delta != 11 && delta != -9 && delta != -11) {
            return false; // Capture non valide selon les règles de la notation Manoury
        }

        int positionIntermediaire = getPositionIntermediaire(positionActuelle, delta);

        System.out.println("Position pion à capturer : " + positionIntermediaire);
        Pion pionAdverse = damier.getPion(positionIntermediaire);

        if (pionAdverse == null || pionAdverse.getCouleur() == pion.getCouleur()) {
            return false; // Pas de pion adverse à capturer
        }

        // Enlève le pion adverse de la case intermédiaire et déplace le pion
        damier.enleverPion(positionActuelle);
        damier.enleverPion(positionIntermediaire);
        damier.ajouterPion(positionSouhaitee, pion);

        historiqueActions.push(new int[] {positionActuelle, positionSouhaitee});

            changerTour();

        return true; // Capture réussie
    }


    /**
     * Change le tour du joueur.
     */
    public void changerTour() {
        tour = 1 - tour; // Alterne entre 0 et 1
    }

    /**
     * Vérifie si le pion est du joueur dont c'est le tour.
     *
     * @param pion Le pion à vérifier.
     * @return true si c'est le bon joueur, false sinon.
     */
    public boolean estDeTour(Pion pion) {
        if (pion == null) {
            return false; // Si le pion est null, retournez false pour éviter l'exception
        }
        return (tour == 0 && pion.getCouleur() == Pion.CouleurPion.blanc) ||
                (tour == 1 && pion.getCouleur() == Pion.CouleurPion.noir);
    }

    /**
     * Vérifie si la partie est terminée.
     *
     * @return true si la partie est terminée, false sinon.
     */
    public boolean estFinDePartie() {
        return damier.nbPions() == 0;
    }

    /**
     * Promeut les pions en dames lorsqu'ils atteignent les rangées de promotion.
     *
     * @param damier Le damier contenant les pions et dames.
     */
    public void pionDevientDame(Damier damier) {
        for (int i = 1; i <= 50; i++) {  // Parcourt toutes les positions valides du damier
            Pion pion = damier.getPion(i);

            // Vérifie si le pion est blanc et est à une position de promotion
            if (pion != null && pion.getCouleur() == Pion.CouleurPion.blanc && i <= 5) {
                damier.enleverPion(i); // Enlève le pion de sa position actuelle
                damier.ajouterPion(i, new Dame(pion.getCouleur())); // Ajoute la dame à la même position
            }

            // Vérifie si le pion est noir et est à une position de promotion
            if (pion != null && pion.getCouleur() == Pion.CouleurPion.noir && i >= 46) {
                damier.enleverPion(i); // Enlève le pion de sa position actuelle
                damier.ajouterPion(i, new Dame(pion.getCouleur())); // Ajoute la dame à la même position
            }
        }
    }

    /**
     * Retourne l'historique des déplacements effectués dans la partie.
     *
     * @return Liste des déplacements en tant que chaînes de caractères.
     */
    public Stack<int[]> getHistoriqueActions()
    {
        return historiqueActions;
    }

    /**
     * Retourne le numéro du joueur dont c'est le tour.
     *
     * @return L'entier représentant le tour du joueur (0 pour joueur 1, 1 pour joueur 2).
     */
    public int getTour() {
        return tour;
    }

    /**
     * Retourne l'instance du damier du jeu.
     *
     * @return Le damier du jeu de dames.
     */
    public Damier getDamier() {
        return damier;
    }

    public void setTour(int tour) {
        this.tour = tour;
    }
}
