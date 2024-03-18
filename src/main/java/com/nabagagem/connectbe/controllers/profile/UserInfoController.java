package com.nabagagem.connectbe.controllers.profile;

import com.nabagagem.connectbe.services.profile.UserInfoService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.nabagagem.connectbe.services.profile.UserInfoService.UserInfo;

@RestController
@AllArgsConstructor
public class UserInfoController {

    private UserInfoService userInfoService;

    @GetMapping("/api/v1/user-info")
    public UserInfo get() {
        return userInfoService.getCurrentUserInfo();
    }
}
