package com.example.tp2extra;

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
    private int positionPionSelectionne = -1;

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
                            dernierPionSelectionne.setColorFilter(null);
                        }

                        if (dernierPionSelectionne != null && positionPionSelectionne != -1) {
                            Log.d("DEPLACEMENT", "Passer de " + positionPionSelectionne + " à " + positionManoury);
                            if (jeu.deplacerPion(positionPionSelectionne, positionManoury)) {
                                mettreAJourVue(positionPionSelectionne, positionManoury);
                            } else {
                                Log.d("DEPLACEMENT", "Déplacement invalide.");
                            }

                            dernierPionSelectionne.setColorFilter(null);
                            dernierPionSelectionne = null;
                            positionPionSelectionne = -1;
                        }

                        else if (caseContientPion(finalRow, finalCol)) {
                            Pion pionSelectionne = jeu.getDamier().getPion(positionManoury);

                            if (pionSelectionne != null) {
                                ImageView pionView = (ImageView) caseLayout.getChildAt(0);
                                if (jeu.estDeTour(pionSelectionne)) {
                                    pionView.setColorFilter(getColor(R.color.selectionColor));
                                    dernierPionSelectionne = pionView;
                                    positionPionSelectionne = positionManoury;
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

    private void mettreAJourVue(int positionPionSelectionne, int positionManoury) {
        GridLayout gridLayout = findViewById(R.id.myGridLayout);


        FrameLayout caseOrigine = (FrameLayout) gridLayout.getChildAt(positionToIndex(positionPionSelectionne));
        if (caseOrigine != null && caseOrigine.getChildCount() > 0) {
            caseOrigine.removeAllViews();
        }

        FrameLayout caseDestination = (FrameLayout) gridLayout.getChildAt(positionToIndex(positionManoury));
        if (caseDestination != null) {
            Pion pion = jeu.getDamier().getPion(positionManoury);
            if (pion != null) {
                ImageView pionView = new ImageView(this);
                pionView.setImageResource(pion.getCouleur() == Pion.CouleurPion.noir
                        ? R.drawable.pion_noir : R.drawable.pion_blanc);

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT);
                pionView.setLayoutParams(params);
                caseDestination.addView(pionView);
            }
        }
    }

    private int positionToIndex(int positionManoury) {
        int row = (positionManoury - 1) / (10 / 2);
        int col = ((positionManoury - 1) % (10 / 2)) * 2 + (row % 2 == 0 ? 1 : 0);
        return row * 10 + col;
    }

}
