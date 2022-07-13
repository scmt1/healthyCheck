package com.scmt.healthy.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.core.common.annotation.SystemLog;
import com.scmt.core.common.enums.LogType;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.vo.Result;
import com.scmt.healthy.common.SocketConfig;
import com.scmt.healthy.entity.TBarcode;
import com.scmt.healthy.entity.TDepartItemResult;
import com.scmt.healthy.entity.TGroupPerson;
import com.scmt.healthy.service.ITBarcodeService;
import com.scmt.healthy.service.ITDepartItemResultService;
import com.scmt.healthy.service.ITGroupPersonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

/**
 *@author
 **/
@RestController
@Api(tags ="webSocket数据接口")
@RequestMapping("/scmt/webSocket")
public class WebSocketController {

    @Autowired
    private ITGroupPersonService tGroupPersonService;
    @Autowired
    private ITDepartItemResultService tDepartItemResultService;
    @Autowired
    private ITBarcodeService tBarcodeService;
    /**
     * socket配置
     */
    @Autowired
    public SocketConfig socketConfig;


    /**
     * 功能描述：根据主键来获取数据
     * @param personId 人员id
     * @return 返回获取结果
     */
    @SystemLog(description = "webSocket连接", type = LogType.OPERATION)
    @ApiOperation("webSocket连接")
    @GetMapping("getWebSocket")
    public Result<Object> getWebSocket(@RequestParam(name = "personId")String personId){
        if (StringUtils.isBlank(personId)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            String content = "";
            TGroupPerson tGroupPerson = tGroupPersonService.getById(personId);
            if(tGroupPerson != null){
                String num = "";
                String age = "";
                String sex = "";
                String height = "0000";
                String weight = "0000";
                if(tGroupPerson.getAge().toString().length() == 1){
                    age = "000" + tGroupPerson.getAge().toString();
                }else if(tGroupPerson.getAge().toString().length() == 2){
                    age = "00" + tGroupPerson.getAge().toString();
                }else if(tGroupPerson.getAge().toString().length() == 3){
                    age = "0" + tGroupPerson.getAge().toString();
                }else if(tGroupPerson.getAge().toString().length() == 4){
                    age = tGroupPerson.getAge().toString();
                }
                if("男".equals(tGroupPerson.getSex())){
                    sex = "1";
                }else if("女".equals(tGroupPerson.getSex())){
                    sex = "2";
                }
                List<TDepartItemResult> tDepartItemResultList = tDepartItemResultService.getAllListByPersonId(personId);
                if(tDepartItemResultList.size() > 0){
                    for(TDepartItemResult tDepartItemResult : tDepartItemResultList){
                        if(tDepartItemResult.getOrderGroupItemProjectName().equals("身高")){
                            if(tDepartItemResult.getResult().length() == 1){
                                height = "000" + tDepartItemResult.getResult();
                            }else if(tDepartItemResult.getResult().length() == 2){
                                height = "00" + tDepartItemResult.getResult();
                            }else if(tDepartItemResult.getResult().length() == 3){
                                height = "0" + tDepartItemResult.getResult();
                            }else if(tDepartItemResult.getResult().length() == 4){
                                height = tDepartItemResult.getResult();
                            }
                        }else if(tDepartItemResult.getOrderGroupItemProjectName().equals("体重")){
                            if(tDepartItemResult.getResult().length() == 1){
                                weight = "000" + tDepartItemResult.getResult();
                            }else if(tDepartItemResult.getResult().length() == 2){
                                weight = "00" + tDepartItemResult.getResult();
                            }else if(tDepartItemResult.getResult().length() == 3){
                                weight = "0" + tDepartItemResult.getResult();
                            }else if(tDepartItemResult.getResult().length() == 4){
                                weight = tDepartItemResult.getResult();
                            }
                        }
                    }
                }
                TBarcode tBarcode = tBarcodeService.getTBarcodeByPersonId(personId,tGroupPerson.getTestNum());
                if(tBarcode.getBarcode().length() == 10){
                    num = "00000" + tBarcode.getBarcode();
                }else if(tBarcode.getBarcode().length() == 11){
                    num = "0000" + tBarcode.getBarcode();
                }else if(tBarcode.getBarcode().length() == 12){
                    num = "000" + tBarcode.getBarcode();
                }else if(tBarcode.getBarcode().length() == 13){
                    num = "00" + tBarcode.getBarcode();
                }else if(tBarcode.getBarcode().length() == 14){
                    num = "0" + tBarcode.getBarcode();
                }else if(tBarcode.getBarcode().length() == 15){
                    num = tBarcode.getBarcode();
                }
                content = num + age + sex + height + weight;
            }
            RepeatServer(content);
            return ResultUtil.data("查询成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 转发
     *
     * @param info
     */
    public void RepeatServer(String info) {
        if(StringUtils.isBlank(info)){
            return;
        }

        Socket socket = null;
        try {
            //创建一个流套接字并将其连接到指定主机上的指定端口号
            socket = new Socket(socketConfig.getLisServerIp(), socketConfig.getLisServerPort());

            //读取服务器端数据
            DataInputStream input = new DataInputStream(socket.getInputStream());
            //向服务器端发送数据
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.write(info.getBytes());

            out.close();
            input.close();
        } catch (Exception e) {
            System.out.println("客户端异常:" + e.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    socket = null;
                    System.out.println("客户端 finally 异常:" + e.getMessage());
                }
            }
        }
    }
}
