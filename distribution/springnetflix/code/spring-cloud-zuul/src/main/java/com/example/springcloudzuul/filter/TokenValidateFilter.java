package com.example.springcloudzuul.filter;

import com.alibaba.fastjson.JSONObject;
import com.example.springcloudzuul.configuration.TokenConfigurationBean;
import com.example.springcloudzuul.jwt.JwtTokenProvider;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import io.jsonwebtoken.Claims;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.servlet.http.HttpServletRequest;

@Component
public class TokenValidateFilter extends ZuulFilter {

    @Autowired
    private TokenConfigurationBean tokenConfigurationBean;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 6;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext context = RequestContext.getCurrentContext();
        return !tokenConfigurationBean.getNoAuthenticationRoutes().contains(context.get("proxy"));
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest httpServletRequest = requestContext.getRequest();
        String token = httpServletRequest.getHeader("Authorization");
        if (null == token) {
            forbidden();
            return null;
        }

        Claims claims = jwtTokenProvider.parseToken(token);
        if (null == claims) {
            forbidden();
            return null;
        }
        System.out.println("token: " + JSONObject.toJSONString(claims));
        return null;
    }

    private void forbidden() {
        RequestContext.getCurrentContext().setResponseStatusCode(HttpStatus.SC_FORBIDDEN);
        ReflectionUtils.rethrowRuntimeException(new ZuulException("无访问权限", HttpStatus.SC_FORBIDDEN, "token非法"));
    }
}
