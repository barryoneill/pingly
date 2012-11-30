/*
 *    Pingly - A simple app for checking for signs of life in hosts/services.
 *    Copyright 2012 Barry O'Neill
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package net.nologin.meep.pingly.model;

/**
 * Schedule 'Repeat Type' enum, providing translation key support
 */
public enum ScheduleRepeatType {

	// Name(id, millis_perunit, rangeupper, defaultval)
	OnceOff(0, 0, 1, 1),
	Seconds(1, 1000, 59, 30),
	Minutes(2, 60 * Seconds.millisPerValue, 59, 30),
	Hours(3, 60 * Minutes.millisPerValue, 23, 1),
	Days(4, 24 * Hours.millisPerValue, 31, 1),
	Weeks(5, 7 * Days.millisPerValue, 52, 1);

	public int id;
	public int millisPerValue;
	public int rangeLowerLimit;
	public int rangeUpperLimit;
	public int defaultValue;

	ScheduleRepeatType(int id, int millisPerValue, int rangeUpperLimit, int defaultValue) {
		this(id, millisPerValue, 1, rangeUpperLimit, defaultValue);
	}

	ScheduleRepeatType(int id, int millisPerValue, int rangeLowerLimit, int rangeUpperLimit, int defaultValue) {
		this.id = id;
		this.millisPerValue = millisPerValue;
		this.rangeLowerLimit = rangeLowerLimit;
		this.rangeUpperLimit = rangeUpperLimit;
		this.defaultValue = defaultValue;
	}

	public int getId() {
		return id;
	}

	public String getResourceNameForName() {
		return "scheduler_repetition_unit_" + id + "_name";
	}

	public String getResourceNameForSummary() {
		return "scheduler_repetition_unit_" + id + "_summary";
	}

	public static ScheduleRepeatType fromId(long id) {
		for (ScheduleRepeatType t : ScheduleRepeatType.values()) {
			if (id == t.id) {
				return t;
			}
		}
		throw new IllegalArgumentException("ID " + id + " not a valid " + ScheduleRepeatType.class.getSimpleName());
	}

	public long getAsMillis(int numUnits) {
		return numUnits * millisPerValue;
	}

	@Override
	public String toString() {
		return super.toString() + "[" + id + "]";
	}


}
