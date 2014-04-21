/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.safehaus.kiskis.mgmt.impl.dbmanager;

/**
 *
 * @author dilshat
 */
public class MyPojo {

    private final String content;

    public MyPojo(String test) {
        this.content = test;
    }

    public String getContent() {
        return content;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MyPojo other = (MyPojo) obj;
        if ((this.content == null) ? (other.getContent() != null) : !this.content.equals(other.getContent())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MyPojo{" + "content=" + content + '}';
    }

}
