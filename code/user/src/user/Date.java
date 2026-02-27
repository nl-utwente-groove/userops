package user;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import nl.utwente.groove.annotation.UserOperation;
import nl.utwente.groove.annotation.UserType;

@SuppressWarnings("javadoc")
@UserType
public record Date(int day, int month, int year) {
	@Override
	@UserOperation
	public String toString() {
		return "" + day() + " " + MONTHS[month() - 1] + " " + year();
	}

	public boolean isLeapYear() {
		return year() % 4 == 0 && (year() % 100 != 0 || year() % 400 == 0);
	}

	@UserOperation(indeterminate = true)
	public int daysSince() {
		var cal = Calendar.getInstance();
		cal.set(year(), month() - 1, day());
		var today = new java.util.Date();
		long diffInMillies = Math.abs(today.getTime() - cal.getTime().getTime());
		return (int) TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
	}

	@UserOperation
	public Date next() {
		int day = day() + 1;
		var month = month();
		var year = year();
		if (day > daysInMonth(month, isLeapYear())) {
			day = 1;
			month += 1;
			if (month > 12) {
				month = 1;
				year += 1;
			}
		}
		return new Date(day, month, year);
	}

	static private String[] MONTHS = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

	@UserOperation
	static public int daysInMonth(int month, boolean leapYear) {
		return switch (month) {
		case 1, 3, 5, 7, 8, 10, 12 -> 31;
		case 2 -> 28 + (leapYear ? 1 : 0);
		default -> 30;
		};
	}

	@UserOperation(indeterminate = true)
	static public Date today() {
		var today = Calendar.getInstance();
		return new Date(today.get(Calendar.DAY_OF_MONTH), today.get(Calendar.MONTH) + 1, today.get(Calendar.YEAR));
	}

	static public void main(String[] args) {
		Date birthday = new Date(6, 6, 1966);
		System.out.println("Days since " + birthday + ": " + birthday.daysSince());
		Date leapTest = new Date(27, 2, 1900);
		System.out.println("Two days after " + leapTest + " was " + leapTest.next().next());
	}
}
