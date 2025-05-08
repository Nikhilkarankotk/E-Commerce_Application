package com.nkk.Users.constants;

public final class UserConstants {

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String SECRET_KEY = "MySuperSecretKeyThatShouldBeVeryLong";
    public static final long EXPIRATION_TIME = 8_6400_000; // 1 day
    public static final long REFRESH_TOKEN_EXPIRATION =  17_28_00_000; // 2 days
    public static final String STATUS_200 = "200";
    public static final String MESSAGE_200 = "Request processed successfully";
    public static final String STATUS_201 = "201";
    public static final String MESSAGE_201 = "Request Created successfully";
    public static final String STATUS_202 = "202";
    public static final String MESSAGE_202 = "Request Accepted successfully";

}
