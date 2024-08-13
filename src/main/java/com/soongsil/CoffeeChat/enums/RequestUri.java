package com.soongsil.CoffeeChat.enums;

public class RequestUri {
	private static final String prefix = "/api/v2";
	public static final String APPLICATION_URI = prefix + "/applications";
	public static final String CLUB_URI = prefix + "/clubs";
	public static final String MENTEE_URI = prefix + "/mentees";
	public static final String MENTOR_URI = prefix + "/mentors";
	public static final String POSSIBLEDATE_URI = prefix + "/possibleDates";
	// public static final String REFRESH_URI = "/reissue";
	public static final String USER_URI = prefix + "/users";
	public static final String EMAIL_URI = "/auth/email";
}
