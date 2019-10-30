package com.example.springcloudzuul.filter;


import com.alibaba.fastjson.JSONObject;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.post.SendErrorFilter;
import org.springframework.stereotype.Component;



@Component
public class SendErrorRestFilter extends SendErrorFilter {

    private final static Logger LOGGER = LoggerFactory.getLogger(SendErrorFilter.class);

    @Override
    public String filterType() {
        return "error";
    }


    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public Object run() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        Throwable throwable = findCauseException(requestContext.getThrowable());

        String status = String.valueOf(requestContext.getResponseStatusCode());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", status);
        jsonObject.put("errorMessage", throwable.getMessage());

        LOGGER.warn("出现异常: ", requestContext.getThrowable());

        requestContext.setResponseBody(jsonObject.toJSONString());
        requestContext.getResponse().setContentType("text/html;charset=utf-8");
        requestContext.remove("throwable");
        return null;
    }

    private Throwable findCauseException(Throwable throwable) {
        while (throwable.getCause() != null) {
            throwable = throwable.getCause();
        }
        return throwable;
    }
}
