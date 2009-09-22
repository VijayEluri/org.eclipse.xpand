package org.eclipse.xpand.incremental;

public class IncrementalCallbackTest extends AbstractIncrementalTest {
	@Override
	public String getWorkflowFileName() {
		return "/resources/workflow/incrementalcallback.mwe";
	}
	
	@Override
	public boolean writesDiff() {
		return true;
	}
}
