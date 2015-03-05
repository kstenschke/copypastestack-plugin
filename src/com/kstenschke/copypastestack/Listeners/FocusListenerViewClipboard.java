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

import com.kstenschke.copypastestack.ToolWindow;
import com.kstenschke.copypastestack.Utils.UtilsFile;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;

public class FocusListenerViewClipboard implements FocusListener {

    final ToolWindow toolWindow;

    public FocusListenerViewClipboard(ToolWindow toolWindow) {
        this.toolWindow = toolWindow;
    }

    @Override
    public void focusGained(FocusEvent e) {
        updateClipboardPreview();
    }

    public void updateClipboardPreview() {
        Clipboard clipboard             = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable clipboardContents  = clipboard.getContents(null);

        if (clipboardContents != null ) {
            try {
                if (clipboardContents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    previewClipboardText(clipboardContents);
                } else /*if (clipboardContents.isDataFlavorSupported(DataFlavor.imageFlavor))*/ {
                        // (Assuming everything non-string to be an image..)
                    previewClipboardImage(clipboard);
                }
            } catch (UnsupportedFlavorException fe) {
                fe.printStackTrace();
            } catch (IOException ie) {
                ie.printStackTrace();
            }
        }
    }

    public void previewClipboardText(Transferable clipboardContents) throws UnsupportedFlavorException, IOException {
        String clipText = (String) clipboardContents.getTransferData(DataFlavor.stringFlavor);
        this.toolWindow.setPreviewText(clipText);
        this.toolWindow.getForm().textPanePreview.setCaretPosition(0);

        toolWindow.form.labelPreviewImage.setIcon(null);
        toolWindow.form.textPanePreview.setVisible(true);
    }

    public void previewClipboardImage(Clipboard clipboard) throws UnsupportedFlavorException, IOException {
        Transferable clipboardContents  = clipboard.getContents(null);
        BufferedImage clipImage = null;

        if( clipboardContents.isDataFlavorSupported(DataFlavor.imageFlavor) ) {
            clipImage   = (BufferedImage) clipboard.getData(DataFlavor.imageFlavor);
            displayImageInViewer(clipImage);
        } else {
            this.macPreviewClipboardImage();
        }
    }

    public void displayImageInViewer(BufferedImage image) {
        if( image != null ) {
            if( toolWindow.isPreviewImage50Percent() ) {
                image = resizeImage(image, 0.5);
            }
            toolWindow.form.labelImageSize.setText(image.getWidth(null)+"x"+image.getHeight(null)+"px");

            ImageIcon icon = new ImageIcon(image);
            toolWindow.form.panelPreviewImage.setMaximumSize(toolWindow.form.scrollPanePreview.getSize());
            toolWindow.form.labelPreviewImage.setIcon(icon);

            toolWindow.form.panelPreviewImage.setVisible(true);
            toolWindow.form.listPreview.setSelectedIndex(0);
            toolWindow.form.scrollPanePreview.setVisible(false);
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        this.toolWindow.form.listPreview.clearSelection();
    }

    /**
     * @param   originalImage
     * @param   factor
     * @return  BufferedImage
     */
    private BufferedImage resizeImage(BufferedImage originalImage, double factor) {
        int w = originalImage.getWidth();
        int h = originalImage.getHeight();
        BufferedImage after = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        AffineTransform at  = new AffineTransform();
        at.scale(factor, factor);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        after = scaleOp.filter(originalImage, after);

        return after;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Workaround to get image out of the clipboard on Mac OS (instead of depending on Quicktime for Java)
     *
     * 1. use apple script to detect type and save clipboard image temporarily to home folder
     * 2. find copy/paste stack exported image in home folder and load into Java Image
     * 3. delete exported temporary image file
     * 4. display the image :)
     */
    void macPreviewClipboardImage() {
        new Thread() {  // thread 1
            public void run() {
                try {
                    SwingUtilities.invokeLater(
                            new Runnable() { // runnable 1
                                public void run() {
                                    exportClipboardImageViaAppleScript();
                                }
                            }
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new Thread() {  // thread 2
                    public void run() {
                        try {
                            SwingUtilities.invokeLater(
                                    new Runnable() { // runnable 2
                                        public void run() {
                                            displayImageInViewer(loadExportedClipboardImage(true));
                                        }
                                    }
                            );
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        }.start();
    }

    private BufferedImage loadExportedClipboardImage(boolean doDelete) {
        BufferedImage image = null;
        String pathClipboardImage = this.getPathToClipboardImage();
        try {
            image = ImageIO.read(new File(pathClipboardImage));
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
        if(doDelete) {
            new File(pathClipboardImage).delete();
        }

        return image;
    }

    private String getPathToClipboardImage() {
        String pathHome = System.getProperty("user.home");

        return UtilsFile.getNewestFileInPath(pathHome);
    }

    private void exportClipboardImageViaAppleScript() {
        String command[] = {
                "osascript",
                "-e",

                "property fileTypes : {¬\n" +
                "\t{JPEG picture, \".jpg\"}, ¬\n" +
                "\t{TIFF picture, \".tiff\"}, ¬\n" +
                "\t{GIF picture, \".gif\"}, ¬\n" +
                "\t{«class PDF », \".pdf\"}, ¬\n" +
                "\t{«class RTF », \".rtf\"}}\n" +
                "\n" +
                "set theType to getType()\n" +
                "if theType is not missing value then\n" +
                "\tset myPath to (path to home folder as text) & \"copypastestack-tmp\" & (second item of theType)\n" +
                "\ttry\n" +
                "\t\tset myFile to (open for access myPath with write permission)\n" +
                "\t\tset eof myFile to 0\n" +
                "\t\twrite (the clipboard as (first item of theType)) to myFile -- as whatever\n" +
                "\t\tclose access myFile\n" +
                "\t\treturn (POSIX path of myPath)\n" +
                "\ton error\n" +
                "\t\ttry\n" +
                "\t\t\tclose access myFile\n" +
                "\t\tend try\n" +
                "\t\treturn \"\"\n" +
                "\tend try\n" +
                "else\n" +
                "\treturn \"\"\n" +
                "end if\n" +
                "\n" +
                "on getType()\n" +
                "\trepeat with aType in fileTypes -- find the first match in the list\n" +
                "\t\trepeat with theInfo in (clipboard info)\n" +
                "\t\t\tif (first item of theInfo) is equal to (first item of aType) then return aType\n" +
                "\t\tend repeat\n" +
                "\tend repeat\n" +
                "\treturn missing value\n" +
                "end getType"
        };
        try {
            Process process = Runtime.getRuntime().exec( command );
            printErrorStream(process);
        } catch (IOException e) {
            System.out.println( "IOException: " + e.getMessage() );
        }
    }

    private void printErrorStream(Process process) throws IOException {
        String lsString;
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(process.getErrorStream()));
        while ((lsString = bufferedReader.readLine()) != null) {
            System.out.println(lsString);
        }
    }
}
