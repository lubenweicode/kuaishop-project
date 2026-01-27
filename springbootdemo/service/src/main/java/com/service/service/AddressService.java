package com.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import generator.domain.address.AddressDTO;
import generator.domain.demo.Result;
import generator.domain.entity.Address;

public interface AddressService extends IService<Address>{
    Result<Object> addAddress(Long userId, AddressDTO addressDTO);

    Result<Object> list(Long userId);

    Result<Object> setDefault(Long userId, Long id);
}
