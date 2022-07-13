package com.scmt.core.dao;

import com.scmt.core.base.ScmtBaseDao;
import com.scmt.core.entity.Permission;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 权限数据处理层
 * @author Exrick
 */
public interface PermissionDao extends ScmtBaseDao<Permission, String> {

    /**
     * 通过层级查找
     * 默认升序
     * @param level
     * @return
     */
    List<Permission> findByLevelOrderBySortOrder(Integer level);

    /**
     * 通过parendId查找
     * @param parentId
     * @return
     */
    List<Permission> findByParentIdOrderBySortOrder(String parentId);

    /**
     * 通过类型和状态获取
     * @param type
     * @param status
     * @return
     */
    List<Permission> findByTypeAndStatusOrderBySortOrder(Integer type, Integer status);

    /**
     * 通过名称获取
     * @param title
     * @return
     */
    List<Permission> findByTitle(String title);

    /**
     * 模糊搜索
     * @param title
     * @return
     */
    List<Permission> findByTitleLikeOrderBySortOrder(String title);

    @Query(value = "SELECT * FROM (SELECT @r AS _id, (SELECT @r \\:= parent_id FROM t_permission WHERE id = _id LIMIT 1) AS parent_id,  @l \\:= @l + 1 AS lvl FROM (SELECT @r \\:= ?1, @l \\:= 0) vars, t_permission h WHERE @r <> 0) T1 JOIN t_permission T2 ON T1._id = T2.id WHERE T1.parent_id = 0 ORDER BY T1.lvl DESC LIMIT 1", nativeQuery = true)
    Permission queryFirstParentData(String id);
}
