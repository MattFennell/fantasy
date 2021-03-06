import { getBearerHeader } from '../CredentialInputService';
import { CollegeTeam } from '../../Models/Interfaces/CollegeTeam';

export const makeCollegeTeam = (name: string): Promise<CollegeTeam> => {
	return fetch('/api/college/make', {
		method: 'POST',
		body: name,
		headers: { 'Content-Type': 'application/json', Authorization: getBearerHeader() }
	}).then(response => {
		if (!response.ok) {
			if (response.status === 400) {
				return response.json().then(json => {
					if (response.ok) {
						return json;
					} else {
						return Promise.reject(json.message);
					}
				});
			} else if (response.status === 500) {
				throw new Error('Internal server error');
			} else {
				throw new Error(response.status.toString());
			}
		} else if (response.status === 200) {
			return response.json();
		}
	});
};

export const deleteCollegeTeam = (name: string): Promise<any> => {
	return fetch('/api/college/delete', {
		method: 'POST',
		body: name,
		headers: { 'Content-Type': 'application/json', Authorization: getBearerHeader() }
	}).then(response => {
		if (!response.ok) {
			if (response.status === 400) {
				return response.json().then(json => {
					if (response.ok) {
						return json;
					} else {
						return Promise.reject(json.message);
					}
				});
			} else if (response.status === 500) {
				throw new Error('Internal server error');
			} else {
				throw new Error(response.status.toString());
			}
		} else if (response.status === 201) {
			return response.json();
		}
	});
};

export const getCollegeTeams = (sortBy: string): Promise<CollegeTeam[]> => {
	return fetch('/api/college/all/sort/' + sortBy, {
		method: 'GET',
		headers: { Authorization: getBearerHeader() }
	}).then(response => {
		if (response.status === 400) {
			return response.json().then(json => {
				if (response.ok) {
					return json;
				} else {
					return Promise.reject(json.message);
				}
			});
		} else if (response.status === 200) {
			return response.json();
		}
	});
};
