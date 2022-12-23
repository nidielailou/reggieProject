package com.shuozi.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shuozi.reggie.common.BaseContext;
import com.shuozi.reggie.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
