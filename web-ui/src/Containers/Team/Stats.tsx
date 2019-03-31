import { connect } from 'react-redux';
import { State } from '../../Reducers/root';
import Stats from '../../Components/Team/Stats';
import {
	getAverageWeeklyPoints,
	getWeekBeingViewed,
	getTopWeeklyPlayer,
	getTopWeeklyUser,
	getRemainingBudget,
	getMostValuable,
	getTotalNumberOfWeeks
} from '../../Selectors/StatsSelector';
import { getUserBeingViewed } from '../../Selectors/ActiveTeamSelector';
import { setUserBeingViewed } from '../../Actions/ActiveTeamActions';
import { setBudget, setMostValuable } from '../../Actions/StatsActions';

const mapStateToProps = (state: State) => ({
	remainingBudget: getRemainingBudget(state),
	averageWeeklyPoints: getAverageWeeklyPoints(state),
	weekBeingViewed: getWeekBeingViewed(state),
	topWeeklyPlayer: getTopWeeklyPlayer(state),
	topWeeklyUsers: getTopWeeklyUser(state),
	userBeingViewed: getUserBeingViewed(state),
	mostValuable: getMostValuable(state),
	totalNumberOfWeeks: getTotalNumberOfWeeks(state)
});

const mapDispatchToProps = {
	setBudget,
	setMostValuable,
	setUserBeingViewed
};

export default connect<any, any, any>(
  mapStateToProps as any,
  mapDispatchToProps
)(Stats);
