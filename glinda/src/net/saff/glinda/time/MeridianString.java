package net.saff.glinda.time;

public class MeridianString {
	private String meridianString;

	public MeridianString(String meridianString) {
		this.meridianString = meridianString;
	}

	public String getMeridianString() {
		return meridianString;
	}

	int interpretTime(String hourNumber) {
		Integer hour = Integer.valueOf(hourNumber);
		if (getMeridianString().equals("pm"))
			hour += 12;
		if (hourNumber.equals("12"))
			hour -= 12;
		return hour;
	}
}