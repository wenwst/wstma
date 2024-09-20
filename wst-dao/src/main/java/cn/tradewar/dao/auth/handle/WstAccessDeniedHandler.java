package cn.tradewar.dao.auth.handle;


import cn.tradewar.core.common.GenericResponse;
import cn.tradewar.core.common.ServiceError;
import com.alibaba.fastjson.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Log4j2
@Component
public class WstAccessDeniedHandler implements AccessDeniedHandler{

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException e) throws IOException {
        log.error("Access denied for request: {}", request.getRequestURI());
        response.setCharacterEncoding("utf-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write("Access Denied. You do not have sufficient privileges to access this resource.");
        response.getWriter()
                .write(JSON.toJSONString(GenericResponse.response(ServiceError.GLOBAL_ERR_NO_AUTHORITY)));
        response.getWriter().flush();
    }
}
