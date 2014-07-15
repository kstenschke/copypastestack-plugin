package com.kstenschke.copypastestack.Popups;


import com.intellij.util.Icons;
import com.kstenschke.copypastestack.resources.StaticTexts;
import com.kstenschke.copypastestack.ToolWindow;
import com.kstenschke.copypastestack.Utils.UtilsEnvironment;
import com.kstenschke.copypastestack.resources.ui.ToolWindowForm;
import groovyjarjarantlr.Utils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PopupPreview extends PopupBase {

    private ToolWindow toolWindow;

    final private JPopupMenu popup;

    JMenuItem menuItemCopySelection;
    JMenuItem menuItemPaste;

    /**
     * Constructor
     */
    public PopupPreview(final ToolWindow toolWindow) {
        this.toolWindow = toolWindow;

        final Class classToolWindow   = getToolWindowClassInstance(toolWindow);

        this.popup = new JPopupMenu();

        final ToolWindowForm form = toolWindow.getForm();

            // Copy selection
        menuItemCopySelection = getJMenuItem(StaticTexts.POPUP_PREVIEW_COPY_SELECTION, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
           String selectedText = form.textPanePreview.getSelectedText();
           UtilsEnvironment.copyToClipboard( selectedText );
            }
        }, com.kstenschke.copypastestack.resources.Icons.ICON_COPY);
        this.popup.add(menuItemCopySelection);

            // Paste
        menuItemPaste = getJMenuItem(StaticTexts.POPUP_PASTE, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text              = form.textPanePreview.getText();
                Integer selectionStart   = form.textPanePreview.getSelectionStart();
                Integer selectionEnd     = form.textPanePreview.getSelectionEnd();
                Integer offset           = form.textPanePreview.getCaretPosition();

                String clipboardContent  = UtilsEnvironment.getClipboardContent();
                if( !selectionStart.equals(selectionEnd) ) {
                    text    = text.substring(0, selectionStart) + clipboardContent + text.substring(selectionEnd);
                    offset  = selectionStart;
                } else {
                    if( offset > 0 ) {
                        text = text.substring(0, offset) + clipboardContent + text.substring(offset);
                    } else {
                        text = clipboardContent + clipboardContent;
                    }
                }

                form.textPanePreview.setText( text );
                form.textPanePreview.setCaretPosition(offset + clipboardContent.length() );
            }
        }, com.kstenschke.copypastestack.resources.Icons.ICON_PASTE);
        this.popup.add(menuItemPaste);

            // Select all
        this.popup.add( getJMenuItem(StaticTexts.POPUP_SELECT_ALL, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                form.textPanePreview.setSelectionStart(0);
                form.textPanePreview.setSelectionEnd( form.textPanePreview.getText().length() );
            }
        }, null) );
    }

    /**
     * @return  PopupListener
     */
    public PopupListener getPopupListener() {
        return new PopupListener();
    }

    /**
     * PopupListener
     */
    class PopupListener extends MouseAdapter {
        /**
         * @param   e
         */
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        /**
         * @param   e
         */
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        /**
         * @param   e
         */
        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger() ) {

                Boolean hasSelection = previewHasSelection();
                menuItemCopySelection.setEnabled(hasSelection);
                menuItemPaste.setEnabled( !UtilsEnvironment.isClipboardEmpty() );
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    private Boolean previewHasSelection() {
        JTextPane textPanePreview = toolWindow.getForm().textPanePreview;
        Integer selStart    = textPanePreview.getSelectionStart();
        Integer selEnd    = textPanePreview.getSelectionEnd();

        return ! selStart.equals( selEnd );
    }

}
