package generator.domain.demo;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
public class Result<T>{

    private Integer code;
    private String msg;
    private T data;
    private Long timestamp;

    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.timestamp = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    public Result(){
        this.timestamp = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    public static <T> Result<T> success(T data){
        return new Result<>(200,"success",data);
    }

    public static <T> Result<T> error(Integer code,String msg){
        return new Result<>(code,msg,null);
    }

    public static <T> Result<T> success(){
        return new Result<>(200,"success",null);
    }
}
