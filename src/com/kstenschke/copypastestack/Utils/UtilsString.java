/*
 * Copyright 2014 Kay Stenschke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kstenschke.copypastestack.Utils;

public class UtilsString {

    /**
     * @param   str
     * @return  String
     */
    public static String convertWhitespace(String str) {
        str = str.replaceAll("\\\\n", "\n");
        str = str.replaceAll("\\\\r", "\r");
        str = str.replaceAll("\\\\t", "\t");

        return str;
    }

    /**
     * @param   str
     * @return  boolean     Does str contain any right-to-left language character?
     */
    public static boolean containsRTL(String str) {
        char[] chars = str.toCharArray();
        for(char c: chars){
            if(c >= 0x600 && c <= 0x6ff){
                return true;
            }
        }

        return false;
    }

}
