package com.xyj.api.service.common;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xyj.api.dto.common.AreaDto;
import com.xyj.api.dto.common.CityDto;
import com.xyj.api.enums.common.CityAreaEnum;
import com.xyj.api.model.SelectListItem;
import com.xyj.api.entity.CityArea;
import com.xyj.api.entity.Dictionary;
import com.xyj.api.entity.OrderLabel;
import com.xyj.api.mapper.AdvertisingImageMapper;
import com.xyj.api.mapper.CityAreaMapper;
import com.xyj.api.mapper.DictionaryMapper;
import com.xyj.api.mapper.OrderLabelMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
@Slf4j
public class CommonService {

    private final AdvertisingImageMapper advertisingImageMapper;
    private final DictionaryMapper dictionaryMapper;
    private final CityAreaMapper cityAreaMapper;
    private final OrderLabelMapper orderLabelMapper;

    @Autowired
    public CommonService(CityAreaMapper cityAreaMapper, OrderLabelMapper orderLabelMapper, AdvertisingImageMapper advertisingImageMapper, DictionaryMapper dictionaryMapper) {
        this.cityAreaMapper = cityAreaMapper;
        this.orderLabelMapper = orderLabelMapper;
        this.advertisingImageMapper = advertisingImageMapper;
        this.dictionaryMapper = dictionaryMapper;
    }

    /**
     * 获取服务城市和地区
     *
     * @return
     */
    public List<CityDto> getCityArea() {
        QueryWrapper<CityArea> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(CityArea::getState, CityAreaEnum.正常.getValue());
        List<CityArea> cityAreas = cityAreaMapper.selectList(queryWrapper);

        List<CityDto> data = new ArrayList<CityDto>();
        for (CityArea city : cityAreas) {
            if (city.getCityId() == 0) {
                List<AreaDto> areas = new ArrayList<AreaDto>();
                for (CityArea area : cityAreas) {
                    if (area.getCityId() == city.getId()) {
                        AreaDto areaDto = new AreaDto();
                        areaDto.setId(area.getId());
                        areaDto.setName(area.getName());
                        areaDto.setAreaCode(area.getAreaCode());
                        areas.add(areaDto);
                    }
                }
                CityDto cityDto = new CityDto();
                cityDto.setId(city.getId());
                cityDto.setName(city.getName());
                cityDto.setAreas(areas);
                data.add(cityDto);
            }
        }
        return data;
    }

    /**
     * 获取订单标签列表
     *
     * @return
     */
    public List<OrderLabel> getOrderLabel() {
        return orderLabelMapper.selectList(new QueryWrapper<>());
    }

    /**
     * 字典表获取数据
     *
     * @return
     */
    public List<SelectListItem> getDictionary(Integer typeValue) {
        List<Dictionary> dictionaries = dictionaryMapper.selectList(
                new QueryWrapper<Dictionary>().lambda()
                        .eq(Dictionary::getTypeValue, typeValue)
        );

        List<SelectListItem> data = new LinkedList<>();
        for (Dictionary d : dictionaries) {
            SelectListItem temp = new SelectListItem();
            temp.setValue(d.getId().toString());
            temp.setText(d.getText());
            data.add(temp);
        }
        return data;
    }

    /**
     * 根据城市ID获取区域
     *
     * @param cityId
     * @return
     */
    public List<AreaDto> getAreaByCityId(int cityId) {
        List<CityArea> cityAreas = cityAreaMapper.selectList(
                new QueryWrapper<CityArea>().lambda()
                        .eq(CityArea::getCityId, cityId)
                        .eq(CityArea::getState, CityAreaEnum.正常.getValue())
        );
        if (cityAreas.size() == 0) return null;
        List<AreaDto> data = new ArrayList<>();
        for (CityArea item : cityAreas) {
            AreaDto temp = new AreaDto();
            temp.setId(item.getId());
            temp.setName(item.getName());
            temp.setAreaCode(item.getAreaCode());
            data.add(temp);
        }
        return data;
    }
}