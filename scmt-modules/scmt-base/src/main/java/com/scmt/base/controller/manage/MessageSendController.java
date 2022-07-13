package com.scmt.base.controller.manage;

import com.scmt.core.base.ScmtBaseController;
import com.scmt.core.common.annotation.SystemLog;
import com.scmt.core.common.constant.CommonConstant;
import com.scmt.core.common.enums.LogType;
import com.scmt.core.common.utils.PageUtil;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.utils.SecurityUtil;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.Result;
import com.scmt.core.entity.Message;
import com.scmt.core.entity.MessageSend;
import com.scmt.core.entity.User;
import com.scmt.core.service.MessageSendService;
import com.scmt.core.service.MessageService;
import com.scmt.core.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Exrick
 */
@Slf4j
@RestController
@Api(description = "消息发送管理接口")
@RequestMapping("/scmt/messageSend")
@Transactional
public class MessageSendController extends ScmtBaseController<MessageSend, String> {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageSendService messageSendService;

    @Override
    public MessageSendService getService() {
        return messageSendService;
    }

    @Autowired
    private SecurityUtil securityUtil;

    @RequestMapping(value = "/getByCondition", method = RequestMethod.GET)
    @ApiOperation(value = "多条件分页获取")
    @SystemLog(description = "多条件分页获取", type = LogType.OPERATION)
    public Result<Page<MessageSend>> getByCondition(MessageSend ms,
                                                    PageVo pv) {

        Page<MessageSend> page = messageSendService.findByCondition(ms, PageUtil.initPage(pv));
        page.getContent().forEach(item -> {
            User u = userService.get(item.getUserId());
            if (u != null) {
                item.setUsername(u.getUsername()).setNickname(u.getNickname());
            }
            Message m = messageService.get(item.getMessageId());
            item.setTitle(m.getTitle()).setContent(m.getContent()).setType(m.getType());
        });
        return new ResultUtil<Page<MessageSend>>().setData(page);
    }

    @RequestMapping(value = "/all/{type}", method = RequestMethod.GET)
    @ApiOperation(value = "获取全部数据")
    @SystemLog(description = "获取全部数据", type = LogType.OPERATION)
    public Result<Object> batchOperation(@Param("0全部已读 1全部删除已读") @PathVariable Integer type) {

        User u = securityUtil.getCurrUser();
        if (type == 0) {
            messageSendService.updateStatusByUserId(u.getId(), CommonConstant.MESSAGE_STATUS_READ);
        } else if (type == 1) {
            messageSendService.deleteByUserId(u.getId());
        }
        return ResultUtil.success("操作成功");
    }
}
