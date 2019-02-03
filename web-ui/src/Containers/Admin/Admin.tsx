import { connect } from 'react-redux';
import { State } from '../../Reducers/root';
import Admin from '../../Components/Admin/Admin';
import { setAdminPageBeingViewed } from '../../Actions/AdminActions';

const mapStateToProps = (state: State) => ({});

const mapDispatchToProps = {
  setAdminPageBeingViewed
};

export default connect<any, any, any>(
  mapStateToProps as any,
  mapDispatchToProps
)(Admin);
