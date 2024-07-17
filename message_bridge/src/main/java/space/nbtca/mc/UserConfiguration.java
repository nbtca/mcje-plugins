package space.nbtca.mc;
import de.exlll.configlib.Configuration;
import lombok.Data;
@Data
@Configuration
public class UserConfiguration {
    private String notificationCenterWsAddress = "ws://127.0.0.1/mc";
    private String notificationCenterToken = "token";
}