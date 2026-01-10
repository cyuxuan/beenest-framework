package club.beenest.sso.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SsoResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer code;
    private String msg;
    private T data;

    public static <T> SsoResult<T> success(T data) {
        return new SsoResult<>(200, "Success", data);
    }

    public static <T> SsoResult<T> error(Integer code, String msg) {
        return new SsoResult<>(code, msg, null);
    }

    public boolean isSuccess() {
        return code != null && code == 200;
    }
}
