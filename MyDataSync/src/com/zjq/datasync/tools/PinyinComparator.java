package com.zjq.datasync.tools;

import java.util.Comparator;

import com.zjq.datasync.model.Contact;

import net.sourceforge.pinyin4j.PinyinHelper;

public class PinyinComparator implements Comparator<Contact> {

	@Override
	public int compare(Contact o1, Contact o2) {  
		if(((String) o1.getName()).length() != 0 && ((String) o2.getName()).length() != 0){
			char c1 = ((String) o1.getName()).charAt(0);  
	        char c2 = ((String) o2.getName()).charAt(0);  
	        return concatPinyinStringArray(  
	                PinyinHelper.toHanyuPinyinStringArray(c1)).compareTo(  
	                concatPinyinStringArray(PinyinHelper  
	                        .toHanyuPinyinStringArray(c2))); 
		} else {
			return 0;
		}
    }  
	
	
    private String concatPinyinStringArray(String[] pinyinArray) {  
        StringBuffer pinyinSbf = new StringBuffer();  
        if ((pinyinArray != null) && (pinyinArray.length > 0)) {  
            for (int i = 0; i < pinyinArray.length; i++) {  
                pinyinSbf.append(pinyinArray[i]);  
            }  
        }  
        return pinyinSbf.toString();  
    }  
}
