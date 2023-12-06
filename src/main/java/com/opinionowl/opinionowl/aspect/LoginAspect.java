package com.opinionowl.opinionowl.aspect;

import com.opinionowl.opinionowl.controllers.CookieController;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Class LoginAspect for defining an aspect for all login checks within APIController and PageController.
 */
@Aspect
@Component
public class LoginAspect {

    /**
     * Method for defining the annotation needsLogin.
     * @param needsLogin
     */
    @Pointcut("@annotation(needsLogin)")
    public void callAt(NeedsLogin needsLogin) {
    }

    /**
     * Method for checking the type of the NeedsLogin annotation and proceeding with the actual login check through the aspect.
     * @param pjp A ProceedingJoinPoint pjp.
     * @param needsLogin A NeedsLogin needsLogin.
     * @return The type of the aspect annotation
     * @throws Throwable
     */
    @Around("callAt(needsLogin)")
    public Object around(ProceedingJoinPoint pjp, NeedsLogin needsLogin) throws Throwable {
        Object[] args = pjp.getArgs();
        HttpServletRequest request = null;
        for (int argIndex = 0; argIndex < args.length; argIndex++) {
            if (args[argIndex] instanceof HttpServletRequest) {
                request = (HttpServletRequest) args[argIndex];
            }
        }
        if (request == null) {
            return "redirect:/";
        }
        String res = CookieController.getUsernameFromCookie(request);
        if (res == null) {
            return getReturnType(needsLogin);
        }
        return pjp.proceed();
    }

    /**
     * Method for getting the return type for the around method.
     * @param needsLogin
     * @return An html, string or int type
     */
    public Object getReturnType(NeedsLogin needsLogin) {
        if (needsLogin.type().equals("html")) {
            return "redirect:/";
        } else if (needsLogin.type().equals("string")) {
            return "";
        } else if (needsLogin.type().equals("int")) {
            return 400;
        } else {
            return null;
        }
    }
}

