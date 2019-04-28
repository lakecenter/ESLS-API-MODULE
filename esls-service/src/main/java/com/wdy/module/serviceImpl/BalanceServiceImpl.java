package com.wdy.module.serviceImpl;

import com.wdy.module.dao.BalanceDao;
import com.wdy.module.entity.Balance;
import com.wdy.module.service.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("BalanceService")
public class BalanceServiceImpl extends BaseServiceImpl<Balance> implements BalanceService<Balance> {
    @Autowired
    private BalanceDao balanceDao;
    @Override
    public List<Balance> findAll() {
        return balanceDao.findAll();
    }

    @Override
//    @Cacheable(value = RedisConstant.CACHE_LOGS)
    public List<Balance> findAll(Integer page, Integer count) {
        List<Balance> content = balanceDao.findAll(PageRequest.of(page, count, Sort.Direction.DESC, "id")).getContent();
        return content;
    }

    @Override
//    @Cacheable(value = RedisConstant.CACHE_LOGS)
    public Balance saveOne(Balance balance) {
        return balanceDao.save(balance);
    }

    @Override
//    @Cacheable(value = RedisConstant.CACHE_LOGS)
    public Balance findById(Long id) {
        Optional<Balance> balance = balanceDao.findById(id);
        if(balance.isPresent())
            return balance.get();
        return null;
    }

    @Override
    public boolean deleteById(Long id) {
        try{
            balanceDao.deleteById(id);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }
}
