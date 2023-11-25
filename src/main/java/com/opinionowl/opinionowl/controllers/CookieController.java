package com.opinionowl.opinionowl.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;

public class CookieController {
    /**
     * Helper function to get the cookie information and add it to the model
     * @param model Model, the client Model
     * @param request An HttpServletRequest request.
     */
    public static void setUsernameCookie(Model model, HttpServletRequest request) {
        Cookie[] cookie = request.getCookies();
        String userid = null;
        if (cookie != null) {
            for (Cookie c : cookie) {
                if (c.getName().equals("userId") && c.getMaxAge() != 0) {
                    userid = c.getValue();
                }
            }
        }
        model.addAttribute("userId", userid);
    }

    public static String getUserIdFromCookie(HttpServletRequest request){
        Cookie[] cookie = request.getCookies();
        String userid = null;
        if (cookie != null) {
            for (Cookie c : cookie) {
                if (c.getName().equals("userId") && c.getMaxAge() != 0) {
                    userid = c.getValue();
                }
            }
        }
        return userid;
    }
}
