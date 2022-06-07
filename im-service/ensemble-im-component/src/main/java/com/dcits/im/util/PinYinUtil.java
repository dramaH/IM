package com.dcits.im.util;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

@Slf4j
public class PinYinUtil {
    public static void main(String[] args) {
        System.out.println(getPinyin("123中国红s123")); //--zhongguohong123
        System.out.println(getInitCha("123中国红s123")); //--zhongguohong123
    }

    /**
     * @param china (字符串 汉字)
     * @return 汉字转拼音 其它字符不变
     */
    public static String getPinyin(String china){
        HanyuPinyinOutputFormat formart = new HanyuPinyinOutputFormat();
        formart.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        formart.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        formart.setVCharType(HanyuPinyinVCharType.WITH_V);
        char[] arrays = china.trim().toCharArray();
        String result = "";
        try {
            for (int i=0;i<arrays.length;i++) {
                char ch = arrays[i];
                if(Character.toString(ch).matches("[\\u4e00-\\u9fa5]")){ //匹配是否是中文
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(ch,formart);
                    result += temp[0];
                }else{
                    result += ch;
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return result;
    }



    /**
     * 获取汉语拼音大写首字母
     * @param china (字符串 汉字)
     * @return 汉字转拼音
     */
    public static String getInitCha(String china){
        HanyuPinyinOutputFormat formart = new HanyuPinyinOutputFormat();
        formart.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        formart.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        formart.setVCharType(HanyuPinyinVCharType.WITH_V);
        char[] arrays = china.trim().toCharArray();
        try {
            for (int i=0;i<arrays.length;i++) {
                char ch = arrays[i];
                if(Character.toString(ch).matches("[\\u4e00-\\u9fa5]")){ //匹配是否是中文
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(ch,formart);
                    return String.valueOf(temp[0].charAt(0)).toUpperCase();
                }else{
                    return String.valueOf(ch).toUpperCase();
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            log.error("汉字转拼音异常", e);
        }
        return "";
    }
}
