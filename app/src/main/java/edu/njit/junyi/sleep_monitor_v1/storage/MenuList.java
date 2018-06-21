package edu.njit.junyi.sleep_monitor_v1.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by junyi on 4/4/18.
 */

public class MenuList {
    public List<String> myList;

    public MenuList() {
        this.myList = new ArrayList<>();
    }

    public void addItem(String item) {
        myList.add(item);
    }

    public List<String> getList() {
        return myList;
    }

    public void setList(List<String> list) {
        myList = list;
    }
}
