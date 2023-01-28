package com.moon.im.common;

import com.moon.im.common.exception.ApplicationExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Chanmoey
 * @date 2023年01月28日
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseVO<T> {

    private static final String SUCCESS = "success";

    private static final String SERVER_ERROR = "系统内部异常";

    private int code;

    private String msg;

    private T data;

    public static ResponseVO<Object> successResponse(Object data) {
        return new ResponseVO<>(200, SUCCESS, data);
    }

    public static ResponseVO<Object> successResponse() {
        return new ResponseVO<>(200, SUCCESS);
    }

    public static ResponseVO<Object> errorResponse() {
        return new ResponseVO<>(500, SERVER_ERROR);
    }

    public static ResponseVO<Object> errorResponse(int code, String msg) {
        return new ResponseVO<>(code, msg);
    }

    public static ResponseVO<Object> errorResponse(ApplicationExceptionEnum enums) {
        return new ResponseVO<>(enums.getCode(), enums.getError());
    }

    public boolean isOk() {
        return this.code == 200;
    }


    public ResponseVO(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResponseVO<T> success() {
        this.code = 200;
        this.msg = SUCCESS;
        return this;
    }

    public ResponseVO<T> success(T data) {
        this.code = 200;
        this.msg = SUCCESS;
        this.data = data;
        return this;
    }
}
