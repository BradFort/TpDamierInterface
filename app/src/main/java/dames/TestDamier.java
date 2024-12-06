package dames;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import cstjean.mobile.dames.damier.Damier;
import cstjean.mobile.dames.damier.Pion;

/**
 * Classe de test pour la classe Damier.
 *
 * @author Bradley Fortin & Antoine Davignon
 */
public class TestDamier {

    private Damier damier;

    @Before
    public void setUp() {
        damier = new Damier();
    }

    /**
     * Teste les fonctionnalités de la classe Damier.
     */
    @Test
    public void testDamier() {
        // Création de deux pions (un par défaut et un noir)
        Pion pion1 = new Pion();
        Pion pion2 = new Pion(Pion.getCouleurs()[1]);

        // Initialisation du damier
        damier.ajouterPion(1, pion1);
        assertEquals(1, damier.nbPions());

        // Ajout d'un autre pion à la position 38
        damier.ajouterPion(38, pion2);
        assertEquals(2, damier.nbPions());

        // Vérification des couleurs des pions
        assertEquals(pion1, damier.getPion(1));
        assertEquals(Pion.getCouleurs()[0], damier.getPion(1).getCouleur());
        assertEquals(Pion.getCouleurs()[1], damier.getPion(38).getCouleur());

        damier.initializer();

        assertEquals("-P-P-P-P-P\n" +
                "P-P-P-P-P-\n" +
                "-P-P-P-P-P\n" +
                "P-P-P-P-P-\n" +
                "----------\n" +
                "----------\n" +
                "-p-p-p-p-p\n" +
                "p-p-p-p-p-\n" +
                "-p-p-p-p-p\n" +
                "p-p-p-p-p-", damier.afficher());

        Damier damierInitial = new Damier();

        damierInitial.initializer();

        assertEquals(40, damierInitial.nbPions());
    }
}
