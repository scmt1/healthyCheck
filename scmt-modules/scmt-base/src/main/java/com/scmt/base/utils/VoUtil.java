package com.scmt.base.utils;

import com.scmt.base.vo.MenuVo;
import com.scmt.core.entity.Permission;
import cn.hutool.core.bean.BeanUtil;

/**
 * @author Exrick
 */
public class VoUtil {

    public static MenuVo permissionToMenuVo(Permission p) {

        MenuVo menuVo = new MenuVo();
        BeanUtil.copyProperties(p, menuVo);
        return menuVo;
    }
}
