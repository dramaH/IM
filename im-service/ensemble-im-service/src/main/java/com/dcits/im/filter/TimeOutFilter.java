package com.dcits.im.filter;

import com.dcits.comet.commons.ThreadLocalManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@SuppressWarnings("all")
@Component
@Slf4j
@Order(11)
@WebFilter(filterName = "timeOutFilter", urlPatterns = {"/*"})
public class TimeOutFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        ThreadLocalManager.put("tranTimestamp",System.currentTimeMillis());
        filterChain.doFilter(request, response);
    }
}