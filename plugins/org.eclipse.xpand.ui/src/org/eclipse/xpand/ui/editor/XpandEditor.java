/*******************************************************************************
 * Copyright (c) 2005, 2007 committers of openArchitectureWare and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     committers of openArchitectureWare - initial API and implementation
 *******************************************************************************/

package org.eclipse.xpand.ui.editor;

import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.Vector;

import org.eclipse.internal.xpand2.XpandTokens;
import org.eclipse.jdt.ui.actions.IJavaEditorActionDefinitionIds;
import org.eclipse.jdt.ui.actions.JdtActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.source.DefaultCharacterPairMatcher;
import org.eclipse.jface.text.source.ICharacterPairMatcher;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;
import org.eclipse.xpand.ui.editor.actions.InsertTextAction;
import org.eclipse.xpand.ui.editor.outline.XpandContentOutlinePage;
import org.eclipse.xpand.ui.refactoring.actions.RefactorActionGroup;
import org.eclipse.xtend.shared.ui.editor.AbstractXtendXpandEditor;
import org.eclipse.xtend.shared.ui.editor.outlineview.AbstractExtXptContentOutlinePage;

/**
 * Template specific text editor.
 */
public class XpandEditor extends AbstractXtendXpandEditor {

    public final static String XPAND_MATCHING_BRACKETS = "xpandMatchingBrackets";
    public final static String XPAND_MATCHING_BRACKETS_COLOR= "xpandMatchingBracketsColor";


	
	private ActionGroup contextMenuRefactoringGroup;

	public XpandEditor() {
		setSourceViewerConfiguration(new XpandSourceViewerConfiguration(this, getPreferenceStore()));
		setDocumentProvider(new XpandDocumentProvider());
	}

	@Override
	protected AbstractExtXptContentOutlinePage createOutlinePage() {
		return new XpandContentOutlinePage(this);
	}

	@Override
	protected void editorContextMenuAboutToShow(final IMenuManager menu) {
		super.editorContextMenuAboutToShow(menu);

		final ActionContext context = new ActionContext(getSelectionProvider().getSelection());
		contextMenuRefactoringGroup.setContext(context);
		contextMenuRefactoringGroup.fillContextMenu(menu);
		contextMenuRefactoringGroup.setContext(null);
	}

	@Override
	protected void createActions() {
		super.createActions();
		final ResourceBundle rb = new ResourceBundle() {

			@SuppressWarnings("unchecked")
			@Override
			public Enumeration getKeys() {
				return new Vector().elements();
			}

			@Override
			protected Object handleGetObject(final String key) {
				return null;
			}
		};
		IAction a = new InsertTextAction(rb, this, getSourceViewer(), XpandTokens.LT);
		a.setActionDefinitionId(IXpandEditorActionDefinitionIds.INSERT_LT);
		setAction("InsertLT", a);

		a = new InsertTextAction(rb, this, getSourceViewer(), XpandTokens.RT);
		a.setActionDefinitionId(IXpandEditorActionDefinitionIds.INSERT_RT);
		setAction("InsertRT", a);

		// refactoring
		contextMenuRefactoringGroup = new RefactorActionGroup(this, ITextEditorActionConstants.GROUP_EDIT);
		
		// hyperlinking and F3 support
		XpandOpenAction openAction = new XpandOpenAction(this);
		openAction.setActionDefinitionId(IJavaEditorActionDefinitionIds.OPEN_EDITOR);
		setAction(JdtActionConstants.OPEN, openAction);
	}

	public ActionGroup getActionGroup() {
		return contextMenuRefactoringGroup;
	}

	@Override
	protected void initializeKeyBindingScopes() {
		setKeyBindingScopes(new String[] { "org.eclipse.xpand.ui.XpandEditorScope" }); //$NON-NLS-1$
	}

	@Override
	protected void configureSourceViewerDecorationSupport(SourceViewerDecorationSupport support) {
		super.configureSourceViewerDecorationSupport(support);

		char[] matchChars = { '«', '»' };
		ICharacterPairMatcher matcher = new DefaultCharacterPairMatcher(matchChars,
				IDocumentExtension3.DEFAULT_PARTITIONING);
		support.setCharacterPairMatcher(matcher);
		support.setMatchingCharacterPainterPreferenceKeys("xpandBrackets", "xpandBracketsColor");

		//Enable bracket highlighting in the preference store
		IPreferenceStore store = getPreferenceStore();
		store.setDefault("xpandBrackets", true);
		store.setDefault("xpandBracketsColor", "192,192,192");

	}

}
