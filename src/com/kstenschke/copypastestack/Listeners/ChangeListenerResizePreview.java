package com.kstenschke.copypastestack.Listeners;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * Created by kay on 04.09.14.
 */
public class ChangeListenerResizePreview implements ChangeListener{

    FocusListenerViewClipboard focusListenerViewClipboard;

    public ChangeListenerResizePreview(FocusListenerViewClipboard focusListenerViewClipboard) {
        this.focusListenerViewClipboard = focusListenerViewClipboard;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        this.focusListenerViewClipboard.showClipboardPreview();
    }
}
