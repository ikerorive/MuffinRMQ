/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DTO;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Ander
 */
@XmlRootElement(name = "responseList")
public class ResposeList {

    private List<Integer> list;

    public List<Integer> getList() {
        return list;
    }

    public void setList(List<Integer> list) {
        this.list = list;
    }

}