package com.raxdiam.teamperms.util;

import java.util.*;

public class TeamMap extends LinkedHashMap<String, List<String>> {
    private final ArrayList<String> indicies = new ArrayList<>();

    @Override
    public boolean containsKey(Object key) {
        return super.containsKey(((String) key).toLowerCase());
    }

    @Override
    public List<String> get(Object key) {
        return super.get(((String) key).toLowerCase());
   }

   public List<String> getAtIndex(int index) {
        return super.get(indicies.get(index));
   }

   public String getKeyAtIndex(int index) {
        return indicies.get(index);
   }

   public int indexOf(String key) {
        return indicies.indexOf(key);
   }

    @Override
    public List<String> put(String key, List<String> value) {
        String newKey = key.toLowerCase();
        if (!super.containsKey(newKey)) indicies.add(newKey);
        return super.put(newKey, value);
    }

    @Override
    public List<String> remove(Object key) {
        String newKey = ((String) key).toLowerCase();
        indicies.remove(newKey);
        return super.remove(newKey);
    }
}
