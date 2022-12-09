package com.shuozi.reggie.common;


import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice(annotations = {RestController.class, Controller.class})
@Log4j2
@ResponseBody
public class GlobalExceptionHandler
{

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex)
    {
        log.info(ex.getMessage());
        if (ex.getMessage().contains("Duplicate entry"))
        {
            String[] split = ex.getMessage().split(" ");
            String s = split[2] +"已经存在了";
            return R.error(s);
        }
        return R.error("系统繁忙");
    }


    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex)
    {
        log.info(ex.getMessage());

        return R.error(ex.getMessage());
    }

}
