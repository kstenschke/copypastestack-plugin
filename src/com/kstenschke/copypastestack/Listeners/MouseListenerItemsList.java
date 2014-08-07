package com.kstenschke.copypastestack.Listeners;

import com.kstenschke.copypastestack.ToolWindow;
import org.jetbrains.annotations.Nullable;

import java.awt.event.MouseEvent;

public class MouseListenerItemsList extends MouseListenerBase {

    private final ToolWindow toolWindow;

    public MouseListenerItemsList(@Nullable String description, ToolWindow toolWindow) {
        super(description);
        this.toolWindow = toolWindow;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if( e.getClickCount() == 2) {
            toolWindow.pasteItems();
        } else if( toolWindow.isSelectedImmediatePaste() ) {
            toolWindow.pasteItems();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

}
