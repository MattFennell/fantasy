package uk.co.scottlogic.gradProject.server.repos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.scottlogic.gradProject.server.misc.Constants;
import uk.co.scottlogic.gradProject.server.misc.Enums;
import uk.co.scottlogic.gradProject.server.repos.documents.ApplicationUser;
import uk.co.scottlogic.gradProject.server.repos.documents.Player;
import uk.co.scottlogic.gradProject.server.repos.documents.TransferMarketOpen;
import uk.co.scottlogic.gradProject.server.repos.documents.UsersWeeklyTeam;
import uk.co.scottlogic.gradProject.server.routers.dto.PlayerDTO;
import uk.co.scottlogic.gradProject.server.routers.dto.TeamHistoryDTO;

import java.util.*;

@Service
public class WeeklyTeamManager {

    private static final Logger log = LoggerFactory.getLogger(WeeklyTeamManager.class);

    private PlayerRepo playerRepo;
    private ApplicationUserRepo applicationUserRepo;
    private WeeklyTeamRepo weeklyTeamRepo;
    private PlayerManager playerManager;
    private TransferMarketRepo transferMarketRepo;

    @Autowired
    public WeeklyTeamManager(ApplicationUserRepo applicationUserRepo, PlayerRepo playeRepo,
                             WeeklyTeamRepo weeklyTeamRepo, PlayerManager playerManager, TransferMarketRepo transferMarketRepo) {
        this.applicationUserRepo = applicationUserRepo;
        this.playerRepo = playeRepo;
        this.weeklyTeamRepo = weeklyTeamRepo;
        this.playerManager = playerManager;
        this.transferMarketRepo = transferMarketRepo;


        List<TransferMarketOpen> marketOpen = this.transferMarketRepo.findAll();
        if (marketOpen.size() == 0){
            TransferMarketOpen transferMarketOpen = new TransferMarketOpen(true);
            transferMarketRepo.save(transferMarketOpen);
        }
    }

    void addPlayerToWeeklyTeam(ApplicationUser user, String id) {
        Optional<Player> player = playerRepo.findById(UUID.fromString(id));

        if (player.isPresent()) {
            Player p = player.get();
            Optional<UsersWeeklyTeam> teams = weeklyTeamRepo.findActiveTeam(user);
            if (teams.isPresent()) {
                UsersWeeklyTeam team = teams.get();
                team.getPlayers().add(p);
                weeklyTeamRepo.save(team);
            }
        } else {
            throw new IllegalArgumentException("Player does not exist");
        }
    }

    public void setTransferMarket(ApplicationUser user, boolean transferMarket){
        log.info("---------------SETTING TRANSFER MARKET---------------");
        TransferMarketOpen transferMarketOpen = transferMarketRepo.findFirstByOrderByIsOpenAsc();
        transferMarketOpen.setOpen(transferMarket);
        transferMarketRepo.save(transferMarketOpen);
        log.info("Username " + user.getUsername() + " set the transfer market to " + transferMarket);
        log.info("---------------TRANSFER MARKET SET---------------");
    }

    public boolean transferMarketIsOpen(){
        TransferMarketOpen transferMarketOpens = transferMarketRepo.findFirstByOrderByIsOpenAsc();
        return transferMarketOpens.getOpen();
    }

    public void updateAllWeeklyTeams(ApplicationUser requestMaker, int week){
        int maxWeek = weeklyTeamRepo.findNumberOfWeeks();
        System.out.println("Max week = " + maxWeek);
        if (week != maxWeek + 1){
            throw new IllegalArgumentException("The next week should be " + (maxWeek+1) + ", not " + week);
        }
        log.info("---------------IMPORTANT - UPDATING ALL TEAMS---------------");
        Iterable<ApplicationUser> allUsers = applicationUserRepo.findAll();
        List<ApplicationUser> users = new ArrayList<>();
        allUsers.forEach(users::add);
        for (ApplicationUser user : users){
            if (weeklyTeamRepo.findByUserByWeek(user, week).isPresent()){
                throw new IllegalArgumentException("There is already a weekly team for this week");
            }
            Optional<UsersWeeklyTeam> uwt = weeklyTeamRepo.findByUserByWeek(user, -1);
            if (uwt.isPresent()){
                System.out.println("weekly team present for user" + user.getUsername());
                System.out.println("weekly team id = " + uwt.get().getId());
                UsersWeeklyTeam newTeam = new UsersWeeklyTeam(user, new Date(), uwt.get().getPlayers(), week);
                System.out.println("Making a new weekly team for week " + week);
                System.out.println("This weekly team has " + uwt.get().getPlayers().size() + " players");
                weeklyTeamRepo.save(newTeam);
                System.out.println("saved the team");
                Player p = new Player(null, null, 0, "test", "test");

            }
            else {
                System.out.println("no weekly ream");
                throw new IllegalArgumentException("User has no active team");
            }
        }
        log.info("Username " + requestMaker.getUsername() + " triggered week " + week);
        log.info("---------------WEEKLY TEAMS UPDATED---------------");
    }

    public Integer getTotalNumberOfWeeks() {
        return weeklyTeamRepo.findNumberOfWeeks();
    }

    void removePlayerFromWeeklyTeam(ApplicationUser user, String id) {
        Optional<Player> player = playerRepo.findById(UUID.fromString(id));
        if (player.isPresent()) {
            Player p = player.get();
            Optional<UsersWeeklyTeam> teams = weeklyTeamRepo.findActiveTeam(user);
            if (teams.isPresent()) {
                UsersWeeklyTeam team = teams.get();
                team.getPlayers().remove(p);
                weeklyTeamRepo.save(team);
            } else {
                throw new IllegalArgumentException("Player does not have a weekly team");
            }
        } else {
            throw new IllegalArgumentException("Player does not exist");
        }
    }

    public List<UsersWeeklyTeam> findWeeklyTeamWithMostPoints(Integer week) {
        return weeklyTeamRepo.findUserWithMostPoints(week);
    }

    // Given a list of players, check that it is a valid squad
    boolean checkTeamIsValid(List<Player> players) {
        double totalCost = 0;

        int numberOfGoalkeepers = 0;
        int numberOfDefenders = 0;
        int numberOfMidfielders = 0;
        int numberOfAttackers = 0;

        Map<UUID, Integer> numberInEachTeam = new HashMap<>();
        Map<UUID, Integer> playersAdded = new HashMap<>();

        for (Player p : players) {
            totalCost += p.getPrice();

            // TO:DO This will need to change if player prices change
            if (totalCost > Constants.INITIAL_BUDGET) {
                return false;
            }

            if (p.getPosition().equals(Enums.Position.GOALKEEPER)) {
                numberOfGoalkeepers += 1;
            } else if (p.getPosition().equals(Enums.Position.DEFENDER)) {
                numberOfDefenders += 1;
            } else if (p.getPosition().equals(Enums.Position.MIDFIELDER)) {
                numberOfMidfielders += 1;
            } else if (p.getPosition().equals(Enums.Position.ATTACKER)) {
                numberOfAttackers += 1;
            }

            UUID playerId = p.getId();
            UUID teamId = p.getActiveTeam().getId();
            if (playersAdded.containsKey(playerId)) {
                throw new IllegalArgumentException("Can't have the same player twice");
            } else {
                playersAdded.put(playerId, 1);
            }

            if (numberInEachTeam.containsKey(teamId)) {
                if (numberInEachTeam.get(teamId) >= Constants.MAX_PLAYERS_PER_COLLEGE_TEAM) {
                    throw new IllegalArgumentException("Too many players from the same team");
                } else {
                    numberInEachTeam.put(teamId, numberInEachTeam.get(teamId) + 1);
                }
            } else {
                numberInEachTeam.put(teamId, 1);
            }
        }

        if (numberOfGoalkeepers != 1){
            throw new IllegalArgumentException("Must have one goalkeeper");
        }
        if (numberOfDefenders < 3 || numberOfDefenders > 5){
            throw new IllegalArgumentException("Must have between 3 and 5 defenders");
        }
        if (numberOfMidfielders < 3 || numberOfMidfielders > 5){
            throw new IllegalArgumentException("Must have between 3 and 5 midfielders");
        }
        if (numberOfAttackers < 1 || numberOfAttackers > 3){
            throw new IllegalArgumentException("Must have between 1 and 3 attackers");
        }

        if (numberOfAttackers+numberOfMidfielders+numberOfDefenders+numberOfGoalkeepers != 11){
            throw new IllegalArgumentException("Team must have 11 players");
        }
        return true;
    }

    public boolean update(ApplicationUser user, List<PlayerDTO> playersBeingAdded, List<PlayerDTO> playersBeingRemoved) {
        double priceOfAdding = 0;
        double priceOfRemoving = 0;

        TransferMarketOpen transferMarketOpen = transferMarketRepo.findFirstByOrderByIsOpenAsc();

        if (!transferMarketOpen.getOpen()) {
            throw new IllegalArgumentException("Transfer market is closed");
        }
        if (playersBeingAdded.isEmpty()) {
            throw new IllegalArgumentException("Must attempt to transfer at least 1 player");
        }
        Optional<UsersWeeklyTeam> optionalActiveTeam = weeklyTeamRepo.findActiveTeam(user);
        if (!optionalActiveTeam.isPresent()){
            throw new IllegalArgumentException("User has no active team");
        }
        UsersWeeklyTeam activeTeam = optionalActiveTeam.get();

        if (activeTeam.getPlayers().size() + playersBeingAdded.size() - playersBeingRemoved.size() != Constants.MAX_TEAM_SIZE) {
            throw new IllegalArgumentException("Invalid team size");
        }

        ArrayList<Player> players = new ArrayList<>(activeTeam.getPlayers());
        for (PlayerDTO player : playersBeingAdded) {
            Optional<Player> p = playerRepo.findById(UUID.fromString(player.getId()));
            if (p.isPresent()) {
                players.add(p.get());
                priceOfAdding += p.get().getPrice();
            } else {
                throw new IllegalArgumentException("Player being added does not exist");
            }
        }
        for (PlayerDTO player : playersBeingRemoved) {
            Optional<Player> p = playerRepo.findById(UUID.fromString(player.getId()));
            if (p.isPresent()) {
                players.remove(p.get());
                priceOfRemoving += p.get().getPrice();
            } else {
                throw new IllegalArgumentException("Player being removed does not exist");
            }
        }
        // Only save if the transfer is valid
        if (checkTeamIsValid(players)) {
            user.setRemainingBudget(user.getRemainingBudget()-priceOfAdding+priceOfRemoving);
            applicationUserRepo.save(user);
            activeTeam.setPlayers(players);
            weeklyTeamRepo.save(activeTeam);
            return true;
        } else {
            throw new IllegalArgumentException("Invalid team");
        }
    }

    public double findAveragePointsOfAllTeamsInWeek(Integer week) {
        return weeklyTeamRepo.findAveragePointsInWeek(week);
    }

    // Returns the list sorted by Goalkeeper - Defenders - Midfielder - Attacker
    public List<PlayerDTO> findAllPlayersInWeeklyTeam(String id, Integer week) {
        Optional<ApplicationUser> user = applicationUserRepo.findById(UUID.fromString(id));
        if (user.isPresent()) {
            Optional<UsersWeeklyTeam> team = weeklyTeamRepo.findByUserByWeek(user.get(), week);
            List<PlayerDTO> playersToReturn = new ArrayList<>();

            if (team.isPresent()) {
                List<Player> players = team.get().getPlayers();
                for (Player p : players) {
                    playersToReturn.add(new PlayerDTO(p, playerManager.findPointsForPlayerInWeek(p, week)));
                }
            } else {
                throw new IllegalArgumentException("No weekly team for that user and week");
            }
            playersToReturn.sort(Comparator.comparing(PlayerDTO::getPosition));
            return playersToReturn;
        }
        else {
            throw new IllegalArgumentException("User does not exist");
        }
    }
}
