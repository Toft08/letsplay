package com.toft.letsplay.security;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class TokenBlacklist {
    private final Set<String> blackListedTokens = new HashSet<>();

    public void blackList(String token) {
        blackListedTokens.add(token);
    }

    public boolean isBlackListed(String token) {
        return blackListedTokens.contains(token);
    }
}
