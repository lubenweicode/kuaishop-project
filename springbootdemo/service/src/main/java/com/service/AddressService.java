package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import domain.address.AddressDTO;
import domain.entity.Address;
import domain.response.Result;

public interface AddressService extends IService<Address> {
    Result<Object> addAddress(Long userId, AddressDTO addressDTO);

    Result<Object> list(Long userId);

    Result<Object> setDefault(Long userId, Long id);
}
