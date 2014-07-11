package com.kstenschke.copypastestack.Listeners;

import com.kstenschke.copypastestack.ToolWindow;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.MouseEvent;

public class MouseListenerCheckboxLabel extends MouseListenerBase {

    private JCheckBox checkbox;

    public MouseListenerCheckboxLabel(@Nullable String description, JCheckBox checkbox) {
        super(description);
        this.checkbox = checkbox;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        this.checkbox.setSelected( ! this.checkbox.isSelected() );
    }

}
