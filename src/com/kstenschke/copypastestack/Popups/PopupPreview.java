package com.kstenschke.copypastestack.Popups;


import com.kstenschke.copypastestack.Static.StaticTexts;
import com.kstenschke.copypastestack.ToolWindow;
import com.kstenschke.copypastestack.Utils.UtilsEnvironment;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PopupPreview extends PopupBase {

    private ToolWindow toolWindow;

    final private JPopupMenu popup;

    JMenuItem menuItemCopySelection;

    /**
     * Constructor
     */
    public PopupPreview(final ToolWindow toolWindow) {
        this.toolWindow = toolWindow;

        final Class classToolWindow   = getToolWindowClassInstance(toolWindow);

        this.popup = new JPopupMenu();

            // Copy selection
        menuItemCopySelection = getJMenuItem(StaticTexts.POPUP_PREVIEW_COPY_SELECTION, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
           String selectedText = toolWindow.getForm().textPanePreview.getSelectedText();
           UtilsEnvironment.copyToClipboard( selectedText );
            }
        }, classToolWindow, "resources/images/copy.png");
        this.popup.add(menuItemCopySelection);
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
