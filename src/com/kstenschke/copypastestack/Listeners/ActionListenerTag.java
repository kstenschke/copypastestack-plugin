package com.kstenschke.copypastestack.Listeners;

import com.kstenschke.copypastestack.TagManager;
import com.kstenschke.copypastestack.ToolWindow;
import com.kstenschke.copypastestack.resources.ui.ToolWindowForm;

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
        TagManager.tagSelected(form, this.idColor);
    }
}
