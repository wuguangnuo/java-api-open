package com.xyj.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xyj.api.dto.common.AreaDto;
import com.xyj.api.dto.common.CityDto;
import com.xyj.api.dto.test.TestDto;
import com.xyj.api.enums.common.CityAreaEnum;
import com.xyj.api.enums.common.DictionaryEnum;
import com.xyj.api.enums.common.OrderStateEnum;
import com.xyj.api.model.ApiRes;
import com.xyj.api.model.SelectListItem;
import com.xyj.api.service.common.CommonService;
import com.xyj.api.entity.CityArea;
import com.xyj.api.entity.OrderLabel;
import com.xyj.api.mapper.CityAreaMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@Api(value = "通用控制器", tags = {"通用控制器"})
@RequestMapping(value = "")
public class CommonController {

    private final CityAreaMapper cityAreaMapper;
    private final CommonService commonService;

    @Autowired
    public CommonController(CityAreaMapper cityAreaMapper, CommonService commonService) {
        this.cityAreaMapper = cityAreaMapper;
        this.commonService = commonService;
    }

    @GetMapping(value = "")
    @ApiOperation(value = "ROOT")
    public String root() {
        log.info("测试日志 --- Log.info()");
        String style = "text-decoration:none;font-family:Courier New;font-weight:700;font-size:32px";
        return "<a href='/swagger-ui.html' style='" + style + "'>JAVA API Framework</a>";
    }

    @GetMapping(value = "common/getOrderStateEnum")
    @ApiOperation(value = "获取订单状态枚举")
    public ApiRes<List<SelectListItem>> getOrderStateEnum() {
        List<SelectListItem> data = new ArrayList<>();
        for (OrderStateEnum oEnum : OrderStateEnum.values()) {
            SelectListItem temp = new SelectListItem();
            temp.setText(oEnum.toString());
            temp.setValue(oEnum.getValue() + "");
            data.add(temp);
        }
        if (data.size() != 0) {
            return ApiRes.suc("获取成功", data);
        } else {
            return ApiRes.fail("查询为空");
        }
    }

    @GetMapping(value = "common/getCityArea")
    @ApiOperation(value = "获取服务城市和地区")
    public ApiRes<List<CityDto>> getCityArea() {
        List<CityDto> data = commonService.getCityArea();

        if (data != null) {
            return ApiRes.suc("获取成功", data);
        } else {
            return ApiRes.fail("查询为空");
        }
    }

    @GetMapping(value = "common/getAreaByCityId")
    @ApiOperation(value = "根据城市ID获取区域")
    public ApiRes<List<AreaDto>> getAreaByCityId(int cityId) {
        List<AreaDto> data = commonService.getAreaByCityId(cityId);

        if (data != null) {
            return ApiRes.suc("获取成功", data);
        } else {
            return ApiRes.fail("查询为空");
        }
    }

    @GetMapping(value = "common/getOrderLabel")
    @ApiOperation(value = "获取订单标签列表")
    public ApiRes<List<OrderLabel>> getOrderLabel() {
        List<OrderLabel> data = commonService.getOrderLabel();

        if (data != null) {
            return ApiRes.suc("获取成功", data);
        } else {
            return ApiRes.fail("查询为空");
        }
    }

    @GetMapping(value = "common/getBaomuWorkModel")
    @ApiOperation(value = "获取保姆工作模式")
    public ApiRes<List<SelectListItem>> getBaomuWorkModel() {
        List<SelectListItem> data = commonService.getDictionary(DictionaryEnum.保姆工作模式.getValue());

        if (data.size() != 0) {
            return ApiRes.suc("获取成功", data);
        } else {
            return ApiRes.fail("查询为空");
        }
    }

    @PostMapping(value = "/pagination")
    @ApiOperation(value = "分页")
    public ApiRes<IPage<CityArea>> pagination(@RequestBody TestDto dto) {
        IPage cityAreaIPage = cityAreaMapper.selectPage(
                new Page(dto.getPageIndex(), dto.getPageSize()),
                new QueryWrapper<CityArea>().lambda()
                        .eq(CityArea::getState, CityAreaEnum.正常.getValue())
        );
        return ApiRes.suc(cityAreaIPage);
    }
}
