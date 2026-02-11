package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import generator.domain.address.AddressDTO;
import generator.domain.entity.Address;
import generator.domain.response.Result;

public interface AddressService extends IService<Address> {
    Result<Object> addAddress(Long userId, AddressDTO addressDTO);

    Result<Object> list(Long userId);

    Result<Object> setDefault(Long userId, Long id);
}
