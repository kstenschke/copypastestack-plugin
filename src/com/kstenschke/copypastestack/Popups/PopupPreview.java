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
package com.kstenschke.copypastestack.Popups;

import com.kstenschke.copypastestack.resources.StaticTexts;
import com.kstenschke.copypastestack.ToolWindow;
import com.kstenschke.copypastestack.Utils.UtilsEnvironment;
import com.kstenschke.copypastestack.resources.ui.ToolWindowForm;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PopupPreview extends PopupBase {

    private final ToolWindow toolWindow;
    final private JPopupMenu popup;
    private final JMenuItem menuItemCopySelection;
    private final JMenuItem menuItemPaste;

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

                boolean hasSelection = previewHasSelection();
                menuItemCopySelection.setEnabled(hasSelection);
                menuItemPaste.setEnabled( !UtilsEnvironment.isClipboardEmpty() );
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    private boolean previewHasSelection() {
        JEditorPane editorPanePreview = toolWindow.getForm().textPanePreview;
        Integer selStart    = editorPanePreview.getSelectionStart();
        Integer selEnd    = editorPanePreview.getSelectionEnd();

        return ! selStart.equals( selEnd );
    }

}
