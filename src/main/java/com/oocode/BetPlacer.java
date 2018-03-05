package com.oocode;

import com.teamoptimization.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BetPlacer {

    public static void main(String[] args) throws Exception {
        /* Results usually look like a bit like one of the following:
           Time out on SlugSwaps
           accepted quote = 14281567-1fde-4996-a61f-0ba60b2c95c0 with offered odds 0.87
           accepted quote = dada5f35-c244-4da6-a370-648ea35f7a03 with required odds 0.50
        */

        // Note that the names of today’s races change every day! And I have automated this to make it easier to bet and test the code
        Date today = new Date();
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE");
        new BetPlacer().placeBet(3, "The " + simpleDateformat.format(today) + " race", new BigDecimal("0.30"));
    }

    public void placeBet(int slugId, String raceName, BigDecimal targetOdds) {

        Quote racingQuote = racingQuote(slugId, raceName);

        long startTime = System.currentTimeMillis();

        String quoteId = swapsQuote(slugId, raceName, targetOdds);
        Quote swapsQuote = new Quote(targetOdds, quoteId);
        String matchingOddsProvider = placeBetLogic(racingQuote.odds,swapsQuote.odds);
        String targetOddsMet = placeTargetBet(racingQuote.odds,swapsQuote.odds,targetOdds);

        if (matchingOddsProvider=="Swaps" && targetOddsMet=="Swaps") {
            //Ensuring Swaps has enough time to return the results
                while ((startTime + 1000) < System.currentTimeMillis()) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }
                try {
                    swapsAccept(quoteId);
                } catch (SlugSwaps.Timeout timeout) {
                    System.out.println("SlugsSwaps API timed out");
                    //Bet is placed with RacingAPI to ensure that a bet goes through on timeout
                    if (placeTargetBet(new BigDecimal(0),racingQuote.odds,targetOdds)=="Race") {
                        racingAgree(racingQuote.uid);
                    }
                }
        } else if (matchingOddsProvider=="Race" && targetOddsMet=="Race") {
                racingAgree(racingQuote.uid);
        }
    }

    public String placeTargetBet(BigDecimal bigDecimalSwap, BigDecimal bigDecimalRace, BigDecimal targetOdds) {
        if ((bigDecimalRace.compareTo(bigDecimalSwap) == 1) && (bigDecimalRace.compareTo(targetOdds) == 1)) {
            return "Race";
        }  else if ((bigDecimalSwap.compareTo(bigDecimalRace) == 1) && (bigDecimalSwap.compareTo(targetOdds) == 1)) {
            return "Swaps";
        } else {
            return "No Bet";
        }
    }

    public String swapsQuote(int slugId, String raceName, BigDecimal targetOdds) {
        String result;
        Race race = SlugSwapsApi.forRace(raceName);

        if (race == null) {
            result = null;
        } else {
            result = race.quote(slugId, targetOdds);
        }
        return result;
    }

    public boolean racingAgree(String uid) {
        return SlugRacingOddsApi.agree(uid);
    }

    public void swapsAccept(String p2p) throws SlugSwaps.Timeout {
        SlugSwapsApi.accept(p2p);
    }

    public Quote racingQuote(int slugId, String raceName) {
        return SlugRacingOddsApi.on(slugId, raceName);
    }

    public String placeBetLogic (BigDecimal bigDecimalSwap, BigDecimal bigDecimalRace) {
        // Method returns where to place the bet based on the odds received
        int result = bigDecimalSwap.compareTo(bigDecimalRace);

        switch (result) {
            default:
                //defaults to Race
                return "Race";
            case 0:
                // if both are equal
                return "Swaps";
            case 1:
                // if Swaps have better odds
                return "Swaps";
            case 2:
                // if Racing company has better odds
                return "Race";
        }
    }

}
