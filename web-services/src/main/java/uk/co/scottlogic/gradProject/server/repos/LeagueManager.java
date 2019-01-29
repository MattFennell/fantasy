package uk.co.scottlogic.gradProject.server.repos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.scottlogic.gradProject.server.repos.documents.ApplicationUser;
import uk.co.scottlogic.gradProject.server.repos.documents.League;
import uk.co.scottlogic.gradProject.server.routers.Token;
import uk.co.scottlogic.gradProject.server.routers.dto.LeagueReturnDTO;
import uk.co.scottlogic.gradProject.server.routers.dto.UserInLeagueReturnDTO;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class LeagueManager {

    private static final Logger log = LoggerFactory.getLogger(Token.class);

    private LeagueRepo leagueRepo;

    @Autowired
    public LeagueManager(LeagueRepo leagueRepo) {
        this.leagueRepo = leagueRepo;
    }

    public void createLeague(ApplicationUser owner, String leagueName, Integer startWeek, String codeToJoin) {

        Iterable<League> userLeagues = leagueRepo.findAll();

        List<League> allLeagues = new ArrayList<>();
        userLeagues.forEach(allLeagues::add);
        for (League league : allLeagues) {
            if (league.getLeagueName().equals(leagueName)) {
                throw new IllegalArgumentException("League with that name already exists");
            }
        }

        League league = new League(owner, leagueName, new ArrayList<>(), startWeek, codeToJoin);
        league.addParticipant(owner);
        leagueRepo.save(league);
    }

    // Sorts them by total points (doesn't look at points since week started!!!!)
    // Top points is first
    List<ApplicationUser> findUsersInLeague(League league) {
        List<ApplicationUser> participants = league.getParticipants();
        participants.sort(Comparator.comparing(ApplicationUser::getTotalPoints).reversed());
        return participants;
    }

    public List<UserInLeagueReturnDTO> findUsersInLeagueAndPositions(String leagueName) {
        Optional<League> league = leagueRepo.findByLeagueName(leagueName);
        List<UserInLeagueReturnDTO> usersAndPositions = new ArrayList<>();
        if (league.isPresent()) {
            List<ApplicationUser> usersInLeague = findUsersInLeague(league.get());
            int position = 0;
            for (ApplicationUser u : usersInLeague) {
                position += 1;
                usersAndPositions.add(new UserInLeagueReturnDTO(u, position));
            }
            return usersAndPositions;
        } else {
            throw new IllegalArgumentException("League does not exist with that league name");
        }
    }

    // Need to stop the same player joining a league multiple times
    // Need to make the code for joining obscure
    public LeagueReturnDTO addPlayerToLeague(ApplicationUser user, String code) {
        Optional<League> league = leagueRepo.findByCodeToJoin(code);
        if (league.isPresent()) {
            League l = league.get();
            if (userExistsInLeague(user, l)) {
                log.debug("There is already a user in the league");
                throw new IllegalArgumentException("You are already in that league");
            }
            String leagueCode = l.getCodeToJoin();
            if (leagueCode.equals(code)) {
                l.addParticipant(user);
                leagueRepo.save(l);
                int position = findPositionOfUserInLeague(user, l);
                return new LeagueReturnDTO(l.getLeagueName(), position);
            } else {
                throw new IllegalArgumentException("Invalid code for league");  // Never happens?
            }
        } else {
            throw new IllegalArgumentException("Invalid code for league");
        }
    }

    boolean userExistsInLeague(ApplicationUser user, League league) {

        // Should make this not use the sorting method
        List<ApplicationUser> usersInLeague = findUsersInLeague(league);
        for (ApplicationUser u : usersInLeague) {
            if (u.getId().equals(user.getId())) {
                return true;
            }
        }
        return false;
    }

    Integer findPositionOfUserInLeague(ApplicationUser user, League league) {
        List<ApplicationUser> usersInLeague = findUsersInLeague(league);
        int position = 0;
        for (ApplicationUser u : usersInLeague) {
            position += 1;
            if (u.getId().equals(user.getId())) {
                return position;
            }
        }
        throw new IllegalArgumentException("User not in league?");
    }

    // Change this to binary search
    // SURELY this can be improved
    public List<LeagueReturnDTO> findLeaguesPlayerIsIn(ApplicationUser user) {

        List<LeagueReturnDTO> returnDTOS = new ArrayList<>();
        Iterable<League> userLeagues = leagueRepo.findAll();

        List<League> allLeagues = new ArrayList<>();
        userLeagues.forEach(allLeagues::add);

        for (League l : allLeagues) {
            List<ApplicationUser> participants = findUsersInLeague(l);
            int position = 0;
            for (ApplicationUser u : participants) {
                position += 1;
                if (u.getId().equals(user.getId())) {
                    returnDTOS.add(new LeagueReturnDTO(l.getLeagueName(), position));
                    break;
                }
            }
        }
        return returnDTOS;
    }

}
