package club.beenest.sso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动程序
 * 
 * @author beenest
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class SsoApplication
{
    public static void main(String[] args)
    {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(SsoApplication.class, args);
        System.out.println(" Application  启动成功");
    }
}
