/**
 * <copyright>
 * </copyright>
 *
 * $Id: ExtensionNodeValidator.java,v 1.3 2008/03/07 09:39:39 jkohnlein Exp $
 */
package org.eclipse.xpand3.internal.parser.xpand3node.validation;

import org.eclipse.tmf.common.node.CompositeNode;
import org.eclipse.tmf.common.node.LeafNode;

/**
 * A sample validator interface for {@link org.eclipse.xpand3.internal.parser.xpand3node.ExtensionNode}.
 * This doesn't really do anything, and it's not a real EMF artifact.
 * It was generated by the org.eclipse.emf.examples.generator.validator plug-in to illustrate how EMF's code generator can be extended.
 * This can be disabled with -vmargs -Dorg.eclipse.emf.examples.generator.validator=false.
 */
public interface ExtensionNodeValidator {
	boolean validate();

	boolean validateCreate(LeafNode value);
	boolean validatePrivate(LeafNode value);
	boolean validateParamList(CompositeNode value);
	boolean validateExtendBody(CompositeNode value);
	boolean validateCached(LeafNode value);
	boolean validateJavaReturnType(CompositeNode value);
	boolean validateJavaName(CompositeNode value);
	boolean validateReturnType(CompositeNode value);
	boolean validateName(CompositeNode value);
}