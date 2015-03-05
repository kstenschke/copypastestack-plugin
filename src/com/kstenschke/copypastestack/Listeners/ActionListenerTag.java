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
package com.kstenschke.copypastestack.Listeners;

import com.kstenschke.copypastestack.TagManager;
import com.kstenschke.copypastestack.ToolWindow;
import com.kstenschke.copypastestack.resources.ui.ToolWindowForm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ActionListenerTag implements ActionListener {

    private final ToolWindow toolWindow;
    private final int idColor;

    /**
     * Constructor
     * @param   toolWindow
     * @param   idColor
     */
    public ActionListenerTag(ToolWindow toolWindow, int idColor) {
        this.toolWindow = toolWindow;
        this.idColor    = idColor;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ToolWindowForm form = this.toolWindow.getForm();
        TagManager.tagSelected(form, this.idColor);
    }
}