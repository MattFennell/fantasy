import { connect } from 'react-redux';
import { State } from '../../Reducers/root';
import Captain from '../../Components/Captain/Captain';
import { setPlayersInFilteredTeam, setCaptainPageBeingViewed } from '../../Actions/AdminActions';
import { getCaptainPageBeingViewed, getPlayersInFilteredTeam } from '../../Selectors/AdminSelector';
import { setPageBeingViewed } from '../../Actions/AccountActions';
import { getTotalNumberOfWeeks } from '../../Selectors/StatsSelector';

const mapStateToProps = (state: State) => ({
	captainPageBeingViewed: getCaptainPageBeingViewed(state),
	playersInFilteredTeam: getPlayersInFilteredTeam(state),
	totalNumberOfWeeks: getTotalNumberOfWeeks(state),
});

const mapDispatchToProps = {
	setCaptainPageBeingViewed,
	setPlayersInFilteredTeam,
	setPageBeingViewed
};

export default connect<any, any, any>(
  mapStateToProps as any,
  mapDispatchToProps
)(Captain);
