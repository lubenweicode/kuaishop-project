package com.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mapper.AddressMapper;
import com.service.AddressService;
import generator.domain.address.AddressDTO;
import generator.domain.demo.Result;
import generator.domain.entity.Address;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {

    @Override
    public Result<Object> addAddress(Long userId, AddressDTO addressDTO) {

        if (userId == null){
            return Result.error(401,"请先登录");
        }

        if (addressDTO.getReceiverName() == null){
            return Result.error(400,"收货人不能为空");
        }

        if (addressDTO.getReceiverPhone() == null){
            return Result.error(400,"收货电话不能为空");
        }

        if (addressDTO.getProvince() == null){
            return Result.error(400,"省份不能为空");
        }

        if (addressDTO.getCity() == null){
            return Result.error(400,"城市不能为空");
        }

        if (addressDTO.getDistrict() == null){
            return Result.error(400,"区县不能为空");
        }

        if (addressDTO.getDetailAddress() == null){
            return Result.error(400,"详细地址不能为空");
        }

        Address address = new Address();
        BeanUtils.copyProperties(addressDTO,address);

        address.setUserId(userId);
        boolean result = this.save(address);

        if (!result){
            return Result.error(500,"添加地址失败");
        }else{
            return Result.success(addressDTO);
        }
    }

    /**
     * 获取用户地址列表
     * @param userId
     * @return
     */
    @Override
    public Result<Object> list(Long userId) {
        if (userId == null){
            return Result.error(401,"请先登录");
        }

        List< Address> list;

        list = this.list(new QueryWrapper<Address>().eq("user_id",userId));

        if(list.isEmpty()){
            return Result.success(list);
        }else{
            return Result.success(list);
        }
    }

    /**
     * 设置默认地址
     * @param userId
     * @param id
     * @return
     */
    @Override
    public Result<Object> setDefault(Long userId, Long id) {

        if (userId == null){
            return Result.error(401,"请先登录");
        }

        if (id == null){
            return Result.error(400,"地址ID不能为空");
        }

        Address address = this.getById(id);
        if (address == null){
            return Result.error(400,"地址不存在");
        }

        if (!address.getUserId().equals(userId)){
            return Result.error(400,"地址不属于当前用户");
        }

        // 查询当前用户默认地址
        LambdaQueryWrapper<Address> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Address::getUserId,userId);
        queryWrapper.eq(Address::getIsDefault,1);

        Address defaultAddress = this.getOne(queryWrapper);
        // 将原来的默认地址设为非默认
        if (defaultAddress != null){
            defaultAddress.setIsDefault(0);
            this.updateById(defaultAddress);
        }

        // 将当前地址设为默认
        LambdaQueryWrapper<Address> newQueryWrapper = new LambdaQueryWrapper<>();
        newQueryWrapper.eq(Address::getId,id);
        Address newDefaultAddress = this.getOne(newQueryWrapper);
        newDefaultAddress.setIsDefault(1);
        boolean update = this.update(newDefaultAddress,newQueryWrapper);

        if (!update){
            return Result.error(500,"设置默认地址失败");
        }else{
            return Result.success();
        }
    }
}
