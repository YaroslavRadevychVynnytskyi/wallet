package com.nerdysoft.security.service;

import java.util.List;
import java.util.function.Predicate;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

@Service
public class RouterCheckerService {
    private final List<String> openEndpoints = List.of(
        "/auth/**"
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public Predicate<String> isOpenedRequest =
            route -> openEndpoints.stream().anyMatch(pattern -> pathMatcher.match(pattern, route));
}
