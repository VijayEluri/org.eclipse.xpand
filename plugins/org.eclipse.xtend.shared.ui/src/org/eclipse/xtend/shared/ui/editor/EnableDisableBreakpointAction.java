/*******************************************************************************
 * Copyright (c) 2007 committers of openArchitectureWare and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     committers of openArchitectureWare - initial API and implementation
 *******************************************************************************/
package org.eclipse.xtend.shared.ui.editor;

import java.util.Iterator;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.SimpleMarkerAnnotation;
import org.eclipse.xtend.shared.ui.Activator;
import org.eclipse.xtend.shared.ui.Messages;

public class EnableDisableBreakpointAction extends Action {

	private final TextEditor editor;

	private final BreakpointActionGroup group;

	/**
	 * Enable/Disable breakpoint action that can be used both at vertical ruler and editor context menu.
	 * 
	 */
	protected EnableDisableBreakpointAction(final TextEditor editor, final BreakpointActionGroup group) {
		this.editor = editor;
		this.group = group;
		setToolTipText(Messages.EnableDisableBreakpointAction_Description);
	}

	public void updateText() {
		fBreakpoint = getBreakpoint();
		setEnabled(fBreakpoint != null);
		if (fBreakpoint != null) {
			try {
				if (fBreakpoint.isEnabled()) {
					setText(Messages.EnableDisableBreakpointAction_DisableAction);
				} else {
					setText(Messages.EnableDisableBreakpointAction_EnableAction);
				}
			} catch (CoreException e) {
			}
		} else {
			setText(Messages.EnableDisableBreakpointAction_DisableAction);
		}
	}

	private IBreakpoint fBreakpoint;

	@Override
	public void run() {
		if (fBreakpoint != null) {
			try {
				fBreakpoint.setEnabled(!fBreakpoint.isEnabled());
			} catch (CoreException e) {
				Activator.logError(Messages.EnableDisableBreakpointAction_Error, e);
			}
		}
	}

	protected IBreakpoint getBreakpoint() {
		boolean isRulerSelected = group.isRulerSelected();
		int selectedLine = group.getLastSelectedLine();
		int selectedOffset = group.getLastSelectedOffset();
		IAnnotationModel annotationModel = editor.getDocumentProvider().getAnnotationModel(
				editor.getEditorInput());
		IDocument document = editor.getDocumentProvider().getDocument(editor.getEditorInput());
		if (annotationModel != null) {
			Iterator<?> iterator = annotationModel.getAnnotationIterator();
			while (iterator.hasNext()) {
				Object object = iterator.next();
				if (object instanceof SimpleMarkerAnnotation) {
					SimpleMarkerAnnotation markerAnnotation = (SimpleMarkerAnnotation) object;
					IMarker marker = markerAnnotation.getMarker();
					try {
						if (marker.isSubtypeOf(IBreakpoint.BREAKPOINT_MARKER)) {
							Position position = annotationModel.getPosition(markerAnnotation);
							int line = document.getLineOfOffset(position.offset);
							if ((isRulerSelected && (line == selectedLine)) || (!isRulerSelected
									&& (selectedOffset >= position.offset)
									&& (selectedOffset <= position.offset + position.length))) {
								IBreakpoint breakpoint = DebugPlugin.getDefault().getBreakpointManager()
										.getBreakpoint(marker);
								if (breakpoint != null) {
									return breakpoint;
								}
							}
						}
					} catch (CoreException e) {
					} catch (BadLocationException e) {
					}
				}
			}
		}
		return null;
	}
}
