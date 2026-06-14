package com.booking.system.v1.service.impl;

import com.booking.system.v1.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.booking.system.v1.service.CurrentUserService;


@Service
public class CurrentUserServiceImpl implements CurrentUserService {
    @Override
    public String getCurrentUsername() {
        Authentication auth =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        if (auth == null) {
            throw new UnauthorizedException(
                    "No authenticated user found"
            );
        }

        return auth.getName();
    }
}
