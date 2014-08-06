package com.kstenschke.copypastestack;

import com.kstenschke.copypastestack.resources.ui.ToolWindowForm;

import javax.swing.*;

public class TagManager {

    public static void untagAll(ToolWindowForm form) {
        ListModel listModel  = form.clipItemsList.getModel();
        int amountItems      = listModel.getSize();

        for(int i=0; i< amountItems; i++) {
            String hashCode = String.valueOf(listModel.getElementAt(i).toString().hashCode());
            Preferences.deleteHashTag(hashCode);
        }
    }

    public static void tagSelected(ToolWindowForm form, int idColor) {
        ListModel listModel                 = form.clipItemsList.getModel();
        ListSelectionModel selectionModel   = form.clipItemsList.getSelectionModel();

        Boolean hasSelection = ! form.clipItemsList.isSelectionEmpty();
        int amountItems      = listModel.getSize();
        int amountSelected   = ! hasSelection ? 0 : form.clipItemsList.getSelectedValuesList().size();

        for(int i=0; i< amountItems; i++) {
            if( amountSelected == 0 || selectionModel.isSelectedIndex(i) ) {
                String hashCode = String.valueOf(listModel.getElementAt(i).toString().hashCode());
                if( idColor > 0 ) {
                    Preferences.saveHashTag(hashCode, idColor);
                } else {
                    Preferences.deleteHashTag(hashCode);
                }
            }
        }
    }

}
