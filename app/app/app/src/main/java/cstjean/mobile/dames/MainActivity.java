package cstjean.mobile.dames;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import cstjean.mobile.dames.damier.JeuDames;
import cstjean.mobile.dames.damier.Pion;

public class MainActivity extends AppCompatActivity {

    private final JeuDames jeu = new JeuDames();
    private ImageView dernierPionSelectionne = null;
    private int positionPionSelectionne = -1;  // Ajout d'une variable pour stocker la position du pion sélectionné

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridLayout gridLayout = findViewById(R.id.myGridLayout);
        int boardSize = 10;
        gridLayout.setRowCount(boardSize);
        gridLayout.setColumnCount(boardSize);

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                FrameLayout caseLayout = new FrameLayout(this);

                if ((row + col) % 2 == 0) {
                    caseLayout.setBackgroundColor(getColor(R.color.white));
                } else {
                    caseLayout.setBackgroundColor(getColor(R.color.black));
                }

                int positionManoury = calculerPositionManoury(row, col);

                int finalRow = row;
                int finalCol = col;
                caseLayout.setOnClickListener(v -> {
                    if (positionManoury != -1) {
                        Log.d("CASE_CLIQUE", "Case cliquée: Position Manoury = " + positionManoury);

                        if (dernierPionSelectionne != null) {
                            dernierPionSelectionne.setColorFilter(null); // Désélectionner le pion précédent
                        }

                        // Si un pion est sélectionné
                        if (dernierPionSelectionne != null && positionPionSelectionne != -1) {
                            Log.d("DEPLACEMENT", "Passer de " + positionPionSelectionne + " à " + positionManoury);
                            if (jeu.deplacerPion(positionPionSelectionne, positionManoury)) {
                                // Si le déplacement est valide, mettez à jour la vue
                                mettreAJourVue();
                            } else {
                                Log.d("DEPLACEMENT", "Déplacement invalide.");
                            }

                            dernierPionSelectionne.setColorFilter(null);
                            dernierPionSelectionne = null;
                            positionPionSelectionne = -1;  // Réinitialiser la position du pion sélectionné
                        }
                        // Si la case contient un pion de l'utilisateur
                        else if (caseContientPion(finalRow, finalCol)) {
                            Pion pionSelectionne = jeu.getDamier().getPion(positionManoury);

                            if (pionSelectionne != null) {
                                ImageView pionView = (ImageView) caseLayout.getChildAt(0);
                                if (jeu.estDeTour(pionSelectionne)) {
                                    pionView.setColorFilter(getColor(R.color.selectionColor));
                                    dernierPionSelectionne = pionView;
                                    positionPionSelectionne = positionManoury;  // Mémoriser la position du pion sélectionné
                                } else {
                                    Log.d("CASE_CLIQUE", "C'est pas votre tour !");
                                }
                            }
                        }
                    } else {
                        Log.d("CASE_CLIQUE", "Case blanche cliquée, non valide.");
                        if (dernierPionSelectionne != null) {
                            dernierPionSelectionne.setColorFilter(null);
                            dernierPionSelectionne = null;
                        }
                    }
                });

                Pion pion = (positionManoury != -1) ? jeu.getDamier().getPion(positionManoury) : null;
                if (pion != null) {
                    ImageView pionView = new ImageView(this);
                    pionView.setImageResource(pion.getCouleur() == Pion.CouleurPion.noir
                            ? R.drawable.pion_noir : R.drawable.pion_blanc);

                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT);
                    pionView.setLayoutParams(params);
                    caseLayout.addView(pionView);
                }

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 100;
                params.height = 100;
                caseLayout.setLayoutParams(params);
                gridLayout.addView(caseLayout);
            }
        }
    }

    private int calculerPositionManoury(int row, int col) {
        if ((row + col) % 2 != 0) {
            return (row * (10 / 2)) + (col / 2) + 1;
        }
        return -1;
    }

    private boolean caseContientPion(int row, int col) {
        int positionManoury = calculerPositionManoury(row, col);
        Pion pion = (positionManoury != -1) ? jeu.getDamier().getPion(positionManoury) : null;
        return pion != null;
    }

    private void mettreAJourVue() {
        GridLayout gridLayout = findViewById(R.id.myGridLayout);
        gridLayout.removeAllViews();

        int boardSize = 10;
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                FrameLayout caseLayout = new FrameLayout(this);

                if ((row + col) % 2 == 0) {
                    caseLayout.setBackgroundColor(getColor(R.color.white));
                } else {
                    caseLayout.setBackgroundColor(getColor(R.color.black));
                }

                int positionManoury = calculerPositionManoury(row, col);

                Pion pion = (positionManoury != -1) ? jeu.getDamier().getPion(positionManoury) : null;
                if (pion != null) {
                    ImageView pionView = new ImageView(this);
                    pionView.setImageResource(pion.getCouleur() == Pion.CouleurPion.noir
                            ? R.drawable.pion_noir : R.drawable.pion_blanc);

                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT);
                    pionView.setLayoutParams(params);
                    caseLayout.addView(pionView);
                }

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 100;
                params.height = 100;
                caseLayout.setLayoutParams(params);
                gridLayout.addView(caseLayout);
            }
        }
    }
}
