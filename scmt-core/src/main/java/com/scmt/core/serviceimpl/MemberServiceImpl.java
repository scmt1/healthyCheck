package com.scmt.core.serviceimpl;

import com.scmt.core.common.vo.SearchVo;
import com.scmt.core.dao.MemberDao;
import com.scmt.core.entity.Member;
import com.scmt.core.service.MemberService;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 会员接口实现
 * @author Exrick
 */
@Slf4j
@Service
@Transactional
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberDao memberDao;

    @Override
    public MemberDao getRepository() {
        return memberDao;
    }

    @Override
    public Page<Member> findByCondition(Member member, SearchVo searchVo, Pageable pageable) {

        return memberDao.findAll(new Specification<Member>() {
            @Nullable
            @Override
            public Predicate toPredicate(Root<Member> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {

                Path<String> usernameField = root.get("username");
                Path<String> nicknameField = root.get("nickname");
                Path<String> mobileField = root.get("mobile");
                Path<String> emailField = root.get("email");
                Path<String> inviteCodeField = root.get("inviteCode");
                Path<String> inviteByField = root.get("inviteBy");
                Path<String> sexField = root.get("sex");
                Path<Integer> typeField = root.get("type");
                Path<Integer> vipStatusField = root.get("vipStatus");
                Path<Integer> statusField = root.get("status");
                Path<Integer> platformField = root.get("platform");
                Path<Date> createTimeField = root.get("createTime");

                List<Predicate> list = new ArrayList<Predicate>();

                // 用户名相等匹配
                if (StrUtil.isNotBlank(member.getUsername())) {
                    list.add(cb.equal(usernameField, member.getUsername()));
                }
                // 邀请码相等匹配
                if (StrUtil.isNotBlank(member.getInviteCode())) {
                    list.add(cb.equal(inviteCodeField, member.getInviteCode()));
                }
                // 邀请人相等匹配
                if (StrUtil.isNotBlank(member.getInviteBy())) {
                    list.add(cb.equal(inviteByField, member.getInviteBy()));
                }
                // 昵称模糊搜素
                if (StrUtil.isNotBlank(member.getNickname())) {
                    list.add(cb.like(nicknameField, '%' + member.getNickname() + '%'));
                }
                // 手机模糊搜素
                if (StrUtil.isNotBlank(member.getMobile())) {
                    list.add(cb.like(mobileField, '%' + member.getMobile() + '%'));
                }
                // 邮件模糊搜素
                if (StrUtil.isNotBlank(member.getEmail())) {
                    list.add(cb.like(emailField, '%' + member.getEmail() + '%'));
                }
                // 性别 相等匹配
                if (StrUtil.isNotBlank(member.getSex())) {
                    list.add(cb.equal(sexField, member.getSex()));
                }
                // 会员类型 相等匹配
                if (member.getType() != null) {
                    list.add(cb.equal(typeField, member.getType()));
                }
                // VIP状态 相等匹配
                if (member.getVipStatus() != null) {
                    list.add(cb.equal(vipStatusField, member.getVipStatus()));
                }
                // 会员状态 相等匹配
                if (member.getStatus() != null) {
                    list.add(cb.equal(statusField, member.getStatus()));
                }
                // 注册平台 相等匹配
                if (member.getPlatform() != null) {
                    list.add(cb.equal(platformField, member.getPlatform()));
                }

                // 创建时间
                if (StrUtil.isNotBlank(searchVo.getStartDate()) && StrUtil.isNotBlank(searchVo.getEndDate())) {
                    Date start = DateUtil.parse(searchVo.getStartDate());
                    Date end = DateUtil.parse(searchVo.getEndDate());
                    list.add(cb.between(createTimeField, start, DateUtil.endOfDay(end)));
                }

                Predicate[] arr = new Predicate[list.size()];
                cq.where(list.toArray(arr));
                return null;
            }
        }, pageable);
    }

    @Override
    public Member findByUsername(String username) {

        return memberDao.findByUsername(username);
    }

    @Override
    public Member findByMobile(String mobile) {

        return memberDao.findByMobile(mobile);
    }

}
