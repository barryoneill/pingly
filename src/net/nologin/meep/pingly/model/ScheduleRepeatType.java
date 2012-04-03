package net.nologin.meep.pingly.model;


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
