package club.beenest.autoconfigure;

import club.beenest.model.InterfaceInfo;
import club.beenest.service.InterfaceInfoService;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.logging.Logger;

@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class InterfaceCollectorAutoConfiguration {

    private static final Logger logger = Logger.getLogger(InterfaceCollectorAutoConfiguration.class.getName());

    public InterfaceCollectorAutoConfiguration() {
        logger.info("InterfaceCollectorAutoConfiguration initialized");
    }

    @Value("${spring.application.name:}")
    private String applicationName;

    /**
     * 使用Dubbo注解引用InterfaceInfoService接口
     */
    @DubboReference(version = "*", timeout = 5000, retries = 0)
    private InterfaceInfoService interfaceInfoService;

    /**
     * 自动配置 InterfaceCollector Bean
     * 使用 @ConditionalOnMissingBean 允许用户自定义实现覆盖默认配置
     *
     * @param applicationContext Spring 应用上下文
     * @return InterfaceCollector 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public InterfaceCollector interfaceCollector(ApplicationContext applicationContext) {
        InterfaceCollector collector = new InterfaceCollector();
        // 通过构造函数注入ApplicationContext可能更优雅，但为了兼容性这里使用setter
        try {
            collector.setApplicationContext(applicationContext);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize InterfaceCollector", e);
        }
        return collector;
    }

    /**
     * 创建应用启动监听器，在应用启动时收集接口信息
     * 使用Spring上下文方式收集所有bean中的接口信息
     *
     * @param interfaceCollector 接口收集器
     * @return 应用启动监听器
     */
    @Bean
    public ApplicationRunner interfaceCollectorRunner(InterfaceCollector interfaceCollector) {
        return args -> {
            try {
                logger.info("开始收集最新接口信息...");

                // 每次启动都重新收集所有接口信息，确保获取最新的接口
                List<InterfaceInfo> interfaceInfos = interfaceCollector.collectInterfaces(applicationName);

                // 如果收集到了接口信息
                if (!CollectionUtils.isEmpty(interfaceInfos)) {
                    logger.info("成功收集到 " + interfaceInfos.size() + " 个接口信息");

                    // 使用Dubbo调用发送接口信息
                    boolean result = interfaceInfoService.sendInterfaceInfos(interfaceInfos);
                    if (result) {
                        logger.info("接口信息发送成功");
                    } else {
                        logger.severe("接口信息发送失败");
                    }
                } else {
                    logger.info("未收集到任何接口信息");
                }
            } catch (Exception e) {
                logger.severe("收集接口信息时发生错误: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}
