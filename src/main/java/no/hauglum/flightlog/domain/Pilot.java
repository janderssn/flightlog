package no.hauglum.flightlog.domain;

public class Pilot {

    private final String mName;
    private final String mUserId;

    public Pilot(String userId, String name) {
        mUserId = userId;
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public String getUserId() {
        return mUserId;
    }
}
