/*
* Copyright 2011-2014 Kay Stenschke
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

public class KeyListenerItemsList implements KeyListener {

    private final ToolWindow toolWindow;
    private final boolean isMac;

    public KeyListenerItemsList(ToolWindow toolWindow) {
        this.toolWindow = toolWindow;
        this.isMac  = UtilsEnvironment.isMac();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        Integer keyCode = e.getKeyCode();
        switch( keyCode ) {
            case KeyEvent.VK_ENTER:
            case KeyEvent.VK_SPACE:
                toolWindow.pasteItems();
                break;

            case KeyEvent.VK_DELETE:
            case KeyEvent.VK_BACK_SPACE:
                this.toolWindow.removeItemsFromStack(null);
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        boolean isControlDown  = e.isControlDown();
        boolean isMetaDown     = e.isMetaDown();
        int keyCode = e.getKeyCode();

        if ( (!this.isMac && isControlDown) || (isMac && isMetaDown) ) {
            switch (keyCode) {
                case KeyEvent.VK_C:
                    this.toolWindow.copySelectedItems();
                    break;
            }
        }

    }
}
