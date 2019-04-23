package com.xyj.api.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.xyj.api.dto.account.LoginDto;
import com.xyj.api.dto.account.LoginPhoneDto;
import com.xyj.api.dto.account.RegisterDto;
import com.xyj.api.dto.account.SendSMSDto;
import com.xyj.api.model.ApiRes;
import com.xyj.api.model.account.EmployeeView;
import com.xyj.api.model.account.LoginModel;
import com.xyj.api.service.account.AccountService;
import com.xyj.api.utils.HttpUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(value = "账户相关", tags = "账户相关")
@RestController
@RequestMapping("account")
public class AccountController {
//    @Value("${apiHost}")
//    private String apiHost;

    private static final String apiHost = "http://xxx.xxx.com/api";

    private final AccountService loginService;
    private final HttpUtils httpUtils;

    @Autowired
    public AccountController(AccountService loginService, HttpUtils httpUtils) {
        this.loginService = loginService;
        this.httpUtils = httpUtils;
    }

    @PostMapping("/login")
    @ApiOperation(value = "登录")
    public ApiRes<LoginModel> login(@RequestBody @Valid LoginDto dto) {
        if (Strings.isNullOrEmpty(dto.getAccount()) || Strings.isNullOrEmpty(dto.getPassword()))
            return ApiRes.fail("输入有误");

        return loginService.login(dto);
    }

    @PostMapping("/register")
    @ApiOperation(value = "注册接口")
    public ApiRes<EmployeeView> Register(@RequestBody @Valid RegisterDto dto) {
        if (dto.getRegisterType() != 0 && dto.getRegisterType() != 1)
            return ApiRes.err("注册类型错误");
        if (dto.getRegisterType() == 1) {
            if (Strings.isNullOrEmpty(dto.getBaomuWorkModel())) {
                return ApiRes.err("保姆工作模式必填");
            }
            if (dto.getBaomuAbility().size() == 0) {
                return ApiRes.err("保姆能力必填!");
            }
            if (dto.getWorkingLifeDate() == null) {
                return ApiRes.err("从业起始时间必填");
            }
        }

        return loginService.register(dto);
    }

    @PostMapping(value = "sendMsg")
    @ApiOperation(value = "发送短信")
    public ApiRes<String> sendMsg(@RequestBody @Valid SendSMSDto dto) {
        if (Strings.isNullOrEmpty(dto.getTel())) return ApiRes.fail("手机号必填");
        String url = apiHost + "/MsgSend/SendSms";
        dto.setRandom(System.currentTimeMillis() / 1000 + "");

        String data = httpUtils.httpPostJson(JSON.toJSONString(dto), url);

        return httpUtils.smoothString(data);
    }

    @PostMapping(value = "loginByPhone")
    @ApiOperation(value = "手机号登录")
    public ApiRes<LoginModel> loginByPhone(@RequestBody @Valid LoginPhoneDto dto) {
        if (Strings.isNullOrEmpty(dto.getPhoneNumber()) || Strings.isNullOrEmpty(dto.getPhoneCode()))
            return ApiRes.fail("数据不完整");

        return loginService.loginByPhone(dto);
    }
}
