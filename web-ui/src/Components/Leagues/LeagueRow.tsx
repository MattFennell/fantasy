import * as React from 'react';
import { LeaguePositions } from '../../Models/Interfaces/LeaguePositions';

interface LeagueRowProps {
  element: LeaguePositions;
  index: number;
  setLeagueBeingViewed: (leagueBeingViewed: string) => void;
}

const calculateClassName = (index:number) => {
	return (index % 2 === 0 ? 'league-even' : 'league-odd');
};

const LeagueRow: React.FC<LeagueRowProps> = (props) => {
	const { element } = props;
	const setLeagueBeingViewed = React.useCallback(
		() => props.setLeagueBeingViewed(element.leagueName),
		[ props.setLeagueBeingViewed, element.leagueName ]
	);
	return (
		<tr
			className={calculateClassName(props.index)}
			key={element.leagueName}
			onClick={setLeagueBeingViewed}
		>
			<td className="league-name">{element.leagueName}</td>
			<td className="position">{element.position}</td>
		</tr>
	);
};

export default LeagueRow;
