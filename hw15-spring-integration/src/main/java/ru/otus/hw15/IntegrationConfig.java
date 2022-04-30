package ru.otus.hw15;

import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.aggregator.HeaderAttributeCorrelationStrategy;
import org.springframework.integration.aggregator.MessageCountReleaseStrategy;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.router.HeaderValueRouter;
import ru.otus.hw15.service.CheckService;


@Configuration
@EnableIntegration
public class IntegrationConfig {
    private final static int RELEASE_MESSAGE_COUNT = 3;
    private final static int QUEUE_CAPACITY = 100;

    @Bean(name = "loanRequestChannel")
    public QueueChannel loanRequestChannel() {
        return MessageChannels
                .queue(QUEUE_CAPACITY)
                .get();
    }

    @Bean(name = "loanSolutionChannel")
    public PublishSubscribeChannel loanSolutionChannel() {
        return MessageChannels
                .publishSubscribe()
                .get();
    }

    @Bean(name = "underwritingChannel")
    public QueueChannel underwritingChannel() {
        return MessageChannels
                .queue(QUEUE_CAPACITY)
                .get();
    }

    @Bean(name = "securityChannel")
    public QueueChannel securityChannel() {
        return MessageChannels
                .queue(QUEUE_CAPACITY)
                .get();
    }

    @Bean(name = "aggregationChannel")
    public QueueChannel aggregationChannel() {
        return MessageChannels
                .queue(QUEUE_CAPACITY)
                .get();
    }

    @Bean
    public IntegrationFlow securityFlow(CheckService checkSecurityService) {
        return IntegrationFlows
                .from("securityChannel")
                .handle(checkSecurityService)
                .channel("aggregationChannel")
                .get();
    }

    @Bean
    public IntegrationFlow underwritingFlow(CheckService checkUnderwritingService) {
        return IntegrationFlows
                .from("underwritingChannel")
                .handle(checkUnderwritingService)
                .channel("aggregationChannel")
                .get();
    }

    @Bean
    public IntegrationFlow aggregationFlow() {
        return IntegrationFlows
                .from("aggregationChannel")
                .aggregate(a -> {
                            a.correlationStrategy(new HeaderAttributeCorrelationStrategy("correlationId"));
                            a.releaseStrategy(new MessageCountReleaseStrategy(RELEASE_MESSAGE_COUNT));
                        }
                )
                .channel("loanSolutionChannel")
                .get();
    }

    @Bean
    public IntegrationFlow loanRequestFlow() {
        return IntegrationFlows
                .from("loanRequestChannel")
                .split("requestSplitter", "splitRequest")
                .route(depIdRouter())
                .get();
    }

    @Bean
    public HeaderValueRouter depIdRouter() {
        val router = new HeaderValueRouter("depId");
        router.setChannelMapping("securityDep", "securityChannel");
        router.setChannelMapping("underwritingDep", "underwritingChannel");
        router.setChannelMapping("loanDep", "aggregationChannel");
        return router;
    }
}
