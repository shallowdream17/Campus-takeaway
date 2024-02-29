package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AddressBookMapper {

    List<AddressBook> queryAddressList(Long userId);


    void addAddressBook(AddressBook addressBook);

    AddressBook queryAddressById(Long id);

    void updateAddressById(AddressBook addressBook);

    @Delete("delete from address_book where id = #{id}")
    void deleteAddressBook(Long id);

    AddressBook queryDefaultAddress(Long userId);

    @Update("update address_book set is_default = #{status} where id = #{id}")
    void setDefaultAddress(Long id,Integer status);
}
