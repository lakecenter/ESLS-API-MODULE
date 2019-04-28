package com.wdy.module.serviceImpl;
import com.wdy.module.common.exception.ResultEnum;
import com.wdy.module.dao.CycleJobDao;
import com.wdy.module.entity.CycleJob;
import com.wdy.module.common.exception.ServiceException;
import com.wdy.module.service.CycleJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("CycleJobService")
public class CycleJobServiceImpl extends BaseServiceImpl implements CycleJobService {
    @Autowired
    private CycleJobDao cycleJobDao;
    @Override
    public List<CycleJob> findAll() {
        return cycleJobDao.findAll();
    }

    @Override
    public List<CycleJob> findAll(Integer page, Integer count) {
        List<CycleJob> content = cycleJobDao.findAll(PageRequest.of(page, count, Sort.Direction.DESC, "id")).getContent();
        return content;
    }

    @Override
    public CycleJob findByMode(Integer mode) {
        return cycleJobDao.findByMode(mode);
    }

    @Override
    public CycleJob saveOne(CycleJob cycleJob) {
        return cycleJobDao.save(cycleJob);
    }

    @Override
    public CycleJob findById(Long id) {
        Optional<CycleJob> cycleJob = cycleJobDao.findById(id);
        if(!cycleJob.isPresent())
            throw new ServiceException(ResultEnum.ID_TO_INFO_NOT_EXIST);
        return cycleJobDao.findById(id).get();
    }

    @Override
    public boolean deleteById(Long id) {
        try{
            cycleJobDao.deleteById(id);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }
}
