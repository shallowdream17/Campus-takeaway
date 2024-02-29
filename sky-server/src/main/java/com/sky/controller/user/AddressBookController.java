package com.sky.controller.user;


import com.sky.constant.MessageConstant;
import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(tags = "地址簿相关接口")
@Slf4j
@RequestMapping("/user/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    @PostMapping()
    @ApiOperation("新增地址")
    public Result addAddressBook(@RequestBody AddressBook addressBook){
        log.info("新增地址{}",addressBook);
        addressBookService.addAddressBook(addressBook);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("查询所有地址")
    public Result queryAddressList(){
        List<AddressBook> addressBookList = addressBookService.queryAddressList();
        return Result.success(addressBookList);
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询地址")
    public Result queryAddressById(@PathVariable Long id){
        AddressBook addressBook = addressBookService.queryAddressById(id);
        return Result.success(addressBook);
    }

    @PutMapping()
    @ApiOperation("根据id修改地址")
    public Result updateAddressById(@RequestBody AddressBook addressBook){
        addressBookService.updateAddressById(addressBook);
        return Result.success();
    }

    @DeleteMapping()
    @ApiOperation("根据id删除地址")
    public Result deleteAddressBook(Long id){
        addressBookService.deleteAddressBook(id);
        return Result.success();
    }

    @GetMapping("/default")
    @ApiOperation("查询默认地址")
    public Result queryDefaultAddress(){
        AddressBook addressBook = addressBookService.queryDefaultAddress();
        if(addressBook != null){
            // TODO 应该只有一个默认地址？
            return Result.success(addressBook);
        }
        return Result.error("未查询到默认地址");
    }

    @PutMapping("/default")
    @ApiOperation("设置默认地址")
    public Result setDefaultAddress(@RequestBody AddressBook addressBook){
        addressBookService.setDefaultAddress(addressBook);
        return Result.success();
    }

}
