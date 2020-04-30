package network.asimov.config;

import network.asimov.behavior.check.CheckBehavior;
import network.asimov.behavior.check.ascan.AddressExistCheck;
import network.asimov.mongodb.service.ascan.TransactionStatisticsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author zhangjing
 * @date 2020-01-29
 */
@Configuration
public class CheckConfig {
    @Resource(name = "transactionStatisticsService")
    private TransactionStatisticsService transactionStatisticsService;

    @Bean("addressExistCheck")
    public CheckBehavior addressExistCheck() {
        return AddressExistCheck.builder().transactionStatisticsService(transactionStatisticsService).build();
    }
}
