import * as React from 'react';
import { Button } from 'reactstrap';
import CollegeTeam from '../../Containers/Admin/AddPointsCollegeTeam';
import SelectPlayer from '../../Containers/Admin/SelectPlayer';
import { PlayerDTO } from '../../Models/Interfaces/Player';
import { AddPoints } from '../../Models/Interfaces/AddPoints';
import { addPlayerPoints } from '../../Services/Player/PlayerService';
import '../../Style/Admin/ErrorMessage.css';
import CustomDropdown from '../common/CustomDropdown';
import TextInputForm from '../common/TexInputForm';
import ResponseMessage from '../common/ResponseMessage';

interface AddPointsFormProps {
  setTeamAddingPoints: (team: string) => void;
  teamAddingPoints: string;
  playersInFilteredTeam: PlayerDTO[];
}

interface AddPointsFormState {
  goals: string;
  assists: string;
  minutesPlayed: string;
  manOfTheMatch: boolean;
  yellowCards: string;
  cleanSheet: boolean;
  redCard: boolean;
  playerID: string;
  week: string;
  viewingDefender: boolean;

  responseMessage: string;
  isError: boolean;
}

class AddPointsForm extends React.Component<AddPointsFormProps, AddPointsFormState> {
	constructor (props: AddPointsFormProps) {
		super(props);
		this._handleGoals = this._handleGoals.bind(this);
		this._handleAssists = this._handleAssists.bind(this);
		this._handleMinutesPlayed = this._handleMinutesPlayed.bind(this);
		this._handleManOfTheMatch = this._handleManOfTheMatch.bind(this);
		this._handleYellowCards = this._handleYellowCards.bind(this);
		this._handleCleanSheet = this._handleCleanSheet.bind(this);
		this._handleRedCard = this._handleRedCard.bind(this);
		this._handlePlayerID = this._handlePlayerID.bind(this);
		this._handleWeek = this._handleWeek.bind(this);
		this._handleCollegeTeam = this._handleCollegeTeam.bind(this);
		this._onSubmit = this._onSubmit.bind(this);
		this.handleOnValidate = this.handleOnValidate.bind(this);
		this.state = {
			goals: '',
			assists: '',
			minutesPlayed: '',
			manOfTheMatch: false,
			yellowCards: '0',
			cleanSheet: false,
			redCard: false,
			playerID: '',
			week: '',
			viewingDefender: true,
			responseMessage: '',
			isError: false
		};
	}

	_handleGoals (goals: string) {
		this.setState({ goals });
	}

	_handlePlayerID (playerID: string) {
		this.setState({ playerID });
		let haveSet: boolean = false;
		const { playersInFilteredTeam } = this.props;

		for (let x = 0; x < playersInFilteredTeam.length; x++) {
			if (playerID === playersInFilteredTeam[x].id) {
				if (
					playersInFilteredTeam[x].position === 'DEFENDER' ||
          			playersInFilteredTeam[x].position === 'GOALKEEPER'
				) {
					haveSet = true;
					this.setState({ viewingDefender: true });
				}
			}
		}
		if (!haveSet) {
			this.setState({ viewingDefender: false });
			this.setState({ cleanSheet: false });
		}
	}

	_handleCollegeTeam (team: string) {
		this.props.setTeamAddingPoints(team);
	}

	_handleAssists (assists: string) {
		this.setState({ assists });
	}

	_handleMinutesPlayed (minutesPlayed: string) {
		this.setState({ minutesPlayed });
	}

	_handleManOfTheMatch (manOfTheMatch: boolean) {
		this.setState({ manOfTheMatch });
	}

	_handleYellowCards (yellowCards: string) {
		this.setState({ yellowCards });
	}

	_handleCleanSheet (cleanSheet: boolean) {
		this.setState({ cleanSheet });
	}

	_handleRedCard (redCard: boolean) {
		this.setState({ redCard });
	}

	_handleWeek (week: string) {
		this.setState({ week });
	}

	handleOnValidate () {
		let error: boolean = false;
		let message: string = 'Please select a value for : ';

		const { playerID, week, goals, assists, minutesPlayed } = this.state;

		if (playerID === '') {
			error = true;
			message += 'Player, ';
		}
		if (week === '' || isNaN(parseFloat(week))) {
			error = true;
			message += 'Week, ';
		}
		if (goals === '' || isNaN(parseFloat(goals))) {
			error = true;
			message += 'Goals, ';
		}
		if (assists === '' || isNaN(parseFloat(assists))) {
			error = true;
			message += 'Assists, ';
		}
		if (minutesPlayed === '' || isNaN(parseFloat(minutesPlayed))) {
			error = true;
			message += 'Minutes Played, ';
		}

		if (error) {
			this.setState({ responseMessage: message.substring(0, message.length - 2), isError: true });
		} else {
			this._onSubmit();
		}
	}

	_onSubmit () {
		const { goals, assists, minutesPlayed, manOfTheMatch, yellowCards, cleanSheet, redCard, playerID, week } = this.state;

		let data: AddPoints = {
			goals: goals,
			assists: assists,
			minutesPlayed: minutesPlayed,
			manOfTheMatch: manOfTheMatch,
			yellowCards: yellowCards,
			cleanSheet: cleanSheet,
			redCard: redCard,
			playerID: playerID,
			week: week
		};

		addPlayerPoints(data)
			.then(response => {
				this.setState({ isError: false, responseMessage: 'Points added to player successfully' });
			})
			.catch(error => {
				this.setState({ isError: true, responseMessage: error });
			});
	}

	render () {
		let setTeam = this._handleCollegeTeam;
		let setPlayerID = this._handlePlayerID;

		const { viewingDefender, week, goals, assists, minutesPlayed } = this.state;

		return (
			<div className="admin-form">
				<div className="admin-form-row-one">
					<div className="admin-wrapper">
						<CollegeTeam setTeam={setTeam} />
					</div>
					<div className="admin-wrapper">
						<SelectPlayer setPlayerID={setPlayerID} />
					</div>
					<div className="admin-wrapper">
						<TextInputForm
							currentValue={week}
							setValue={this._handleWeek}
							title="Week"
						/>
					</div>
				</div>
				<div className="admin-form-row-two">
					<div className="admin-wrapper">
						<TextInputForm
							currentValue={goals}
							setValue={this._handleGoals}
							title="Goals"
						/>
					</div>
					<div className="admin-wrapper">
						<TextInputForm
							currentValue={assists}
							setValue={this._handleAssists}
							title="Assists"
						/>
					</div>
					<div className="admin-wrapper">
						<TextInputForm
							currentValue={minutesPlayed}
							setValue={this._handleMinutesPlayed}
							title="Minutes played"
						/>
					</div>
					<div className="admin-wrapper">
						<CustomDropdown
							setData={this._handleYellowCards}
							title="Yellow Cards"
							values={['0', '1', '2']}
						/>
					</div>
					<div className="admin-wrapper">
						<CustomDropdown
							setData={this._handleManOfTheMatch}
							title="Man of the Match"
							values={['No', 'Yes']}
						/>
					</div>
					<div className="admin-wrapper">
						<CustomDropdown
							setData={this._handleRedCard}
							title="Red Card"
							values={['No', 'Yes']}
						/>
					</div>

					{viewingDefender ? (
						<div className="admin-wrapper">
							<CustomDropdown
								setData={this._handleCleanSheet}
								title="Clean Sheet"
								values={['No', 'Yes']}
							/> </div>
					) : null}
				</div>
				<div>
					<Button
						className="btn btn-default btn-round-lg btn-lg second"
						id="btnAddPoints"
						onClick={this.handleOnValidate}
					>
            Add Points
					</Button>

					<ResponseMessage
						isError={this.state.isError}
						responseMessage={this.state.responseMessage}
						shouldDisplay={this.state.responseMessage.length > 0}
					/>
				</div>

			</div>
		);
	}
}
export default AddPointsForm;
