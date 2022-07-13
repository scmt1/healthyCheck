package com.scmt.activiti.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Exrickx
 */
@Data
public class EmailMessage implements Serializable {

    private String username;

    private String content;

    private String fullUrl;
}
