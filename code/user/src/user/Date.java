package user;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import nl.utwente.groove.annotation.UserOperation;
import nl.utwente.groove.annotation.UserType;

@SuppressWarnings("javadoc")
@UserType
public record Date(int day, int month, int year) {
	@UserOperation
	public String toString() { // Returns a String representation of this Date
		return "" + day() + " " + MONTHS[month() - 1] + " " + year();
	}

	@UserOperation(indeterminate = true)
	public int daysSince() { // Returns the number of days between this Date and now
		var cal = Calendar.getInstance(); cal.set(year(), month() - 1, day());
		long diff = Math.abs(new java.util.Date().getTime() - cal.getTime().getTime());
		return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
	}

	@UserOperation
	public Date next() { // Returns the next Date with respect to this one
		int day = day() + 1, month = month(), year = year();
		if (day > daysInMonth()) {
			day = 1; month += 1;
			if (month > 12) {
				month = 1; year += 1;
			}
		}
		return new Date(day, month, year);
	}

	private int daysInMonth() { // Returns the number of days in this Date's month
		return switch (month()) {
		case 1, 3, 5, 7, 8, 10, 12 -> 31;
		case 2 -> 28 + (isLeapYear(year()) ? 1 : 0);
		default -> 30;
		};
	}

	static private String[] MONTHS =
		{"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

	@UserOperation
	static public boolean isLeapYear(int year) { // Tests whether year is a leap year
		return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0);
	}

	@UserOperation(indeterminate = true)
	static public Date today() { // Returns a Date object representing today
		var today = Calendar.getInstance();
		return new Date(today.get(Calendar.DAY_OF_MONTH),
				today.get(Calendar.MONTH) + 1, today.get(Calendar.YEAR));
	}

	static public void main(String[] args) {
		Date birthday = new Date(6, 6, 1966);
		System.out.println("Days since " + birthday + ": " + birthday.daysSince());
		Date leapTest = new Date(27, 2, 1900);
		System.out.println("Two days after " + leapTest + " was " + leapTest.next().next());
	}
}