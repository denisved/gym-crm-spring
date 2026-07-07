package org.gymcrm.util;

import ch.qos.logback.classic.Logger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Slf4j
@Component
public class CredentialsGenerator {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private final Random random = new Random();

    public String generatePassword() {
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

    public String generateUsername(String firstName, String lastName, List<String> existingUsernames) {
        String baseName = firstName + "." + lastName;
        int maxSuffix = -1;
        boolean exactMatchExists = false;

        for (String existing : existingUsernames) {
            if (existing.equals(baseName)) {
                exactMatchExists = true;
            } else if (existing.startsWith(baseName)) {
                try {
                    String suffixStr = existing.substring(baseName.length());
                    int suffix = Integer.parseInt(suffixStr);
                    if (suffix > maxSuffix) {
                        maxSuffix = suffix;
                    }
                } catch (NumberFormatException e) {
                    log.trace("Found non-numeric suffix for username, ignoring format exception: {}", e.getMessage());
                }
            }
        }

        if (!exactMatchExists && maxSuffix == -1) {
            return baseName;
        }

        int nextSuffix = Math.max(maxSuffix, 0) + 1;
        return baseName + nextSuffix;
    }
}
