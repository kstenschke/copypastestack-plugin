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
package com.kstenschke.copypastestack.Listeners;

import com.kstenschke.copypastestack.ToolWindow;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ListSelectionListenerItemsList implements ListSelectionListener {

    ToolWindow toolWindow;

    public ListSelectionListenerItemsList(ToolWindow toolWindow) {
        this.toolWindow = toolWindow;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if( this.toolWindow.isActivePreview() ) {
            String itemText = this.toolWindow.getSelectedItemText();
            this.toolWindow.setPreviewText(itemText);
        }
    }
}
