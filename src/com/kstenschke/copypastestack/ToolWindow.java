/*
 * Copyright 2014 Kay Stenschke
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
package com.kstenschke.copypastestack;

import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.kstenschke.copypastestack.Listeners.MouseListenerBase;
import com.kstenschke.copypastestack.Listeners.MouseListenerCheckboxLabel;
import com.kstenschke.copypastestack.Listeners.MouseListenerItemsList;
import com.kstenschke.copypastestack.Popups.PopupItems;
import com.kstenschke.copypastestack.Utils.UtilsArray;
import com.kstenschke.copypastestack.Utils.UtilsEnvironment;
import com.kstenschke.copypastestack.Utils.UtilsString;
import com.kstenschke.copypastestack.resources.ui.ToolWindowForm;
import org.apache.commons.lang.ArrayUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.util.Arrays;

public class ToolWindow extends SimpleToolWindowPanel {

    private final ToolWindowForm form;
    private Boolean isMac;

    /**
     * Constructor - initialize the tool window content
     */
    public ToolWindow() {
        super(false);

        this.isMac  = UtilsEnvironment.isMac();
        this.form    = new ToolWindowForm();

        int amountItems = initItemsList();
        initStatusLabel(amountItems);
        initToolbar();
        initWrap();

            // Add form into toolWindow
        add(form.getMainPanel(), BorderLayout.CENTER);
    }

    /**
     * @return  JList
     */
    public JList getJlistItems() {
        return this.form.clipItemsList;
    }

    private void initToolbar() {
        this.form.buttonRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshClipboardList();
            }
        });
        this.form.buttonRefresh.addMouseListener( new MouseListenerBase(StaticTexts.INFO_REFRESH));

        this.form.buttonSortAlphabetical.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sortClipboardListAlphabetical();
            }
        });
        this.form.buttonSortAlphabetical.addMouseListener(new MouseListenerBase(StaticTexts.INFO_SORT));

        this.form.checkboxKeepSorted.setSelected( Preferences.getIsActiveKeepSorting() );
        this.form.checkboxKeepSorted.addMouseListener( new MouseListenerBase(StaticTexts.INFO_KEEP_SORTED_ALPHABETICAL));
        this.form.checkboxKeepSorted.addChangeListener( new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                boolean isActive = form.checkboxKeepSorted.isSelected();
                if( isActive ) {
                    sortClipboardListAlphabetical();
                } else {
                    refreshClipboardList();
                }

                Preferences.saveIsActiveKeepSorting(isActive);
            }
        });

        this.form.buttonDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeSelectedItems();
            }
        });
        this.form.buttonDelete.addMouseListener(new MouseListenerBase(StaticTexts.INFO_DELETE));

        this.form.buttonPaste.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pasteItems();
            }
        });
        this.form.buttonPaste.addMouseListener(new MouseListenerBase(StaticTexts.INFO_PASTE));

        this.form.buttonCopy.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copySelectedItems();
            }
        } );
//        this.form.clipItemsList.addKeyListener( new KeyListenerItemsList(this) );
        this.form.buttonCopy.addMouseListener(new MouseListenerBase(StaticTexts.INFO_RECOPY));

        this.initAdditionalOptions();
    }

    public void removeSelectedItems() {
        Boolean hasSelection = ! this.form.clipItemsList.isSelectionEmpty();
        String[] items       = hasSelection ? getUnselectedItems() : new String[0];

        this.updateItemsList(items);
        Preferences.saveCopyItems(items);

        if( !hasSelection ) {
            this.refreshClipboardList();
        }
    }

    /**
     * @return  String[]
     */
    private String[] getUnselectedItems() {
        ListModel<String> listModel                     = this.form.clipItemsList.getModel();
        javax.swing.ListSelectionModel selectionModel   = this.form.clipItemsList.getSelectionModel();
        int[] selectedIndices                           = this.form.clipItemsList.getSelectedIndices();

        int amountItems             = listModel.getSize();
        String[] unselectedItems    = new String[ amountItems - selectedIndices.length ];
        int index = 0;
        for(int i=0; i< amountItems; i++ ) {
            if( !selectionModel.isSelectedIndex(i) ) {
                unselectedItems[ index ]    = listModel.getElementAt(i);
                index++;
            }
        }

        return unselectedItems;
    }

    /**
     * Paste all / selected items into editor
     */
    public void pasteItems() {
        Boolean hasSelection = ! this.form.clipItemsList.isSelectionEmpty();
        int amountSelected   = ! hasSelection ? 0 : this.form.clipItemsList.getSelectedValuesList().size();
        Boolean focusEditor = this.form.checkboxFocusOnPaste.isSelected();

        String wrapBefore   = "";
        String wrapAfter    = "";
        String wrapDelimiter= "";

        if( this.form.checkBoxWrap.isSelected() ) {
            wrapBefore      = this.form.textFieldWrapBefore.getText();
            wrapAfter       = this.form.textFieldWrapAfter.getText();
            wrapDelimiter   = this.form.textFieldWrapDelimiter.getText();

            if( this.form.checkboxWrapExtended.isSelected() ) {
                wrapBefore      = UtilsString.convertWhitespace(wrapBefore);
                wrapAfter       = UtilsString.convertWhitespace(wrapAfter);
                wrapDelimiter   = UtilsString.convertWhitespace(wrapDelimiter);
            }
        }

        if( !hasSelection || amountSelected > 1 ) {
                // Insert multiple items
            ListModel<String> listModel                     = this.form.clipItemsList.getModel();
            javax.swing.ListSelectionModel selectionModel   = this.form.clipItemsList.getSelectionModel();

            int amountItems     = listModel.getSize();
            int amountInserted  = 0;
            for(int i=0; i< amountItems; i++ ) {
                if( selectionModel.isSelectedIndex(i) ) {
                    String currentItemText  = listModel.getElementAt(i);
                    UtilsEnvironment.insertInEditor(
                        wrapBefore + currentItemText + wrapAfter + (amountInserted + 1 < amountSelected ? wrapDelimiter : ""), focusEditor
                    );
                    amountInserted++;
                }
            }
        } else {
                // Insert selected item
            Object itemValue= this.form.clipItemsList.getSelectedValue();
            if( itemValue != null ) {
                String itemText = itemValue.toString();
                if( this.form.checkBoxWrap.isSelected() ) {
                    itemText    = wrapBefore + itemText + wrapAfter;
                }
                UtilsEnvironment.insertInEditor(itemText, focusEditor);
            }
        }
    }

    /**
     * Copy selected items back into clipboard
     */
    public void copySelectedItems() {
        Boolean hasSelection = ! this.form.clipItemsList.isSelectionEmpty();
        if( hasSelection ) {
            ListModel<String> listModel                     = this.form.clipItemsList.getModel();
            javax.swing.ListSelectionModel selectionModel   = this.form.clipItemsList.getSelectionModel();

            int amountItems     = listModel.getSize();

            for(int i=0; i< amountItems; i++ ) {
                if( selectionModel.isSelectedIndex(i) ) {
                    String currentItemText  = listModel.getElementAt(i);
                    UtilsEnvironment.copyToClipboard(currentItemText);
                }
            }
        }
    }

    private void sortClipboardListAlphabetical() {
        ListModel<String> listModel = this.form.clipItemsList.getModel();
        int amountItems = listModel.getSize();

        if( amountItems > 0 ) {
            String[] items   = new String[amountItems];
            int index = 0;
            for (int i = 0; i < amountItems; i++) {
                items[index] = listModel.getElementAt(i);
                index++;
            }
            if( items.length > 0 ) {
                Arrays.sort(items, String.CASE_INSENSITIVE_ORDER);
            }

            this.updateItemsList(items);
        }
    }

    /**
     * Refresh listed items from distinct merged sum of:
     * 1. previous clipboard items still in prefs
     * 2. current clipboard items
     */
    private void refreshClipboardList() {
        Transferable[] copiedItems = CopyPasteManager.getInstance().getAllContents();

        int amountItems     = getAmountStringItemsInTransferables(copiedItems);
        if( amountItems > 0 ) {
            String[] copyItemsList   = new String[amountItems];
            int index           = 0;
            for( Transferable currentItem : copiedItems) {
                if( currentItem.isDataFlavorSupported( DataFlavor.stringFlavor ) )  {
                    try {
                        String itemStr  = currentItem.getTransferData( DataFlavor.stringFlavor ).toString();
                        copyItemsList[index] = itemStr;
                        index++;
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            String[] copyItemsPref = Preferences.getItems();
            Object[] allItems       = ArrayUtils.addAll(copyItemsList, copyItemsPref);
            String[] itemsUnique    = UtilsArray.tidy(allItems, true, true);

            if( itemsUnique.length > 0 ) {
                this.updateItemsList(itemsUnique);

                if( this.form.checkboxKeepSorted.isSelected() ) {
                    this.sortClipboardListAlphabetical();
                }

                Preferences.saveCopyItems(itemsUnique);
            }
        }
    }

    /**
     * @param   items
     */
    private void updateItemsList(String[] items) {
        this.form.clipItemsList.setListData( items );
    }

    /**
     * @param   amountItems
     */
    private void initStatusLabel(int amountItems) {
        this.form.labelStatus.setText( String.valueOf(amountItems) + " " + StaticTexts.LABEL_ITEMS);
    }

    /**
     * @return  Amount of items
     */
    private int initItemsList() {
        this.form.clipItemsList.setCellRenderer( new ListCellRendererCopyPasteStack(this.form.clipItemsList, false, this.isMac));
        String[] items = Preferences.getItems();
        this.updateItemsList( items );

            // Add keylistener
        this.form.clipItemsList.addKeyListener( new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                Integer keyCode = e.getKeyCode();
                switch( keyCode ) {
                    case KeyEvent.VK_ENTER:
                    case KeyEvent.VK_SPACE:
                    pasteItems();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        this.form.clipItemsList.addMouseListener(new MouseListenerItemsList(StaticTexts.INFO_LIST, this));

            // add popup
        this.form.clipItemsList.addMouseListener(new PopupItems(this).getPopupListener() );

        return items.length;
    }

    /**
     * @param   transferables
     * @return  int
     */
    private int getAmountStringItemsInTransferables(Transferable[] transferables) {
        int amount = 0;
        for( Transferable currentItem : transferables) {
            if( currentItem.isDataFlavorSupported( DataFlavor.stringFlavor ) )  {
                amount++;
            }
        }

        return amount;
    }

    private void initWrap() {
        Boolean isActiveWrap = Preferences.getIsActiveWrap();
        this.form.checkBoxWrap.setSelected( isActiveWrap );
        this.form.checkBoxWrap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Boolean isActive = form.checkBoxWrap.isSelected();
                form.panelWrap.setVisible( isActive );
                if( isActive ) {
                    form.textFieldWrapBefore.requestFocusInWindow();
                }
                Preferences.saveIsActiveWrap(isActive);
            }
        });

        this.form.panelWrap.setVisible( isActiveWrap );

        this.form.textFieldWrapBefore.setText(Preferences.getWrapBefore());

        FocusListener focusListenerWrapComponents = new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                Preferences.saveWrapBefore( form.textFieldWrapBefore.getText() );
                Preferences.saveWrapAfter(form.textFieldWrapAfter.getText());
                Preferences.saveWrapDelimiter(form.textFieldWrapDelimiter.getText());
            }
        };

        this.form.textFieldWrapBefore.addFocusListener( focusListenerWrapComponents );

        this.form.textFieldWrapAfter.setText(Preferences.getWrapAfter());
        this.form.textFieldWrapAfter.addFocusListener(focusListenerWrapComponents);

        this.form.textFieldWrapDelimiter.setText(Preferences.getWrapDelimiter());
        this.form.textFieldWrapDelimiter.addFocusListener(focusListenerWrapComponents);
    }

    private void initAdditionalOptions() {
        this.form.checkboxImmediatePaste.addMouseListener(new MouseListenerBase(StaticTexts.INFO_IMMEDIATE_PASTE));
        this.form.labelOptionImmediateInsert.addMouseListener( new MouseListenerCheckboxLabel(StaticTexts.INFO_IMMEDIATE_PASTE, this.form.checkboxImmediatePaste));

        Boolean isActiveImmediatePaste = Preferences.getIsActiveImmediatePaste();
        form.checkboxImmediatePaste.setSelected(isActiveImmediatePaste);
        this.form.checkboxImmediatePaste.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Boolean isActive = form.checkboxImmediatePaste.isSelected();
                Preferences.saveIsActiveImmediatePaste(isActive);
            }
        });

        Boolean isActiveFocusOnPaste = Preferences.getIsActiveFocusOnPaste();
        this.form.checkboxFocusOnPaste.setSelected(isActiveFocusOnPaste);

        this.form.checkboxFocusOnPaste.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Boolean isActive = form.checkboxFocusOnPaste.isSelected();
                Preferences.saveIsActiveFocusOnPaste( isActive );
            }
        });

        this.form.checkboxFocusOnPaste.addMouseListener(new MouseListenerBase(StaticTexts.INFO_FOCUS_ON_PASTE));
        this.form.labelOptionFocusOnPaste.addMouseListener( new MouseListenerCheckboxLabel(StaticTexts.INFO_FOCUS_ON_PASTE, this.form.checkboxFocusOnPaste));

        this.form.checkboxWrapExtended.setSelected(Preferences.getIsActiveWrapExtended());
        this.form.checkboxWrapExtended.addMouseListener( new MouseListenerBase(StaticTexts.INFO_WRAP_EXTENDED));
        this.form.checkboxWrapExtended.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Preferences.saveIsActiveWrapExtended(form.checkboxWrapExtended.isSelected());
            }
        });
    }

    /**
     * @return  Boolean
     */
    public Boolean isSelectedImmediatePaste() {
        return this.form.checkboxImmediatePaste.isSelected();
    }

    /**
     * @param   project     Idea Project
     * @return  Instance of AhnToolWindow
     */
    public static ToolWindow getInstance(Project project) {
        com.intellij.openapi.wm.ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Copy/Paste Stack");

        if( toolWindow != null ) {
            try {
                Content content = toolWindow.getContentManager().getContent(0);
                if( content != null ) {
                    JComponent toolWindowComponent = content.getComponent();
                    String canonicalName = toolWindowComponent.getClass().getCanonicalName();
                    if(canonicalName.endsWith("com.kstenschke.copypastestack.ToolWindow")) {
                        return (ToolWindow) toolWindowComponent;
                    }
                }
            } catch(Exception exception) {
                exception.printStackTrace();
            }
        }

        return null;
    }

    /**
     * @return  ToolWindow
     */
    public static ToolWindow getInstance() {
        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        if( openProjects.length == 0 ) {
            return null;
        }

        Project project= openProjects[0];

        return getInstance(project);
    }

}
