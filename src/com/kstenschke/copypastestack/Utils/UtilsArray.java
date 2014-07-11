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


public class UtilsArray {

    /**
     * @param   arr
     * @return  int
     */
    public static int getAmountUniqueItems(Object[] arr){
        int amount = 0;
        for(int i=0; i<arr.length; i++){
            if( arr[i] != null ) {
                boolean isDistinct = true;

                for (int j = 0; j < i; j++) {
                    if (arr[j] != null && arr[i].equals(arr[j])) {
                        isDistinct = false;
                        break;
                    }
                }

                if (isDistinct) {
                    amount++;
                }
            }
        }
        return amount;
    }

    /**
     * @param   arr
     * @param   removeEmpty
     * @param   trim
     * @return  String[]        Array of unique items, w/o any empty item
     */
    public static String[] tidy(Object[] arr, Boolean removeEmpty, Boolean trim) {
        String[] distinct   = new String[ getAmountUniqueItems(arr) ];
        int index           = 0;

        for(int i=0; i<arr.length; i++) {
            boolean isDistinct = true;

            for(int j=0; j<i; j++){
                if( arr[i] != null && arr[j] != null && arr[i].equals(arr[j]) ){
                    isDistinct = false;
                    break;
                }
            }

            String item = arr[i] != null ? arr[i].toString() : "";

            if( item != null && !item.isEmpty() && trim ) {
                item    = item.trim();
            }

            if(isDistinct && item != null && (! removeEmpty || !item.isEmpty() ) ) {
                distinct[index] = item;
                index++;
            }
        }

        return distinct;
    }

}
