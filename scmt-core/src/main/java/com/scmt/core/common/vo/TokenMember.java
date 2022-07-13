package com.scmt.core.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Exrickx
 */
@Data
@AllArgsConstructor
public class TokenMember implements Serializable {

    private String username;

    private Integer platform;
}
