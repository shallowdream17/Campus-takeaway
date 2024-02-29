package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookService {
    void addAddressBook(AddressBook addressBook);

    List<AddressBook> queryAddressList();

    AddressBook queryAddressById(Long id);

    void updateAddressById(AddressBook addressBook);

    void deleteAddressBook(Long id);

    AddressBook queryDefaultAddress();

    void setDefaultAddress(AddressBook addressBook);
}
