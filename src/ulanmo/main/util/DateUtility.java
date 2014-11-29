package ulanmo.main.util;

public class DateUtility {
	private DateUtility() {}
	
	public static String getDate() {
		return "2013-08-18";
	}
	
	public static String getHour(String date) {
		String[] comp = date.split(" ");
		String[] time = comp[1].split(":");
		int hour = Integer.parseInt(time[0]);
		if(hour == 0)
			return "12am";
		else if(hour <= 11)
			return hour+"am";
		else if(hour == 12)
			return "12pm";
		else
			return hour%12 + "pm";
	}
	
	public static String getHour() {
		return "9";
	}
}
