package uk.co.scottlogic.gradProject.server.repos;

import net.bytebuddy.pool.TypePool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.scottlogic.gradProject.server.misc.Enums;
import uk.co.scottlogic.gradProject.server.repos.documents.CollegeTeam;
import uk.co.scottlogic.gradProject.server.repos.documents.Player;
import uk.co.scottlogic.gradProject.server.repos.documents.UsersWeeklyTeam;
import uk.co.scottlogic.gradProject.server.routers.dto.CollegeTeamDTO;
import uk.co.scottlogic.gradProject.server.routers.dto.CollegeTeamStatsDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CollegeTeamManager {

    private CollegeTeamRepo teamRepo;
    private PlayerRepo playerRepo;

    @Autowired
    public CollegeTeamManager(CollegeTeamRepo teamRepo, PlayerRepo playerRepo) {
        this.teamRepo = teamRepo;
        this.playerRepo = playerRepo;
//        makeTeam("A", 10, 0, 1, 50, 3);
//        makeTeam("B", 8, 2,1, 45,5);
//        makeTeam("C", 6, 5, 4, 35, 15);
//        makeTeam("D", 4, 4, 3, 30, 20);
//        makeTeam("E", 2, 7, 2, 25, 25);

        getAllCollegeTeams("points");
        getAllCollegeTeams("not points");
    }


    public void makeTeam(String name) {
        CollegeTeam team = new CollegeTeam(name);
        teamRepo.save(team);
    }

    public void deleteTeam(String name){
        Optional<CollegeTeam> collegeTeam = teamRepo.findByName(name);

        if (collegeTeam.isPresent()){
            List<Player> players = playerRepo.findByCollegeTeam(collegeTeam.get());

            if (!players.isEmpty()){
                throw new IllegalArgumentException("Can't delete a college team that has players associated with it");
            }
            teamRepo.delete(collegeTeam.get());
        }
        else {
            throw new IllegalArgumentException("No college team with that name exists");
        }
    }

    public void addStatsToCollegeTeam(CollegeTeamStatsDTO dto){
        Optional<CollegeTeam> collegeTeam = teamRepo.findByName(dto.getCollegeName());
        if (collegeTeam.isPresent()){
            collegeTeam.get().addGoalsFor(dto.getGoalsFor());
            collegeTeam.get().addGoalsAgainst(dto.getGoalsAgainst());

            if (dto.getResult().equals(Enums.COLLEGE_MATCH_RESULT.WIN)){
                collegeTeam.get().addWin();
            }
            else if (dto.getResult().equals(Enums.COLLEGE_MATCH_RESULT.DRAW)){
                collegeTeam.get().addDraw();
            }
            else if (dto.getResult().equals(Enums.COLLEGE_MATCH_RESULT.LOSS)){
                collegeTeam.get().addLoss();
            }

            teamRepo.save(collegeTeam.get());
        }
        else {
            throw new IllegalArgumentException("College team does not exist");
        }
    }

    public void editCollegeTeamStats(CollegeTeamStatsDTO dto){
        Optional<CollegeTeam> collegeTeam = teamRepo.findByName(dto.getCollegeName());
        if (collegeTeam.isPresent()){
            collegeTeam.get().setGoalsFor(dto.getGoalsFor());
            collegeTeam.get().setGoalsAgainst(dto.getGoalsAgainst());

            collegeTeam.get().setWins(dto.getWins());
            collegeTeam.get().setDraws(dto.getDraws());
            collegeTeam.get().setLosses(dto.getLosses());

            teamRepo.save(collegeTeam.get());
        }
        else {
            throw new IllegalArgumentException("College team does not exist");
        }
    }

    // Sorts by points first
    // Then goal difference
    // Then goals for
    // Then reverse alphabetically
    private int compareByPoints(CollegeTeamDTO team_one, CollegeTeamDTO team_two){
        if (team_one.getTotalScore() > team_two.getTotalScore()){
            return -1;
        }
        else if (team_one.getTotalScore() < team_two.getTotalScore()){
            return 1;
        }
        else if (team_one.getGoalsFor()-team_one.getGoalsAgainst() > team_two.getGoalsFor() - team_two.getGoalsAgainst()){
            return -1;
        }
        else if (team_one.getGoalsFor()-team_one.getGoalsAgainst() < team_two.getGoalsFor()-team_two.getGoalsAgainst()){
            return 1;
        }
        else if (team_one.getGoalsFor() > team_two.getGoalsFor()){
            return -1;
        }
        else if (team_one.getGoalsFor() < team_two.getGoalsFor()){
            return 1;
        }
        else if (team_one.getName().compareTo(team_two.getName()) > 0){
            return -1;
        }
        else {
            return 1;
        }
    }

    // Sorts by points first
    // Then goal difference
    // Then goals for
    // Then reverse alphabetically
    private int compareByAlphabet(CollegeTeamDTO teamOne, CollegeTeamDTO teamTwo){
        if (teamOne.getName().compareTo(teamTwo.getName()) > 0){
            return 1;
        }
        else {
            return -1;
        }
    }

    public List<CollegeTeamDTO> getAllCollegeTeams(String sortBy){
        Iterable<CollegeTeam> teams = teamRepo.findAll();
        List<CollegeTeam> allCollegeTeams = new ArrayList<>();
        teams.forEach(allCollegeTeams::add);
        List<CollegeTeamDTO> teamsOrderedByPoints = new ArrayList<>();
        for (CollegeTeam ct : allCollegeTeams){
            teamsOrderedByPoints.add(new CollegeTeamDTO(ct));
        }

        if (sortBy.equals("points")) {
            teamsOrderedByPoints.sort(this::compareByPoints);
        }
        else {
            teamsOrderedByPoints.sort(this::compareByAlphabet);
        }

        return teamsOrderedByPoints;
    }
}
