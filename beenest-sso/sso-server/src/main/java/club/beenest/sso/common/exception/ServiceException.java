package club.beenest.sso.common.exception;

import lombok.Getter;

/**
 * 业务逻辑异常
 */
@Getter
public class ServiceException extends RuntimeException {
    private Integer code;

    public ServiceException(String message) {
        super(message);
        this.code = 500;
    }

    public ServiceException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
