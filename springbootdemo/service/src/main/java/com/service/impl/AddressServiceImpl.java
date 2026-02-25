package com.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.repository.mapper.AddressMapper;
import com.service.AddressService;
import generator.domain.address.AddressDTO;
import generator.domain.entity.Address;
import generator.domain.response.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.constant.AddressConstants.*;

@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {

    @Override
    public Result<Object> addAddress(Long userId, AddressDTO addressDTO) {
        // 1. 登录校验
        if (userId == null) {
            return Result.error(CODE_LOGIN_ERROR, MSG_LOGIN_ERROR);
        }
        // 2. 参数校验
        // 2.1 收货人名称
        if (addressDTO.getReceiverName() == null){
            return Result.error(CODE_RECEIVER_NAME_REQUIRED, MSG_RECEIVER_NAME_REQUIRED);
        }
        // 2.2 收货人手机号
        if (addressDTO.getReceiverPhone() == null){
            return Result.error(CODE_RECEIVER_PHONE_REQUIRED, MSG_RECEIVER_PHONE_REQUIRED);
        }
        // 2.3 省份
        if (addressDTO.getProvince() == null){
            return Result.error(CODE_PROVINCE_REQUIRED, MSG_PROVINCE_REQUIRED);
        }
        // 2.4 城市
        if (addressDTO.getCity() == null){
            return Result.error(CODE_CITY_REQUIRED, MSG_CITY_REQUIRED);
        }
        // 2.5 区县
        if (addressDTO.getDistrict() == null){
            return Result.error(CODE_DISTRICT_REQUIRED, MSG_DISTRICT_REQUIRED);
        }
        // 2.6 详细地址
        if (addressDTO.getDetailAddress() == null){
            return Result.error(CODE_DETAIL_ADDRESS_REQUIRED, MSG_DETAIL_ADDRESS_REQUIRED);
        }


        Address address = new Address();
        BeanUtils.copyProperties(addressDTO, address);

        address.setUserId(userId);
        boolean result = this.save(address);

        if (!result) {
            return Result.error(CODE_ADD_ADDRESS_FAIL, MSG_ADD_ADDRESS_FAIL);
        } else {
            return Result.success(CODE_ADD_ADDRESS_SUCCESS,MSG_ADD_ADDRESS_SUCCESS,addressDTO);
        }
    }

    /**
     * 获取用户地址列表
     *
     * @param userId
     * @return
     */
    @Override
    public Result<Object> list(Long userId) {
        try {
            if (userId == null) {
                return Result.error(CODE_LOGIN_ERROR,  MSG_LOGIN_ERROR);
            }

            List<Address> list;

            list = this.list(new QueryWrapper<Address>().eq("user_id", userId));

            if (list.isEmpty()){
                return Result.success(CODE_LIST_ADDRESS_EMPTY, MSG_LIST_ADDRESS_EMPTY);
            }else {
                return Result.success(CODE_LIST_ADDRESS_SUCCESS, MSG_LIST_ADDRESS_SUCCESS, list);
            }
        } catch (Exception e) {
            //
            log.error("获取用户地址列表失败:{}");
            return Result.error(CODE_LIST_ADDRESS_FAIL, MSG_LIST_ADDRESS_FAIL);
        }
    }

    /**
     * 设置默认地址
     *
     * @param userId
     * @param id
     * @return
     */
    @Override
    public Result<Object> setDefault(Long userId, Long id) {

        if (userId == null) {
            return Result.error(CODE_LOGIN_ERROR, MSG_LOGIN_ERROR);
        }

        if (id == null) {
            return Result.error(CODE_ADDRESS_ID_REQUIRED, MSG_ADDRESS_ID_REQUIRED);
        }

        Address address = this.getById(id);
        if (address == null) {
            return Result.error(CODE_ADDRESS_NOT_FOUND, MSG_ADDRESS_NOT_FOUND);
        }

        if (!address.getUserId().equals(userId)) {
            return Result.error(CODE_ADDRESS_NOT_BELONG_TO_USER, MSG_ADDRESS_NOT_BELONG_TO_USER );
        }

        // 查询当前用户默认地址
        LambdaQueryWrapper<Address> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Address::getUserId, userId);
        queryWrapper.eq(Address::getIsDefault, 1);

        Address defaultAddress = this.getOne(queryWrapper);
        // 将原来的默认地址设为非默认
        if (defaultAddress != null) {
            defaultAddress.setIsDefault(0);
            this.updateById(defaultAddress);
        }

        // 将当前地址设为默认
        LambdaQueryWrapper<Address> newQueryWrapper = new LambdaQueryWrapper<>();
        newQueryWrapper.eq(Address::getId, id);
        Address newDefaultAddress = this.getOne(newQueryWrapper);
        newDefaultAddress.setIsDefault(1);
        boolean update = this.update(newDefaultAddress, newQueryWrapper);


        //
        if (!update) {
            return Result.error(CODE_SET_DEFAULT_ADDRESS_FAIL, MSG_SET_DEFAULT_ADDRESS_FAIL);
        } else {
            return Result.success(CODE_SET_DEFAULT_ADDRESS_SUCCESS, MSG_SET_DEFAULT_ADDRESS_SUCCESS);
        }
    }
}
