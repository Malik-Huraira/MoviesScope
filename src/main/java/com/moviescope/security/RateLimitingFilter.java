package com.moviescope.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitingFilter implements Filter {

    private static final int MAX_REQUESTS = 5; // max allowed
    private static final long WINDOW_TIME = 120_000; // 2 minutes (in ms)

    private final Map<String, RequestInfo> requestMap = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String clientIp = httpRequest.getRemoteAddr();
        long currentTime = System.currentTimeMillis();

        requestMap.putIfAbsent(clientIp, new RequestInfo(new AtomicInteger(0), currentTime));

        RequestInfo info = requestMap.get(clientIp);

        // Check if 2 minutes passed → reset counter
        if (currentTime - info.startTime >= WINDOW_TIME) {
            info.counter.set(0);
            info.startTime = currentTime;
        }

        // Increment request count
        int currentCount = info.counter.incrementAndGet();

        if (currentCount > MAX_REQUESTS) {
            httpResponse.setStatus(429);
            httpResponse.getWriter().write("Too many requests. Try again after 2 minutes.");
            return;
        }

        chain.doFilter(request, response);
    }

    // Helper class
    private static class RequestInfo {
        AtomicInteger counter;
        long startTime;

        RequestInfo(AtomicInteger counter, long startTime) {
            this.counter = counter;
            this.startTime = startTime;
        }
    }
}
