package net.huizhu.exception;


import net.huizhu.common.api.ResultCode;

public class GlobalException  extends RuntimeException{
    private static final long serialVersionUID = 1L;
    private ResultCode resultCode;
    public GlobalException(ResultCode resultCode){
        super(resultCode.toString());
        this.resultCode=resultCode;
    }
    public ResultCode getResultCode(){
        return resultCode;
    }
}

