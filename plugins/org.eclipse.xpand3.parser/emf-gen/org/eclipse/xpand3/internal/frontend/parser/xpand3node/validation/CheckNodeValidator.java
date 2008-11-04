/**
 * <copyright>
 * </copyright>
 *
 * $Id: CheckNodeValidator.java,v 1.1 2008/03/17 14:39:09 jkohnlein Exp $
 */
package org.eclipse.xpand3.internal.frontend.parser.xpand3node.validation;

import org.eclipse.tmf.common.node.CompositeNode;
import org.eclipse.tmf.common.node.LeafNode;

/**
 * A sample validator interface for {@link org.eclipse.xpand3.internal.frontend.parser.xpand3node.CheckNode}.
 * This doesn't really do anything, and it's not a real EMF artifact.
 * It was generated by the org.eclipse.emf.examples.generator.validator plug-in to illustrate how EMF's code generator can be extended.
 * This can be disabled with -vmargs -Dorg.eclipse.emf.examples.generator.validator=false.
 */
public interface CheckNodeValidator {
	boolean validate();

	boolean validateType(CompositeNode value);
	boolean validateError(LeafNode value);
	boolean validateMessage(CompositeNode value);
	boolean validateIfExpression(CompositeNode value);
	boolean validateWarning(LeafNode value);
	boolean validateConstraint(CompositeNode value);
}