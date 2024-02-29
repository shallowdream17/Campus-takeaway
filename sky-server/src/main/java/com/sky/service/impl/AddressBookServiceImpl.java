package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AddressBookServiceImpl implements AddressBookService {

    @Autowired
    private AddressBookMapper addressBookMapper;

    /**
     * 新增地址
     * @param addressBook
     */
    @Override
    public void addAddressBook(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(0);
        addressBookMapper.addAddressBook(addressBook);
    }

    /**
     * 查询所有地址
     * @return
     */
    @Override
    public List<AddressBook> queryAddressList() {
        Long userId = BaseContext.getCurrentId();
        List<AddressBook> addressBookList = addressBookMapper.queryAddressList(userId);
        return addressBookList;
    }

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    @Override
    public AddressBook queryAddressById(Long id) {
        AddressBook addressBook = addressBookMapper.queryAddressById(id);
        return addressBook;
    }

    /**
     * 根据id修改地址
     * @param addressBook
     */
    @Override
    public void updateAddressById(AddressBook addressBook) {
        addressBookMapper.updateAddressById(addressBook);
    }

    /**
     * 根据id删除地址
     * @param id
     */
    @Override
    public void deleteAddressBook(Long id) {
        addressBookMapper.deleteAddressBook(id);
    }

    /**
     * 查询默认地址
     * @return
     */
    @Override
    public AddressBook queryDefaultAddress() {
        Long userId = BaseContext.getCurrentId();
        AddressBook addressBook = addressBookMapper.queryDefaultAddress(userId);
        return addressBook;
    }

    /**
     * 设置默认收货地址
     * @param id
     */
    @Override
    public void setDefaultAddress(AddressBook addressBook) {
        //先将原来的默认地址取消掉
        Long userId = BaseContext.getCurrentId();
        AddressBook oldAddress = addressBookMapper.queryDefaultAddress(userId);
        if(oldAddress != null){
            addressBookMapper.setDefaultAddress(oldAddress.getId(),0);
        }
        addressBookMapper.setDefaultAddress(addressBook.getId(),1);
    }


}
