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

AUI.add(
	'liferay-calendar-reminders',
	A => {
		var Lang = A.Lang;

		var STR_BLANK = '';

		var TPL_REMINDER_SECTION =
			'<div class="calendar-portlet-reminder-section form-inline">' +
			'<input <tpl if="!disabled">checked="checked"</tpl> class="calendar-portlet-reminder-check" id="{portletNamespace}reminder{i}" name="{portletNamespace}reminder{i}" type="checkbox" />' +
			'<label class="reminder-type" for="{portletNamespace}reminder{i}">' +
			'<input id="{portletNamespace}reminderType{i}" name="{portletNamespace}reminderType{i}" type="hidden" value="email" />' +
			'{email}' +
			'</label>' +
			'<input class="input-mini reminder-value" <tpl if="disabled">disabled="disabled"</tpl> name="{portletNamespace}reminderValue{i}" size="5" type="text" value="{time.value}" /> ' +
			'<select class="reminder-duration span2" <tpl if="disabled">disabled="disabled"</tpl> name="{portletNamespace}reminderDuration{i}">' +
			'<option <tpl if="time.desc == \'minutes\'">selected="selected"</tpl> value="60">{minutes}</option>' +
			'<option <tpl if="time.desc == \'hours\'">selected="selected"</tpl> value="3600">{hours}</option>' +
			'<option <tpl if="time.desc == \'days\'">selected="selected"</tpl> value="86400">{days}</option>' +
			'<option <tpl if="time.desc == \'weeks\'">selected="selected"</tpl> value="604800">{weeks}</option>' +
			'</select>' +
			'</div>';

		var Reminders = A.Component.create({
			ATTRS: {
				portletNamespace: {
					value: '',
				},

				strings: {
					value: {
						days: Liferay.Language.get('days'),
						email: Liferay.Language.get('email'),
						hours: Liferay.Language.get('hours'),
						minutes: Liferay.Language.get('minutes'),
						weeks: Liferay.Language.get('weeks'),
					},
				},

				values: {
					validator: Lang.isArray,
					value: [
						{
							interval: 10,
							type:
								Liferay.CalendarUtil.NOTIFICATION_DEFAULT_TYPE,
						},
						{
							interval: 60,
							type:
								Liferay.CalendarUtil.NOTIFICATION_DEFAULT_TYPE,
						},
					],
				},
			},

			NAME: 'reminders',

			UI_ATTRS: ['values'],

			prototype: {
				_onChangeCheckbox(event) {
					var target = event.target;

					var checked = target.get('checked');
					var elements = target.siblings('input[type=text],select');

					elements.set('disabled', !checked);

					if (checked) {
						elements.first().select();
					}
				},

				_uiSetValues(val) {
					var instance = this;

					var boundingBox = instance.get('boundingBox');
					var portletNamespace = instance.get('portletNamespace');
					var strings = instance.get('strings');

					var buffer = [];

					var tplReminder = instance.tplReminder;

					for (var i = 0; i < val.length; i++) {
						var value = val[i];

						buffer.push(
							tplReminder.parse(
								A.merge(strings, {
									disabled: !value.interval,
									i,
									portletNamespace,
									time: Liferay.Time.getDescription(
										value.interval
									),
								})
							)
						);
					}

					boundingBox.setContent(buffer.join(STR_BLANK));
				},

				bindUI() {
					var instance = this;

					var boundingBox = instance.get('boundingBox');

					boundingBox.delegate(
						'change',
						instance._onChangeCheckbox,
						'.calendar-portlet-reminder-check',
						instance
					);
				},

				initializer() {
					var instance = this;

					instance.tplReminder = new A.Template(TPL_REMINDER_SECTION);
				},
			},
		});

		Liferay.Reminders = Reminders;
	},
	'',
	{
		requires: [
			'aui-base',
			'aui-template-deprecated',
			'liferay-calendar-util',
		],
	}
);
