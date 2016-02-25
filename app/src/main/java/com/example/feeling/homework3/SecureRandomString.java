package com.example.feeling.homework3;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by luca on 21/1/2016.
 */
public final class SecureRandomString {
    private SecureRandom random = new SecureRandom();

    public String nextString() {
        return new BigInteger(130, random).toString(32);
    }

}
