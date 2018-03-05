package com.oocode;

import org.junit.Test;
import java.math.BigDecimal;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class TodoTests {

    @Test
    public void usesCheaperProviderIfOddsTheSame() throws Exception {
        BetPlacer betPlacer = new BetPlacer();
        BigDecimal bigDecimalSame = new BigDecimal(0.5);
        assertThat(betPlacer.placeBetLogic(bigDecimalSame,bigDecimalSame), equalTo("Swaps"));
    }

    @Test
    public void usesCheaperProviderIfOddsBetter() throws Exception {
        BetPlacer betPlacer = new BetPlacer();
        BigDecimal cheaperSwapOdds = new BigDecimal(0.7);
        BigDecimal expensiveRacingOdds = new BigDecimal(0.5);
        assertThat(betPlacer.placeBetLogic(cheaperSwapOdds,expensiveRacingOdds), equalTo("Swaps"));
    }

    @Test
    public void usesExpensiveProviderIfOddsBetter() throws Exception {
        BetPlacer betPlacer = new BetPlacer();
        BigDecimal cheaperSwapOdds = new BigDecimal(0.5);
        BigDecimal expensiveRacingOdds = new BigDecimal(0.7);
        assertThat(betPlacer.placeBetLogic(cheaperSwapOdds,expensiveRacingOdds), equalTo("Race"));
    }

    @Test
    public void placesExpensiveBetIfTargetOddsNotMetOnCheapBet() throws Exception {
        BetPlacer betPlacer = new BetPlacer();
        BigDecimal targetOdds = new BigDecimal(0.5);
        BigDecimal cheaperSwapOdds = new BigDecimal(0.4);
        BigDecimal expensiveRacingOdds = new BigDecimal(0.7);
        assertThat(betPlacer.placeTargetBet(cheaperSwapOdds, expensiveRacingOdds, targetOdds),equalTo("Race"));
    }

    @Test
    public void placesNoBetIfTargetOddsNotMetOnEither() throws Exception {
        BetPlacer betPlacer = new BetPlacer();
        BigDecimal targetOdds = new BigDecimal(0.5);
        BigDecimal cheaperSwapOdds = new BigDecimal(0.3);
        BigDecimal expensiveRacingOdds = new BigDecimal(0.2);
        assertThat(betPlacer.placeTargetBet(cheaperSwapOdds, expensiveRacingOdds, targetOdds),equalTo("No Bet"));
    }
}
