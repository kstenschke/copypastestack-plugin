/*
 * Copyright 2014-2015 Kay Stenschke
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

import com.intellij.openapi.ide.CopyPasteManager;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

public class UtilsClipboard {

    /**
     * @param   transferables
     * @return  int
     */
    public static int getAmountStringItemsInTransferables(Transferable[] transferables) {
        int amount = 0;
        for( Transferable currentItem : transferables) {
            if( currentItem.isDataFlavorSupported( DataFlavor.stringFlavor ) )  {
                amount++;
            }
        }

        return amount;
    }

    /**
     * @return  Does the clipboard contain anything (being string or image) currently?
     */
    public static boolean hasContent() {
        Transferable[] transferables = CopyPasteManager.getInstance().getAllContents();
        for( Transferable currentItem : transferables) {
            if(    currentItem.isDataFlavorSupported( DataFlavor.stringFlavor )
                || currentItem.isDataFlavorSupported( DataFlavor.imageFlavor )
            )  {
                return true;
            }
        }

        return false;
    }

    /**
     * @param text
     * @return boolean  Is given string in clipboard currently?
     */
    public static boolean isInClipboard(String text) {
        if( text == null || text.isEmpty() ) {
            return false;
        }

        Transferable[] copiedItems = CopyPasteManager.getInstance().getAllContents();

        int amountItems     = getAmountStringItemsInTransferables(copiedItems);
        if( amountItems > 0 ) {
            for( Transferable currentItem : copiedItems) {
                if( currentItem.isDataFlavorSupported( DataFlavor.stringFlavor ) )  {
                    try {
                        String itemStr  = currentItem.getTransferData( DataFlavor.stringFlavor ).toString();
                        if( itemStr.equals(text) ) {
                            return true;
                        }
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return false;
    }

}
