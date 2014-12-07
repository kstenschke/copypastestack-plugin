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
package com.kstenschke.copypastestack.resources.ui;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;

import javax.swing.*;

public class ToolWindowForm {

    public JButton buttonCopy;
    public JButton buttonDelete;
    public JButton buttonInfo;
    public JButton buttonPaste;
    public JButton buttonRefresh;
    public JButton buttonSortAlphabetical;
    public JButton buttonTagGreen;
    public JButton buttonTagRed;
    public JButton buttonTagUnset;
    public JButton buttonTagYellow;

    public JCheckBox checkBoxScale50Percent;
    public JCheckBox checkboxFocusOnPaste;
    public JCheckBox checkboxImmediatePaste;
    public JCheckBox checkboxKeepSorted;
    public JCheckBox checkBoxWrap;
    public JCheckBox checkboxWrapExtended;

    public JLabel labelAfter;
    public JLabel labelImage;
    public JLabel labelImageSize;
    public JLabel labelOptionFocusOnPaste;
    public JLabel labelOptionImmediateInsert;
    public JLabel labelPreviewImage;
    public JLabel labelSettings;
    public JLabel labelStatus;
    public JLabel labelTag;
    public JLabel labelWrapOptions;

    public JList listClipItems;
    public JList listPreview;

    public JPanel panelMain;
    public JPanel panelAroundPreview;
    public JPanel panelAroundWrap;
    public JPanel panelPreview;
    public JPanel panelPreviewImage;
    public JPanel panelWrap;

    public JScrollPane scrollPaneItemsList;
    public JScrollPane scrollPanePreview;

    public JSeparator separatorPreview;

    public JSplitPane splitPaneToolContent;

    public JTextField textFieldWrapAfter;
    public JTextField textFieldWrapBefore;
    public JTextField textFieldWrapDelimiter;

    public JEditorPane textPanePreview;

    public JToolBar toolBarMain;
    public JPanel panelPreviewMenu;
    public JButton buttonRefreshPreview;
    public JToolBar toolbarWrapOptions;

    public Document document;
    public Editor editor;

    /**
     * Constructor
     */
    public ToolWindowForm() {
        this.document   = null;
    }

    public JPanel getPanelMain() {
        return panelMain;
    }

    private void createUIComponents() {

    }

}

