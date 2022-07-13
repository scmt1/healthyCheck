package com.scmt.core.config.security;

import com.scmt.core.common.constant.MemberConstant;
import com.scmt.core.entity.Member;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Exrickx
 */
@Slf4j
public class SecurityMemberDetails extends Member implements UserDetails {

    private static final long serialVersionUID = 1L;

    private String permissions;

    public SecurityMemberDetails(Member member) {

        if (member != null) {
            this.setUsername(member.getUsername());
            this.setPassword(member.getPassword());
            this.setStatus(member.getStatus());

            this.permissions = member.getPermissions();
        }
    }

    /**
     * 添加用户拥有的权限和角色
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<GrantedAuthority> authorityList = new ArrayList<>();
        if (StrUtil.isBlank(permissions)) {
            return authorityList;
        }
        String[] as = permissions.split(",");
        for (String a : as) {
            authorityList.add(new SimpleGrantedAuthority(a));
        }
        return authorityList;
    }

    /**
     * 账户是否过期
     * @return
     */
    @Override
    public boolean isAccountNonExpired() {

        return true;
    }

    /**
     * 是否禁用
     * @return
     */
    @Override
    public boolean isAccountNonLocked() {

        return MemberConstant.MEMBER_STATUS_LOCK.equals(this.getStatus()) ? false : true;
    }

    /**
     * 密码是否过期
     * @return
     */
    @Override
    public boolean isCredentialsNonExpired() {

        return true;
    }

    /**
     * 是否启用
     * @return
     */
    @Override
    public boolean isEnabled() {

        return MemberConstant.MEMBER_STATUS_NORMAL.equals(this.getStatus()) ? true : false;
    }
}
