package com.wdy.module.service;


import com.wdy.module.common.request.RequestBean;
import com.wdy.module.common.response.ResponseBean;
import com.wdy.module.entity.Shop;

import java.util.List;
import java.util.Optional;

public interface ShopService extends Service{
    List<Shop> findAll();
    List<Shop> findAll(Integer page, Integer count);
    Shop saveOne(Shop shop);
    Optional<Shop> findById(Long id);
    boolean deleteById(Long id);
    ResponseBean tagsByCycle(RequestBean requestBean, Integer mode);
}
