package com.wdy.module.service;

import com.wdy.module.common.request.RequestBean;
import com.wdy.module.common.response.ResponseBean;
import com.wdy.module.common.response.ResultBean;
import com.wdy.module.entity.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface TagService<T> extends Service<T> {
    List<Tag> findAll();

    List<Tag> findAll(Integer page, Integer count);

    List<Tag> findByRouterId(Long routerId);

    Tag findByTagAddress(String tagAddress);

    Tag findByBarCode(String barCode);

    // 添加标签
    Tag saveOne(Tag tag);

    // 获取指定ID的标签
    Optional<Tag> findById(Long id);

    // 删除指定ID的标签
    boolean deleteById(Long id);

    // 对标签进行批量更新
    ResponseBean flushTags(RequestBean requestBean);

    // 对路由器下的标签进行批量刷新
    ResponseBean flushTagsByRouter(RequestBean requestBean);

    ResponseBean flushTagsByCycle(RequestBean requestBean, Integer mode);

    // 对标签进行批量巡检
    ResponseBean scanTags(RequestBean requestBean);

    // 对路由器下的标签进行批量巡检
    ResponseBean scanTagsByRouter(RequestBean requestBean);

    ResponseBean scanTagsByCycle(RequestBean requestBean, Integer mode);

    // 禁用或启用指定标签
    ResponseBean changeStatus(RequestBean requestBean, Integer mode);

    // 闪灯或结束闪灯
    ResponseBean changeLightStatus(RequestBean requestBean, Integer mode);

    ResponseBean changeLightStatusByRouter(RequestBean requestBean, Integer mode);

    // 更新指定路由器下的所有样式
    ResponseBean updateTagStyle(Tag tag, boolean isWaittingLong);

    // 对指定的标签或路由器发出标签移除命令
    ResponseBean removeTagCommand(RequestBean requestBean, Integer mode);

    // 绑定和解绑商品和标签
    ResponseEntity<ResultBean> bindGoodAndTag(String sourceArgs1, String ArgsString1, String sourceArgs2, String ArgsString2, String mode);

    // 价签更换样式
    ResponseEntity<ResultBean> updateTagStyleById(long tagId, long styleId, Integer mode);

    ResponseBean testInkScreen(RequestBean requestBean, Integer type, Integer mode);
}
