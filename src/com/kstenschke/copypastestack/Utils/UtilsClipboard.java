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
                        java.lang.String itemStr  = currentItem.getTransferData( DataFlavor.stringFlavor ).toString();
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
