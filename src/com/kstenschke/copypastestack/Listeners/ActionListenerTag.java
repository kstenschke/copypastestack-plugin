package com.kstenschke.copypastestack.Listeners;

import com.kstenschke.copypastestack.ToolWindow;
import com.kstenschke.copypastestack.resources.ui.ToolWindowForm;
import com.kstenschke.copypastestack.Preferences;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ActionListenerTag implements ActionListener {

    private ToolWindow toolWindow;
    private int idColor;

    public ActionListenerTag(ToolWindow toolWindow, int idColor) {
        this.toolWindow = toolWindow;
        this.idColor    = idColor;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ToolWindowForm form = this.toolWindow.getForm();
        ListModel listModel = form.clipItemsList.getModel();
        javax.swing.ListSelectionModel selectionModel   = form.clipItemsList.getSelectionModel();

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
