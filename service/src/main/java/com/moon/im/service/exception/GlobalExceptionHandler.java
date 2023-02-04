package com.moon.im.service.exception;

import com.moon.im.common.BaseErrorCode;
import com.moon.im.common.ResponseVO;
import com.moon.im.common.exception.ApplicationException;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

/**
 * @author Chanmoey
 * @date 2023年01月28日
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseVO<Object> exceptionHandler(Exception e) {
        e.printStackTrace();
        ResponseVO<Object> responseVO = new ResponseVO<>();
        responseVO.setCode(BaseErrorCode.SYSTEM_ERROR.getCode());
        responseVO.setMsg(BaseErrorCode.SYSTEM_ERROR.getError());

        return responseVO;
    }

    /**
     * Validator 参数校验异常处理
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseBody
    public ResponseVO<Object> handleMethodArgumentNotValidException(ConstraintViolationException ex) {

        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        ResponseVO<Object> resultBean = new ResponseVO<>();
        resultBean.setCode(BaseErrorCode.PARAMETER_ERROR.getCode());
        if (constraintViolations != null && !constraintViolations.isEmpty()) {
            ConstraintViolation<?> constraintViolation = (ConstraintViolation<?>) constraintViolations.toArray()[0];
            PathImpl pathImpl = (PathImpl) constraintViolation.getPropertyPath();
            // 读取参数字段，constraintViolation.getMessage() 读取验证注解中的message值
            String paramName = pathImpl.getLeafNode().getName();
            String message = "参数{".concat(paramName).concat("}").concat(constraintViolation.getMessage());
            resultBean.setMsg(message);

            return resultBean;
        }
        resultBean.setMsg(BaseErrorCode.PARAMETER_ERROR.getError() + ex.getMessage());
        return resultBean;
    }

    @ExceptionHandler(ApplicationException.class)
    @ResponseBody
    public Object applicationExceptionHandler(ApplicationException e) {
        // 使用公共的结果类封装返回结果, 这里我指定状态码为
        ResponseVO<Object> resultBean = new ResponseVO<>();
        resultBean.setCode(e.getCode());
        resultBean.setMsg(e.getError());
        return resultBean;
    }

    /**
     * Validator 参数校验异常处理
     */
    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public Object bindExceptionHandler(BindException ex) {
        FieldError err = ex.getFieldError();
        assert err != null;
        String message = "参数{".concat(err.getField()).concat("}").concat(err.getDefaultMessage() + "");
        ResponseVO<Object> resultBean = new ResponseVO<>();
        resultBean.setCode(BaseErrorCode.PARAMETER_ERROR.getCode());
        resultBean.setMsg(message);
        return resultBean;


    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public Object methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex) {
        StringBuilder errorMsg = new StringBuilder();
        BindingResult re = ex.getBindingResult();
        for (ObjectError error : re.getAllErrors()) {
            errorMsg.append(error.getDefaultMessage()).append(",");
        }
        errorMsg.delete(errorMsg.length() - 1, errorMsg.length());

        ResponseVO<Object> resultBean = new ResponseVO<>();
        resultBean.setCode(BaseErrorCode.PARAMETER_ERROR.getCode());
        resultBean.setMsg(BaseErrorCode.PARAMETER_ERROR.getError() + " : " + errorMsg);
        return resultBean;
    }
}
