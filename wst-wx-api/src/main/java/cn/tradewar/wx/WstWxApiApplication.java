package cn.tradewar.wx;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(
        scanBasePackages = {
                "cn.tradewar.dao.services",
                "cn.tradewar.dao.auth",
                "cn.tradewar.dao.auth.utils",
                "cn.tradewar.core",
                "cn.tradewar.wx"}
)
@MapperScan({"cn.tradewar.dao.mapper"})
@EntityScan(basePackages = {"cn.tradewar.dao.model.entity"})
@EnableTransactionManagement
@EnableScheduling
public class WstWxApiApplication{

    public static void main(String[] args) {
        SpringApplication.run(WstWxApiApplication.class, args);
    }

}
