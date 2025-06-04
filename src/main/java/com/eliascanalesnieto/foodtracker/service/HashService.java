package com.eliascanalesnieto.foodtracker.service;

import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import java.security.Key;
import java.util.Base64;

@Service
public class HashService {

    public String hash(final Key key, final String message) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(key);

            byte[] hmacBytes = mac.doFinal(message.getBytes());

            return Base64.getEncoder().encodeToString(hmacBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
