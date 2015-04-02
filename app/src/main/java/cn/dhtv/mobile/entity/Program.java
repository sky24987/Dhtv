package cn.dhtv.mobile.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by Jack on 2015/3/26.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Program extends Category implements Serializable{
}
