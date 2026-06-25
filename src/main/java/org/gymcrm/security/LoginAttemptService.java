package org.gymcrm.security;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPT = 3;
    private static final long LOCK_TIME_DURATION = 300000;

    private final Map<String, Integer> attemptsCache = new ConcurrentHashMap<>();
    private final Map<String, Long> lockTimeCache = new ConcurrentHashMap<>();

    public void loginSucceeded(String username) {
        attemptsCache.remove(username);
        lockTimeCache.remove(username);
    }

    public void loginFailed(String username) {
        int attempts = attemptsCache.getOrDefault(username, 0);
        attempts++;
        attemptsCache.put(username, attempts);

        if (attempts >= MAX_ATTEMPT) {
            lockTimeCache.put(username, System.currentTimeMillis() + LOCK_TIME_DURATION);
        }
    }

    public boolean isBlocked(String username) {
        if (lockTimeCache.containsKey(username)) {
            long lockTime = lockTimeCache.get(username);
            if (System.currentTimeMillis() < lockTime) {
                return true;
            } else {
                lockTimeCache.remove(username);
                attemptsCache.remove(username);
                return false;
            }
        }
        return false;
    }
}