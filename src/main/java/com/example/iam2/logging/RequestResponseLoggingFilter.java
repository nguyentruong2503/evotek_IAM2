package com.example.iam2.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Enumeration;

@Component
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        // Log Request
        StringBuilder reqInfo = new StringBuilder();
        reqInfo.append("REQUEST [")
                .append(request.getMethod())
                .append("] ")
                .append(request.getRequestURI());

        // Log headers (trừ Authorization)
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            if (!"authorization".equalsIgnoreCase(headerName)) {
                reqInfo.append("\nHeader: ").append(headerName)
                        .append(" = ").append(request.getHeader(headerName));
            }
        }

        logger.info(reqInfo.toString());

        // Thực thi request
        filterChain.doFilter(request, response);

        long duration = System.currentTimeMillis() - startTime;
        logger.info("RESPONSE [{}] status={} ({} ms)",
                request.getRequestURI(),
                response.getStatus(),
                duration);
    }
}