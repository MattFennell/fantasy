package uk.co.scottlogic.gradProject.server.misc;

public class Constants {

    private Constants(){

    }

    public static final Integer MAX_TEAM_SIZE = 11;
    public static final Integer MAX_PLAYERS_PER_COLLEGE_TEAM = 11;

    public static final Integer MAX_GOALKEEPERS = 2;
    public static final Integer MAX_DEFENDERS = 5;
    public static final Integer MAX_MIDFIELDERS = 5;
    public static final Integer MAX_ATTACKERS = 3;

    public static final Integer POINTS_PER_CLEAN_SHEET = 4;
    public static final Integer POINTS_PER_DEFENDER_GOAL = 6;
    public static final Integer POINTS_PER_MIDFIELDER_GOAL = 5;
    public static final Integer POINTS_PER_ATTACKER_GOAL = 4;

    public static final Integer POINTS_PER_ASSIST = 3;
    public static final Integer POINTS_PER_RED_CARD = -5;
    public static final Integer POINTS_PER_YELLOW_CARD = -2;
    public static final Integer MAN_OF_THE_MATCH_BONUS = 3;

    public static final Integer INITIAL_BUDGET = 100;
    public static final boolean TRANSFER_MARKET_OPEN = true;

}
