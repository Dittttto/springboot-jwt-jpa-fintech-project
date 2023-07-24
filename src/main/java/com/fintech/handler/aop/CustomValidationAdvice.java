package com.fintech.handler.aop;

import com.fintech.exception.CustomValidationException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

@Component
@Aspect
public class CustomValidationAdvice {

    @Pointcut("within(com.fintech.web.controller.UserApiController)")
    public void userController() {}
    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void postMapping() {}
    @Pointcut("@annotation(org.springframework.web.bind.annotation.PutMapping)")
    public void putMapping() {}

    @Around("userController() && (postMapping() || putMapping())")
    public Object validationAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object[] args = proceedingJoinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof BindingResult bindingResult) {
                if (bindingResult.hasErrors()) {
                    Map<String, String> errMap = new HashMap<>();
                    for (FieldError fieldError : bindingResult.getFieldErrors()) {
                        errMap.put(fieldError.getField(), fieldError.getDefaultMessage());
                    }

                    throw new CustomValidationException("유효성 검사에 실패 했습니다.", errMap);
                }

                break;
            }
        }

        return proceedingJoinPoint.proceed();
    }
}
