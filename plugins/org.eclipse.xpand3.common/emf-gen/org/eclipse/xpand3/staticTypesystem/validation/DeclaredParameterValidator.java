/**
 * <copyright>
 * </copyright>
 *
 * $Id: DeclaredParameterValidator.java,v 1.1 2008/03/17 14:39:19 jkohnlein Exp $
 */
package org.eclipse.xpand3.staticTypesystem.validation;

import org.eclipse.xpand3.staticTypesystem.AbstractTypeReference;

/**
 * A sample validator interface for {@link org.eclipse.xpand3.staticTypesystem.DeclaredParameter}.
 * This doesn't really do anything, and it's not a real EMF artifact.
 * It was generated by the org.eclipse.emf.examples.generator.validator plug-in to illustrate how EMF's code generator can be extended.
 * This can be disabled with -vmargs -Dorg.eclipse.emf.examples.generator.validator=false.
 */
public interface DeclaredParameterValidator {
	boolean validate();

	boolean validateType(AbstractTypeReference value);
}
