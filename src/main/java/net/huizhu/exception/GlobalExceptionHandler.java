package net.huizhu.exception;


import cn.hutool.core.date.DateException;
import lombok.extern.slf4j.Slf4j;
import net.huizhu.common.api.CommonResult;
import net.huizhu.common.api.ResultCode;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.time.format.DateTimeParseException;

@Slf4j
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
    @ExceptionHandler(value = Exception.class)  //处理所有的异常
    public CommonResult<String> exceptionHandle(HttpServletRequest request, Exception e){
        e.printStackTrace();

        //将异常进行分类，不同的异常进行不同的处理
        if(e instanceof GlobalException){
            GlobalException ex=(GlobalException) e;
            return CommonResult.failed(ex.getResultCode());
        }else if (e instanceof HttpMessageNotReadableException) {
            return CommonResult.failed("无法解析json");
        } else if (e instanceof DateException) {
            return CommonResult.failed("时间格式错误");
        } else if (e instanceof DateTimeParseException) {
            return CommonResult.failed("时间格式转换错误");
        } else {
            return CommonResult.failed(ResultCode.FAILED);
        }
    }
}