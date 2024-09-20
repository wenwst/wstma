package cn.tradewar.core.config;

import cn.tradewar.core.filter.Slf4jMDCFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@ComponentScan(basePackages = {"cn.tradewar.core.filter"})
public class BeanConfig {

    private final Slf4jMDCFilter slf4jMDCFilter;

    @Bean
    public FilterRegistrationBean<Slf4jMDCFilter> servletRegistrationBean() {
        final FilterRegistrationBean<Slf4jMDCFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(slf4jMDCFilter);
        filterRegistrationBean.setOrder(2);
        return filterRegistrationBean;
    }
}
