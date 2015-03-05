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
package com.kstenschke.copypastestack;

import com.kstenschke.copypastestack.resources.ui.ToolWindowForm;

import javax.swing.*;

public class TagManager {

    public static void untagAll(ToolWindowForm form) {
        ListModel listModel  = form.listClipItems.getModel();
        int amountItems      = listModel.getSize();

        for(int i=0; i< amountItems; i++) {
            String hashCode = String.valueOf(listModel.getElementAt(i).toString().hashCode());
            Preferences.deleteHashTag(hashCode);
        }
    }

    public static void tagSelected(ToolWindowForm form, int idColor) {
        ListModel listModel                 = form.listClipItems.getModel();
        ListSelectionModel selectionModel   = form.listClipItems.getSelectionModel();

        boolean hasSelection = ! form.listClipItems.isSelectionEmpty();
        int amountItems      = listModel.getSize();
        int amountSelected   = ! hasSelection ? 0 : form.listClipItems.getSelectedIndices().length;

        for(int i=0; i< amountItems; i++) {
            if( amountSelected == 0 || selectionModel.isSelectedIndex(i) ) {
                String hashCode = String.valueOf(listModel.getElementAt(i).toString().hashCode());
                if( idColor > 0 ) {
                    Preferences.saveHashTag(hashCode, idColor);
                } else {
                    Preferences.deleteHashTag(hashCode);
                }
            }
        }
    }

    public static int getIdColorByValue(String value) {
        String hashCode = String.valueOf(value.hashCode());

        return Preferences.getIdColorByHashTag(hashCode);
    }

}
