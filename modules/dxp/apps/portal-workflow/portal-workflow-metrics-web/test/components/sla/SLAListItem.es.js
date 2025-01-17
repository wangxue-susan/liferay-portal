/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 */

import React from 'react';
import renderer from 'react-test-renderer';

import SLAListItem from '../../../src/main/resources/META-INF/resources/js/components/sla/SLAListItem.es';
import {MockRouter as Router} from '../../mock/MockRouter.es';

test('Should render component', () => {
	const component = renderer.create(
		<Router>
			<SLAListItem
				dateModified={
					new Date(Date.UTC('2019', '04', '06', '20', '32', '18'))
				}
				duration={58000}
				id={1234}
				instancesCount="10"
				onTime="5"
				overdue="5"
				processName="Process test"
			/>
		</Router>
	);

	const tree = component.toJSON();

	expect(tree).toMatchSnapshot();
});

test('Should render component', () => {
	const component = mount(
		<Router>
			<SLAListItem
				dateModified={
					new Date(Date.UTC('2019', '04', '06', '20', '32', '18'))
				}
				duration={58000}
				instancesCount="10"
				onTime="5"
				overdue="5"
				processName="Process test"
			/>
		</Router>
	);

	const instance = component.find(SLAListItem).instance();

	instance.context = {
		showConfirmDialog: () => {},
	};

	instance.showConfirmDialog();

	expect(component).toMatchSnapshot();
});

test('Should render component blocked', () => {
	const component = mount(
		<Router>
			<SLAListItem
				dateModified={
					new Date(Date.UTC('2019', '04', '06', '20', '32', '18'))
				}
				duration={58000}
				instancesCount="10"
				onTime="5"
				overdue="5"
				processName="Process test"
				status={2}
			/>
		</Router>
	);

	const instance = component.find(SLAListItem).instance();

	instance.context = {
		showConfirmDialog: () => {},
	};

	instance.showConfirmDialog();

	expect(component).toMatchSnapshot();
});
