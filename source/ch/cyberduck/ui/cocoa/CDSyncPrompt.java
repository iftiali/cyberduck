package ch.cyberduck.ui.cocoa;

/*
 *  Copyright (c) 2005 David Kocher. All rights reserved.
 *  http://cyberduck.ch/
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  Bug fixes, suggestions and comments should be sent to:
 *  dkocher@cyberduck.ch
 */

import ch.cyberduck.core.Preferences;
import ch.cyberduck.core.SyncTransfer;
import ch.cyberduck.core.TransferAction;
import ch.cyberduck.core.Transfer;

import com.apple.cocoa.application.*;
import com.apple.cocoa.foundation.NSArray;
import com.apple.cocoa.foundation.NSBundle;
import com.apple.cocoa.foundation.NSSelector;
import org.apache.log4j.Logger;

/**
 * @version $Id$
 */
public class CDSyncPrompt extends CDTransferPrompt {
    private static Logger log = Logger.getLogger(CDSyncPrompt.class);

    public CDSyncPrompt(final CDWindowController parent, final Transfer transfer) {
        super(parent, transfer);
    }

    public void awakeFromNib() {
        this.browserView.setDataSource(this.browserModel = new CDSyncPromptModel(this, transfer));
        super.awakeFromNib();
    }

    public void setBrowserView(NSOutlineView view) {
        super.setBrowserView(view);
        NSSelector setResizableMaskSelector
                = new NSSelector("setResizingMask", new Class[]{int.class});
        {
            NSTableColumn c = new NSTableColumn();
            c.setIdentifier(CDSyncPromptModel.SYNC_COLUMN);
            c.headerCell().setStringValue("");
            c.setMinWidth(20f);
            c.setWidth(20f);
            c.setMaxWidth(20f);
            if(setResizableMaskSelector.implementedByClass(NSTableColumn.class)) {
                c.setResizingMask(NSTableColumn.AutoresizingMask);
            }
            else {
                c.setResizable(true);
            }
            c.setEditable(false);
            c.setDataCell(new NSImageCell());
            c.dataCell().setAlignment(NSText.CenterTextAlignment);
            view.addTableColumn(c);
        }
        {
            NSTableColumn c = new NSTableColumn();
            c.setIdentifier(CDSyncPromptModel.CREATE_COLUMN);
            c.headerCell().setStringValue("");
            c.setMinWidth(20f);
            c.setWidth(20f);
            c.setMaxWidth(20f);
            if(setResizableMaskSelector.implementedByClass(NSTableColumn.class)) {
                c.setResizingMask(NSTableColumn.AutoresizingMask);
            }
            else {
                c.setResizable(true);
            }
            c.setEditable(false);
            c.setDataCell(new NSImageCell());
            c.dataCell().setAlignment(NSText.CenterTextAlignment);
            view.addTableColumn(c);
        }
        view.sizeToFit();
    }

    public void callback(final int returncode) {
        if(returncode == DEFAULT_OPTION) { // Continue
            action = TransferAction.ACTION_OVERWRITE;
        }
        else if(returncode == CANCEL_OPTION) { // Abort
            action = TransferAction.ACTION_CANCEL;
        }
    }

    // ----------------------------------------------------------
    // Outlets
    // ----------------------------------------------------------

    private final int INDEX_ACTION_DOWNLOAD = 0;
    private final int INDEX_ACTION_UPLOAD = 1;
    private final int INDEX_ACTION_MIRROR = 2;

    private static final String ACTION_DOWNLOAD_LOCALIZED = NSBundle.localizedString("Download", "");
    private static final String ACTION_UPLOAD_LOCALIZED = NSBundle.localizedString("Upload", "");
    private static final String ACTION_MIRROR_LOCALIZED = NSBundle.localizedString("Mirror", "");

    public void setActionPopup(final NSPopUpButton actionPopup) {
        this.actionPopup = actionPopup;
        this.actionPopup.removeAllItems();
        this.actionPopup.addItemsWithTitles(new NSArray(new String[]{
                ACTION_DOWNLOAD_LOCALIZED, ACTION_UPLOAD_LOCALIZED, ACTION_MIRROR_LOCALIZED
        }));
        this.actionPopup.setTarget(this);
        this.actionPopup.setAction(new NSSelector("actionPopupClicked", new Class[]{NSPopUpButton.class}));
        SyncTransfer.Action current = ((SyncTransfer)transfer).getAction();
        if(current.equals(SyncTransfer.ACTION_DOWNLOAD)) {
            this.actionPopup.selectItemAtIndex(INDEX_ACTION_DOWNLOAD);
        }
        else if(current.equals(SyncTransfer.ACTION_UPLOAD)) {
            this.actionPopup.selectItemAtIndex(INDEX_ACTION_UPLOAD);
        }
        else if(current.equals(SyncTransfer.ACTION_MIRROR)) {
            this.actionPopup.selectItemAtIndex(INDEX_ACTION_MIRROR);
        }
    }

    public void actionPopupClicked(NSPopUpButton sender) {
        SyncTransfer.Action current = ((SyncTransfer)transfer).getAction();
        if(actionPopup.indexOfSelectedItem() == INDEX_ACTION_DOWNLOAD) {
            if(current.equals(SyncTransfer.ACTION_DOWNLOAD)) {
                return;
            }
            Preferences.instance().setProperty("queue.sync.action.default", SyncTransfer.ACTION_DOWNLOAD.toString());
            //Download
            ((SyncTransfer)transfer).setAction(SyncTransfer.ACTION_DOWNLOAD);
        }
        else if(actionPopup.indexOfSelectedItem() == INDEX_ACTION_UPLOAD) {
            if(current.equals(SyncTransfer.ACTION_UPLOAD)) {
                return;
            }
            Preferences.instance().setProperty("queue.sync.action.default", SyncTransfer.ACTION_UPLOAD.toString());
            //Upload
            ((SyncTransfer)transfer).setAction(SyncTransfer.ACTION_UPLOAD);
        }
        else if(actionPopup.indexOfSelectedItem() == INDEX_ACTION_MIRROR) {
            if(current.equals(SyncTransfer.ACTION_MIRROR)) {
                return;
            }
            Preferences.instance().setProperty("queue.sync.action.default", SyncTransfer.ACTION_MIRROR.toString());
            //Mirror
            ((SyncTransfer)transfer).setAction(SyncTransfer.ACTION_MIRROR);
        }
        browserView.reloadData();
    }
}