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
import com.kstenschke.copypastestack.Utils.UtilsEnvironment;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyListenerTextPanePreview implements KeyListener {

    private boolean isMac;
    private boolean isMetaDown;
    private ToolWindow toolWindow;

    /**
     * Constructor
     *
     * @param toolWindow
     */
    public KeyListenerTextPanePreview(ToolWindow toolWindow) {
        this.toolWindow = toolWindow;
        this.isMac = UtilsEnvironment.isMac();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        this.isMetaDown = e.isMetaDown();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(this.toolWindow != null) {
            Integer keyCode = e.getKeyCode();

            // [Ctrl] or (on mac) [cmd] key pressed?
            boolean isControlDown   = (this.isMac && this.isMetaDown) || e.isControlDown();

            if ( isControlDown && (keyCode == KeyEvent.VK_Z /* @todo catch vk_z on mac via proppert workaround */ ) ) {
                // CTRL + Z = undo
                if( this.toolWindow.undoManager.canUndo() ) {
                    this.toolWindow.undoManager.undo();
                }
            }
        }
    }

}
