/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

import ClayButton from '@clayui/button';
import ClayTable from '@clayui/table';
import React, {useRef} from 'react';
import {DndProvider, createDndContext} from 'react-dnd';
import HTML5Backend from 'react-dnd-html5-backend';

import LanguageListItem from './LanguageListItem.es';
import LanguageListItemEditable from './LanguageListItemEditable.es';

const noop = () => {};

const LanguagesList = ({
	defaultLocaleId,
	locales,
	isEditable = false,
	onMakeDefault = noop,
	onEditBtnClick = noop,
	onItemDrop = noop,
}) => {
	const manager = useRef(createDndContext(HTML5Backend));

	return (
		<ClayTable borderless headVerticalAlignment="middle" hover={false}>
			<ClayTable.Head>
				<ClayTable.Row>
					<ClayTable.Cell expanded headingCell headingTitle>
						{Liferay.Language.get('active-language')}
					</ClayTable.Cell>

					{isEditable && (
						<ClayTable.Cell align="center">
							<ClayButton
								displayType="secondary"
								onClick={onEditBtnClick}
								small
							>
								{Liferay.Language.get('edit')}
							</ClayButton>
						</ClayTable.Cell>
					)}
				</ClayTable.Row>
			</ClayTable.Head>

			<ClayTable.Body>
				<DndProvider manager={manager.current.dragDropManager}>
					{locales.map((locale, index) => {
						const baseProps = {
							...locale,
							index,
							isDefault: defaultLocaleId === locale.localeId,
							key: locale.localeId,
						};

						return isEditable ? (
							<LanguageListItemEditable
								{...baseProps}
								onItemDrop={onItemDrop}
								onMakeDefault={onMakeDefault}
							/>
						) : (
							<LanguageListItem {...baseProps} />
						);
					})}
				</DndProvider>
			</ClayTable.Body>
		</ClayTable>
	);
};

export default LanguagesList;
