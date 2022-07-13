package com.scmt.base.dao;

import com.scmt.base.entity.DictData;
import com.scmt.core.base.ScmtBaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 字典数据数据处理层
 * @author Exrick
 */
public interface DictDataDao extends ScmtBaseDao<DictData, String> {

    /**
     * 通过dictId和状态获取
     * @param dictId
     * @param status
     * @return
     */
    List<DictData> findByDictIdAndStatusOrderBySortOrder(String dictId, Integer status);

    /**
     * 通过dictId删除
     * @param dictId
     */
    @Modifying
    @Query("delete from DictData d where d.dictId = ?1")
    void deleteByDictId(String dictId);
}
