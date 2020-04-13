package Controller;

import Model.Match;
import Model.Worker;

public class Athena implements God {

    @Override
    public String getName() {
        return "Athena";
    }

    @Override
    public String getDescription() {
        return "Goddess of Wisdom\n" +
                "Opponent’s Turn: If one of your\n" +
                "Workers moved up on your last\n" +
                "turn, opponent Workers cannot\n" +
                "move up this turn.";
    }

    @Override
    public void turn(Match m, Worker w) {

    }
}