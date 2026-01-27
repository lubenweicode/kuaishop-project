package com.controller;

import com.service.service.AddressService;
import generator.domain.address.AddressDTO;
import generator.domain.context.UserContext;
import generator.domain.demo.Result;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    /**
     * 添加地址
     * @param addressDTO
     * @return
     */
    @PostMapping
    public Result<Object> addAddress(@RequestBody AddressDTO addressDTO){
        Long userId = UserContext.getUserId();
        return addressService.addAddress(userId,addressDTO);
    }

    /**
     * 获取地址列表
     * @return
     */
    @GetMapping
    public Result<Object> list(){
        Long userId = UserContext.getUserId();
        return addressService.list(userId);
    }

    /**
     * 设置默认地址
     * @param id
     * @return
     */
    @PutMapping("/{id}/default")
    public Result<Object> setDefault(@PathVariable Long id){
        Long userId = UserContext.getUserId();
        return addressService.setDefault(userId,id);
    }

}
