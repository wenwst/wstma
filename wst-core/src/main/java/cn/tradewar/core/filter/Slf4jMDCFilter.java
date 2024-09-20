package cn.tradewar.core.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.util.UUID;

import static cn.tradewar.core.consts.CommConst.MDC_UUID_TOKEN_KEY;
import static cn.tradewar.core.consts.LogConst.ERROR_FORMAT;

@EqualsAndHashCode(callSuper = false)
@Component
@Log4j2
public class Slf4jMDCFilter extends OncePerRequestFilter{

    @Override
    protected void doFilterInternal(@NonNull final HttpServletRequest request,
                                    @NonNull final HttpServletResponse response,
                                    @NonNull final FilterChain chain) {
        try {
            MDC.put(MDC_UUID_TOKEN_KEY, UUID.randomUUID().toString());
            chain.doFilter(request, response);
        } catch (Exception ex) {
            log.error(ERROR_FORMAT, "Exception occurred in filter while setting UUID for logs", ex);
        } finally {
            MDC.remove(MDC_UUID_TOKEN_KEY);
        }
    }

    @Override
    protected boolean isAsyncDispatch(@NonNull final HttpServletRequest request) {
        return false;
    }

    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return false;
    }
}