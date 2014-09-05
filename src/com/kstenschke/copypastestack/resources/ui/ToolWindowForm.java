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

import org.intellij.images.ui.ImageComponent;

import javax.swing.*;

public class ToolWindowForm {

    public JToolBar toolBar;

    private JPanel mainPanel;
    public JPanel panelWrap;

    public JList clipItemsList;

    public JButton buttonSortAlphabetical;
    public JButton buttonRefresh;
    public JButton buttonDelete;
    public JButton buttonPaste;
    public JButton buttonCopy;

    public JTextField textFieldWrapAfter;
    public JTextField textFieldWrapBefore;
    public JTextField textFieldWrapDelimiter;

    public JCheckBox checkboxFocusOnPaste;
    public JCheckBox checkboxImmediateCopy;
    public JCheckBox checkboxImmediatePaste;
    public JCheckBox checkboxKeepSorted;
    public JCheckBox checkBoxWrap;
    public JCheckBox checkboxWrapExtended;

    public JLabel labelStatus;
    public JLabel labelAfter;
    public JLabel labelOptionImmediateInsert;
    public JLabel labelOptionFocusOnPaste;

    public JTextPane textPanePreview;
    public JPanel panelPreview;

    public JButton buttonTagYellow;
    public JButton buttonTagGreen;
    public JButton buttonTagRed;
    public JButton buttonTagUnset;
    public JSeparator separatorPreview;
    public JLabel labelTag;
    public JLabel labelSettings;
    public JButton buttonInfo;
    public JPanel panelAroundPreview;
    public JScrollPane jscrollpaneItemsList;
    public JPanel panelAroundWrap;
    public JSplitPane splitpaneToolContent;
    public JList jListPreview;
    public JScrollPane scrollPanePreview;
    public JToolBar toolbarWrap;
    public JLabel labelPreviewImage;
    public JPanel panelPreviewImage;
    public JCheckBox a50CheckBox;
    public JLabel labelWrapOptions;
    public JLabel labelImage;
    public JLabel labelImageSize;

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private void createUIComponents() {

    }
}

