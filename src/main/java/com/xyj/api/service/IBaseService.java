package com.xyj.api.service;

import com.xyj.api.model.UserData;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public interface IBaseService {
    default UserData getUserData() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        UserData userData = (UserData) request.getAttribute("userData");
        return userData;
    }
}
